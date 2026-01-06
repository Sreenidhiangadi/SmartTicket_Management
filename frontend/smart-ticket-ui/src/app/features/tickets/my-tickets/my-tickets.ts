import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { TicketService } from '../ticket.service';
import { AuthService } from '../../../core/auth/auth.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-my-tickets',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './my-tickets.html',
  styleUrls: ['./my-tickets.scss']
})
export class MyTicketsComponent implements OnInit {

  tickets: any[] = [];
  loading = false;
  error = '';
  filteredTickets: any[] = [];

page = 0;
size = 10;

statusFilter = '';
priorityFilter = '';
  constructor(
    private ticketService: TicketService,
    private auth: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadTickets();
  }
applyFilters(): void {
  this.filteredTickets = this.tickets.filter(t => {
    const statusMatch =
      !this.statusFilter || t.status === this.statusFilter;

    const priorityMatch =
      !this.priorityFilter || t.priority === this.priorityFilter;

    return statusMatch && priorityMatch;
  });
}

clearFilters(): void {
  this.statusFilter = '';
  this.priorityFilter = '';
  this.filteredTickets = [...this.tickets];
}

  loadTickets(): void {
    const userId = this.auth.getUserId();
    if (!userId) return;

    this.loading = true;
    this.error = '';

    this.ticketService
    .getMyTickets(userId)
    .subscribe({
      next: res => {
        this.tickets = res;
        this.applyFilters();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'Failed to load tickets';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
  
}
