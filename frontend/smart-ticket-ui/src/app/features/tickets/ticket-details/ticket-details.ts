import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { TicketService } from '../ticket.service';
import { AuthService } from '../../../core/auth/auth.service';
import { ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AgentDirectoryService } from '../../agent/agent-directory.service';
import { ToastService } from '../../../core/ui/toast/toast.service';
@Component({
  selector: 'app-ticket-details',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ticket-details.html',
  styleUrls: ['./ticket-details.scss']
})
export class TicketDetailsComponent implements OnInit {

  ticket: any;
  timeline: any[] = [];
  comments: any[] = [];
  agents: any[] = [];
  selectedAgentId = '';
  loading = true;
  error = '';
  newComment = '';
  submittingComment = false;
  constructor(
    private route: ActivatedRoute,
    private ticketService: TicketService,
    public auth: AuthService,
    public agentService: AgentDirectoryService,
    private toast: ToastService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadAll(id);
    }
    if (this.isManager()|| this.hasRole('USER')) {
      this.loadAgents();
    }

  }

  loadAll(id: string): void {
    this.loading = true;
    this.cdr.markForCheck();
    this.ticketService.getTicket(id).subscribe({
      next: ticket => {
        this.ticket = ticket;
        this.loadExtras(id);
      },
      error: () => {
        this.error = 'Failed to load ticket';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });

  }

  loadExtras(id: string): void {
    this.ticketService.getTimeline(id).subscribe(res => {
      this.timeline = res;
      this.cdr.markForCheck();
    });

    this.ticketService.getComments(id).subscribe(res => {
      this.comments = res;
      this.loading = false;
      this.cdr.markForCheck();
    });
  }
 loadAgents(): void {
    this.agentService.getActiveAgents().subscribe(res => {
      this.agents = res;
      this.cdr.markForCheck();
    });
  }

  start(): void {
    this.ticketService
      .updateStatus(this.ticket.id, 'IN_PROGRESS')
      .subscribe(() => this.loadAll(this.ticket.id));
  }

  resolve(): void {
    this.ticketService
      .updateStatus(this.ticket.id, 'RESOLVED')
      .subscribe(() => this.loadAll(this.ticket.id));
  }

  close(): void {
    this.ticketService
      .close(this.ticket.id)
      .subscribe(() => this.loadAll(this.ticket.id));
  }
  reopen(): void {
    this.ticketService
      .reopen(this.ticket.id)
      .subscribe(() => this.loadAll(this.ticket.id));
  }

assign(): void {
  if (!this.selectedAgentId) return;

  if (this.ticket.assignedTo === this.selectedAgentId) {
    this.toast.show(
      'This agent is already assigned to the ticket',
      'warning'
    );
    return;
  }

  this.ticketService
    .assignAgent(this.ticket.id, this.selectedAgentId)
    .subscribe({
      next: updatedTicket => {
        this.ticket = updatedTicket;

        const agent = this.agents.find(
          a => a.id === this.selectedAgentId
        );

        this.toast.show(
          `Ticket assigned to ${agent?.name ?? 'agent'}`
        );

        this.selectedAgentId = '';
        this.cdr.markForCheck();
      },
      error: () => {
        this.toast.show(
          'Failed to assign agent',
          'error'
        );
      }
    });
}


addComment(): void {
    if (!this.newComment.trim()) return;

    this.submittingComment = true;

    this.ticketService
      .addComment(this.ticket.id, this.newComment)
      .subscribe({
        next: () => {
          this.newComment = '';
          this.submittingComment = false;

          this.ticketService
            .getComments(this.ticket.id)
            .subscribe(res => {
              this.comments = res;
              this.cdr.markForCheck();
            });
        },
        error: () => {
          this.submittingComment = false;
          this.toast.show('Failed to add comment', 'error');

        }
      });
  }
   isManager(): boolean {
    return this.auth.hasRole('MANAGER') || this.auth.hasRole('ADMIN');
  }
  getAssignedAgentName(): string {
  const agent = this.agents.find(
    a => a.id === this.ticket?.assignedTo
  );
  return agent ? agent.name : this.ticket?.assignedTo;
}

  hasRole(role: string): boolean {
    return this.auth.hasRole(role);
  }
  priorities = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];

changePriority(newPriority: string) {
  this.ticket.priority = newPriority;

  localStorage.setItem(
    `ticket-priority-${this.ticket.id}`,
    newPriority
  );

  this.toast.show(
    `Priority changed to ${newPriority}`,
    'success'
  );
}

loadPriority() {
  const saved = localStorage.getItem(
    `ticket-priority-${this.ticket.id}`
  );
  if (saved) {
    this.ticket.priority = saved;
  }
}
rating = 0;

rateAgent(value: number) {
  this.rating = value;

  localStorage.setItem(
    `ticket-rating-${this.ticket.id}`,
    value.toString()
  );

  this.toast.show(
    `Agent rated ${value} star${value > 1 ? 's' : ''}`,
    'success'
  );
}

loadRating() {
  const saved = localStorage.getItem(
    `ticket-rating-${this.ticket.id}`
  );
  if (saved) {
    this.rating = +saved;
  }
}

}
