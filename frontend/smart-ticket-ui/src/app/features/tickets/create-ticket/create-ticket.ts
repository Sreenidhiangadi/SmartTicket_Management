import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormBuilder,FormGroup,Validators,ReactiveFormsModule} from '@angular/forms';
import { Router } from '@angular/router';
import { TicketService } from '../ticket.service';
import { ToastService } from '../../../core/ui/toast/toast.service';

@Component({
  selector: 'app-create-ticket',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-ticket.html',
  styleUrls: ['./create-ticket.scss']
})
export class CreateTicketComponent {

  form: FormGroup;
  loading = false;
  error = '';

  categories = [
    'HARDWARE',
    'SOFTWARE',
    'NETWORK',
    'ACCESS',
    'OTHER'
  ];

  priorities = [
    'LOW',
    'MEDIUM',
    'HIGH',
    'CRITICAL'
  ];

  constructor(
    private fb: FormBuilder,
    private ticketService: TicketService,
    private router: Router,
    private toast: ToastService
  ) {
    this.form = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(5)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      category: ['', Validators.required],
      priority: ['', Validators.required]
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.error = '';

    this.ticketService.createTicket(this.form.value).subscribe({
      next: (ticket) => {
        this.loading = false;
        this.toast.show('Ticket created successfully');
        this.router.navigate(['/tickets', ticket.id]);
      },
      error: err => {
        this.loading = false;
        this.error =
          err?.error?.message ?? 'Failed to create ticket';
          this.toast.show('Failed to create ticket', 'error');
      }
    });
  }
}
