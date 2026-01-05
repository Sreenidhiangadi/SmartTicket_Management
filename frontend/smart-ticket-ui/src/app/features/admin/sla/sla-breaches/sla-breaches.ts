import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SlaService } from '../sla.service';
import { ChangeDetectorRef } from '@angular/core';
@Component({
  selector: 'app-sla-breaches',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sla-breaches.html',
  styleUrls: ['./sla-breaches.scss']
})
export class SlaBreachesComponent implements OnInit {

  breaches: any[] = [];
  loading = false;
  error = '';

  constructor(private slaService: SlaService, private cdRef: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.runCheck();
  }

  runCheck(): void {
    this.loading = true;
    this.error = '';
    this.cdRef.markForCheck();

    this.slaService.checkSla(false).subscribe({
      next: res => {
        this.breaches = res.filter(r => r.breached);
        this.loading = false;
        this.cdRef.markForCheck();
      },
      error: () => {
        this.error = 'Failed to check SLA breaches';
        this.loading = false;
        this.cdRef.markForCheck();
      }
    });
  }
}
