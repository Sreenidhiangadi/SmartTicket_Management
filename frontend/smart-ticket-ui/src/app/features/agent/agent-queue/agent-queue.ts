import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs/operators';

import { AgentService } from '../agent.service';
import { AuthService } from '../../../core/auth/auth.service';
import { RouterModule } from '@angular/router';

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
    private cdr: ChangeDetectorRef
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
        },
        error: () => {
          this.error = 'Failed to load ticket queue';
        }
      });
  }

  startWork(ticket: any): void {
    this.updateAndReload(() =>
      this.agentService.updateStatus(ticket.id, 'IN_PROGRESS')
    );
  }

  resolve(ticket: any): void {
    this.updateAndReload(() =>
      this.agentService.updateStatus(ticket.id, 'RESOLVED')
    );
  }

  close(ticket: any): void {
    this.updateAndReload(() =>
      this.agentService.closeTicket(ticket.id)
    );
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
