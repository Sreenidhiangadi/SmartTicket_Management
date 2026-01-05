import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AdminEscalationService } from './admin-escalation.service';
import { ChangeDetectorRef } from '@angular/core';
@Component({
  selector: 'app-admin-escalations',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-escalation.html',
  styleUrls: ['./admin-escalation.scss']
})
export class AdminEscalationsComponent implements OnInit {

  escalations: any[] = [];
  loading = false;
  error = '';

  constructor(private escalationService: AdminEscalationService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadEscalations();
    
  }

  loadEscalations(): void {
    this.loading = true;
    this.error = '';
    this.cdr.markForCheck();

    this.escalationService.getAllEscalations().subscribe({
      next: res => {
        this.escalations = res;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.error = 'Failed to load escalations';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }
}
