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


  statusReport(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/tickets-by-status`);
  }
  priorityReport(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/tickets-by-priority`);
  }

}
