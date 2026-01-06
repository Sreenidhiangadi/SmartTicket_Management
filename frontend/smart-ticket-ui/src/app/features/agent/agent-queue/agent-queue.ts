import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs/operators';
import { AgentService } from '../agent.service';
import { AuthService } from '../../../core/auth/auth.service';
import { RouterModule } from '@angular/router';
import { ToastService } from '../../../core/ui/toast/toast.service';
import { TicketService } from '../../tickets/ticket.service';

@Component({
  selector: 'app-agent-queue',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './agent-queue.html',
  styleUrls: ['./agent-queue.scss']
})
export class AgentQueueComponent implements OnInit {

  tickets: any[] = [];
  loading = false;
  error = '';

  constructor(
    private agentService: AgentService,
    private auth: AuthService,
    private cdr: ChangeDetectorRef,
    private toast: ToastService,
    private ticketService: TicketService
  ) {}

  ngOnInit(): void {
    this.loadQueue();
  }

  loadQueue(): void {
    const agentId = this.auth.getUserId();
    if (!agentId) return;

    this.loading = true;
    this.error = '';

    this.agentService
      .getMyQueue(agentId)
      .pipe(
        finalize(() => {
          this.loading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: res => {
          this.tickets = res;
          this.cdr.detectChanges();
        },
        error: () => {
          this.error = 'Failed to load ticket queue';
          this.cdr.detectChanges();
        }
      });
  }

startWork(ticket: any): void {
  this.ticketService.updateStatus(ticket.id, 'IN_PROGRESS').subscribe({
    next: () => {
      this.toast.show('Ticket marked as In Progress');
      this.loadQueue();
    },
    error: () => {
      this.toast.show('Failed to start ticket', 'error');
    }
  });
}

resolve(ticket: any): void {
  this.ticketService.updateStatus(ticket.id, 'RESOLVED').subscribe({
    next: () => {
      this.toast.show('Ticket resolved successfully');
      this.loadQueue();
    },
    error: () => {
      this.toast.show('Failed to resolve ticket', 'error');
    }
  });
}

close(ticket: any): void {
  this.ticketService.close(ticket.id).subscribe({
    next: () => {
      this.toast.show('Ticket closed');
      this.loadQueue();
    },
    error: () => {
      this.toast.show('Failed to close ticket', 'error');
    }
  });
}


  private updateAndReload(action: () => any): void {
    this.loading = true;

    action()
      .pipe(
        finalize(() => {
          this.loading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe(() => this.loadQueue());
  }
}
