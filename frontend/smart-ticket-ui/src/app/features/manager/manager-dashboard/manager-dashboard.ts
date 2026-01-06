import { Component, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ManagerDashboardService } from './manager-dashboard.service';
import { Chart } from 'chart.js/auto';
import { ChangeDetectorRef } from '@angular/core';
@Component({
  selector: 'app-manager-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './manager-dashboard.html',
  styleUrls: ['./manager-dashboard.scss']
})
export class ManagerDashboardComponent implements AfterViewInit {

  summary: any;

  constructor(private service: ManagerDashboardService, private cdr: ChangeDetectorRef) {}

  ngAfterViewInit(): void {
    this.loadSummary();
    this.loadStatusChart();
    this.loadPriorityChart();
  }

loadSummary(): void {
    this.service.getSummary().subscribe({
      next: res => {
        this.summary = res;
        this.cdr.detectChanges();
      }
    });
  }

loadStatusChart() {
  this.service.ticketsByStatus().subscribe(data => {
    new Chart('statusChart', {
      type: 'doughnut',
      data: {
        labels: data.map(d => d.status),
        datasets: [
          {
            data: data.map(d => d.count)
          }
        ]
      },
      options: {
        responsive: true,
        animation: {
          duration: 1200,
          easing: 'easeOutQuart'
        },
        plugins: {
          legend: {
            position: 'bottom'
          }
        }
      }
    });
  });
}
loadPriorityChart() {
  this.service.ticketsByPriority().subscribe(data => {
    new Chart('priorityChart', {
      type: 'bar',
      data: {
        labels: data.map(d => d.priority),
        datasets: [
          {
            label: 'Tickets',
            data: data.map(d => d.count)
          }
        ]
      },
      options: {
        responsive: true,
        animation: {
          duration: 1000,
          easing: 'easeOutCubic'
        },
        scales: {
          y: {
            beginAtZero: true
          }
        },
        plugins: {
          legend: {
            display: false
          }
        }
      }
    });
  });
}

}
