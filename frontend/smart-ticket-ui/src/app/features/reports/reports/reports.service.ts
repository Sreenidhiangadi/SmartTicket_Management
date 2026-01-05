import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environment';

@Injectable({ providedIn: 'root' })
export class ReportsService {

  private baseUrl =
    environment.apiBaseUrl + '/ticket-service/reports';

  constructor(private http: HttpClient) {}
    getSummary(): Observable<any> {
        return this.http.get<any>(`${this.baseUrl}/summary`);}

  // Doughnut chart
  statusReport(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/tickets-by-status`);
  }

  // Bar chart
  priorityReport(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/tickets-by-priority`);
  }

  // Line chart
  slaTrend(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/sla-trend`);
  }
}
