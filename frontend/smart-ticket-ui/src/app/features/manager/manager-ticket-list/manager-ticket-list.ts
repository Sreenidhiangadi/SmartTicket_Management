import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ManagerTicketService } from '../manager-ticket.service';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ChangeDetectorRef } from '@angular/core';
@Component({
  selector: 'app-manager-ticket-list',
  standalone: true,
  imports: [CommonModule, RouterModule,FormsModule],
  templateUrl: './manager-ticket-list.html',
  styleUrls: ['./manager-ticket-list.scss']
})
export class ManagerTicketListComponent implements OnInit {

  tickets: any[] = [];
  loading = false;
  error = '';

  status = '';
  priority = '';

  page = 0;
  size = 10;

  statuses = ['OPEN', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'];
  priorities = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];

  constructor(private ticketService: ManagerTicketService, private cdRef: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadTickets();

  }

  loadTickets(): void {
    this.loading = true;
    this.error = '';
    this.cdRef.markForCheck();

    this.ticketService
      .getTickets(this.page, this.size, this.status, this.priority)
      .subscribe({
        next: res => {
          this.tickets = res;
          this.loading = false;
          this.cdRef.markForCheck();
        },
        error: () => {
          this.error = 'Failed to load tickets';
          this.loading = false;
          this.cdRef.markForCheck();
        }
      });
  }

  applyFilters(): void {
    this.page = 0;
    this.loadTickets();
  }

  resetFilters(): void {
    this.status = '';
    this.priority = '';
    this.page = 0;
    this.loadTickets();
  }

  nextPage(): void {
    this.page++;
    this.loadTickets();
  }

  prevPage(): void {
    if (this.page === 0) return;
    this.page--;
    this.loadTickets();
  }
}
