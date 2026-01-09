import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../../environment';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ManagerDashboardService {

  private baseUrl = environment.apiBaseUrl + '/ticket-service';

  constructor(private http: HttpClient) {}

  getSummary(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/dashboard/summary`);
  }

  ticketsByStatus(): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.baseUrl}/reports/tickets-by-status`
    );
  }

  ticketsByPriority(): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.baseUrl}/reports/tickets-by-priority`
    );
  }

  getAllTickets(): Observable<any[]> {
    const params = new HttpParams()
      .set('page', 0)
      .set('size', 1000);

    return this.http.get<any[]>(
      `${this.baseUrl}/tickets`,
      { params }
    );
  }
}
