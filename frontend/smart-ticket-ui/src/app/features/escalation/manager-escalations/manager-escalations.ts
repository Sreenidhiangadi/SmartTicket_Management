import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { EscalationService } from '../escalation.service';
import { AuthService } from '../../../core/auth/auth.service';
import { ChangeDetectorRef } from '@angular/core';
@Component({
  selector: 'app-manager-escalations',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './manager-escalations.html',
  styleUrls: ['./manager-escalations.scss']
})
export class ManagerEscalationsComponent implements OnInit {

  escalations: any[] = [];
  loading = false;
  error = '';

  constructor(
    private escalationService: EscalationService,
    private auth: AuthService,
    private cdRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const managerId = this.auth.getUserId();
    if (!managerId) return;

    this.loading = true;
    this.cdRef.detectChanges();

    this.escalationService.getMyEscalations(managerId).subscribe({
      next: res => {
        this.escalations = res;
        this.loading = false;
        this.cdRef.markForCheck();
      },
      error: () => {
        this.error = 'Failed to load escalations';
        this.loading = false;
        this.cdRef.markForCheck();
      }
    });
  }
}
