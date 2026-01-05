import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-ticket-filters',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="d-flex gap-2 mb-3">

      <select class="form-select w-auto" [(ngModel)]="status">
        <option value="">All Status</option>
        <option *ngFor="let s of statuses" [value]="s">
          {{ s }}
        </option>
      </select>

      <select class="form-select w-auto" [(ngModel)]="priority">
        <option value="">All Priority</option>
        <option *ngFor="let p of priorities" [value]="p">
          {{ p }}
        </option>
      </select>

      <button class="btn btn-outline-primary" (click)="apply()">
        Apply
      </button>

    </div>
  `
})
export class TicketFiltersComponent {
  statuses = ['OPEN', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'];
  priorities = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];

  status = '';
  priority = '';

  @Output() filterChange = new EventEmitter<{
    status?: string;
    priority?: string;
  }>();

  apply(): void {
    this.filterChange.emit({
      status: this.status || undefined,
      priority: this.priority || undefined
    });
  }
}
