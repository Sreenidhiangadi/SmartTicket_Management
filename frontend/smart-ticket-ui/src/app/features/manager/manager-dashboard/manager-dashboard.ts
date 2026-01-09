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
  tickets: any[] = [];

  statusChart?: Chart;
  priorityChart?: Chart;
  ticketsOverTimeChart?: Chart;

  constructor(
    private service: ManagerDashboardService,
    private cdr: ChangeDetectorRef
  ) {}

  ngAfterViewInit(): void {
    this.loadSummary();
    this.loadStatusChart();
    this.loadPriorityChart();
    this.loadTicketsOverTime();
  }

  loadSummary(): void {
    this.service.getSummary().subscribe(res => {
      this.summary = res;
      this.cdr.detectChanges();
    });
  }

  loadStatusChart(): void {
    this.service.ticketsByStatus().subscribe(data => {
      this.statusChart?.destroy();

      this.statusChart = new Chart('statusChart', {
        type: 'doughnut',
        data: {
          labels: data.map(d => d.status),
          datasets: [{
            data: data.map(d => d.count)
          }]
        },
        options: {
          responsive: true,
          plugins: {
            legend: { position: 'bottom' }
          }
        }
      });
    });
  }

  loadPriorityChart(): void {
    this.service.ticketsByPriority().subscribe(data => {
      this.priorityChart?.destroy();

      this.priorityChart = new Chart('priorityChart', {
        type: 'bar',
        data: {
          labels: data.map(d => d.priority),
          datasets: [{
            label: 'Tickets',
            data: data.map(d => d.count)
          }]
        },
        options: {
          responsive: true,
          scales: {
            y: { beginAtZero: true }
          },
          plugins: {
            legend: { display: false }
          }
        }
      });
    });
  }

loadTicketsOverTime(): void {
  this.service.getAllTickets().subscribe({
    next: res => {
      this.tickets = res;

      if (!this.tickets.length) {
        console.warn('No tickets found');
        return;
      }

      this.renderTicketsOverTime();
    },
    error: err => {
      console.error('Failed to load tickets', err);
    }
  });
}


  renderTicketsOverTime(): void {
    this.ticketsOverTimeChart?.destroy();

    const grouped = this.groupTicketsByDate();

    this.ticketsOverTimeChart = new Chart('ticketsOverTimeChart', {
      type: 'line',
      data: {
        labels: Object.keys(grouped),
        datasets: [{
          label: 'Tickets Created',
          data: Object.values(grouped),
          borderColor: '#0d6efd',
          backgroundColor: 'rgba(13,110,253,0.15)',
          fill: true,
          tension: 0.4,
          pointRadius: 3
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { display: false }
        },
        scales: {
          x: { grid: { display: false } },
          y: { beginAtZero: true }
        }
      }
    });
  }

  groupTicketsByDate(): Record<string, number> {
    const map: Record<string, number> = {};

    this.tickets.forEach(t => {
      const date = new Date(t.createdAt).toLocaleDateString();
      map[date] = (map[date] || 0) + 1;
    });

    return map;
  }
}
