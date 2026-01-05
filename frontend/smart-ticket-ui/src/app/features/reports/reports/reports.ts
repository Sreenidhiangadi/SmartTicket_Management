import { Component, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart } from 'chart.js/auto';
import { ReportsService } from './reports.service';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reports.html',
  styleUrls: ['./reports.scss']
})
export class ReportsComponent implements AfterViewInit {

  summary: any;

  constructor(private service: ReportsService) {}

  ngAfterViewInit(): void {
    this.loadStatusChart();
    this.loadPriorityChart();
    this.loadSlaChart();
  }


loadSummary() {
  this.service.getSummary().subscribe(res => {
    this.summary = {
      totalTickets: res.totalTickets,
      resolved: res.ticketsByStatus?.RESOLVED ?? 0,
      inProgress: res.ticketsByStatus?.IN_PROGRESS ?? 0,
      slaBreaches: res.slaBreachedTickets
    };
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
              '#0d6efd', '#ffc107', '#198754', '#6c757d', '#dc3545'
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

  loadSlaChart() {
    this.service.slaTrend().subscribe(data => {
      new Chart('slaChart', {
        type: 'line',
        data: {
          labels: data.map(d => d.date),
          datasets: [{
            label: 'SLA Breaches',
            data: data.map(d => d.count),
            borderColor: '#dc3545',
            backgroundColor: 'rgba(220,53,69,0.15)',
            tension: 0.4,
            fill: true
          }]
        },
        options: {
          plugins: { legend: { display: false } },
          animation: { duration: 900 }
        }
      });
    });
  }
}
