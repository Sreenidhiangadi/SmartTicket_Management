import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environment';

@Injectable({ providedIn: 'root' })
export class ManagerDashboardService {

  private baseUrl = environment.apiBaseUrl+ '/ticket-service';

  constructor(private http: HttpClient) {}

  getSummary() {
    return this.http.get<any>(`${this.baseUrl}/dashboard/summary`);
  }

  ticketsByStatus() {
    return this.http.get<any[]>(`${this.baseUrl}/reports/tickets-by-status`);
  }

  ticketsByPriority() {
    return this.http.get<any[]>(`${this.baseUrl}/reports/tickets-by-priority`);
  }
}
