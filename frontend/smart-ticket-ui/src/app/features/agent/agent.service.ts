import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environment';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AgentService {

  private baseUrl =
    environment.apiBaseUrl + '/ticket-service/tickets';

  constructor(private http: HttpClient) {}

  getMyQueue(agentId: string): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.baseUrl}/agent/${agentId}`
    );
  }

  updateStatus(ticketId: string, status: string): Observable<any> {
    return this.http.put(
      `${this.baseUrl}/${ticketId}/status`,
      { status }
    );
  }

  closeTicket(ticketId: string): Observable<any> {
    return this.http.put(
      `${this.baseUrl}/${ticketId}/close`,
      {}
    );
  }
}
