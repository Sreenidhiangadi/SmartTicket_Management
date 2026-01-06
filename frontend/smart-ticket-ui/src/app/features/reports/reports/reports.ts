import { Component, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart } from 'chart.js/auto';
import { ReportsService } from './reports.service';
import { RouterModule } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';
@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule,RouterModule],
  templateUrl: './reports.html',
  styleUrls: ['./reports.scss']
})
export class ReportsComponent implements AfterViewInit {

  summary: any;

  constructor(private service: ReportsService, private cdRef: ChangeDetectorRef) {}

  ngAfterViewInit(): void {
    this.loadSummary();
    this.loadStatusChart();
    this.loadPriorityChart();
  }

loadSummary() {
  this.service.getSummary().subscribe(res => {
    this.summary = {
      totalTickets: res.totalTickets ?? 0,
      resolved: res.resolved ?? 0,
      inProgress: res.inProgress ?? 0
    };
    this.cdRef.detectChanges();
  });
}


  loadStatusChart() {
    this.service.statusReport().subscribe(data => {
      new Chart('statusChart', {
        type: 'doughnut',
        data: {
          labels: data.map(d => d.status),
          datasets: [{
            data: data.map(d => d.count),
            backgroundColor: [
              '#0d6efd',
              '#ffc107',
              '#198754',
              '#6c757d',
              '#dc3545'
            ]
          }]
        },
        options: {
          cutout: '70%',
          plugins: { legend: { position: 'bottom' } },
          animation: { duration: 900 }
        }
      });
    });
  }


  loadPriorityChart() {
    this.service.priorityReport().subscribe(data => {
      new Chart('priorityChart', {
        type: 'bar',
        data: {
          labels: data.map(d => d.priority),
          datasets: [{
            data: data.map(d => d.count),
            backgroundColor: ['#198754', '#0dcaf0', '#ffc107', '#dc3545'],
            borderRadius: 8
          }]
        },
        options: {
          plugins: { legend: { display: false } },
          animation: { duration: 800 }
        }
      });
    });
  }
}
