import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environment';
import { Observable } from 'rxjs';
import { Ticket } from './ticket.model';

@Injectable({ providedIn: 'root' })
export class TicketService {

  private baseUrl =
    environment.apiBaseUrl + '/ticket-service/tickets';

  constructor(private http: HttpClient) {}
createTicket(payload: {
  title: string;
  description: string;
  category: string;
  priority: string;
}) {
    return this.http.post<Ticket>(
      `${this.baseUrl}`,
      payload
    );
}

getTicket(id: string): Observable<Ticket> {
  return this.http.get<Ticket>(`${this.baseUrl}/${id}`);
}

getMyTickets(userId: string) {
  return this.http.get<any[]>(
    `${this.baseUrl}/user/${userId}`
  );
}

  getTimeline(id: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/${id}/timeline`);
  }

  getComments(id: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/${id}/comments`);
  }

  addComment(id: string, comment: string): Observable<any> {
  return this.http.post(`${this.baseUrl}/${id}/comments`, {
   comment
    });
  }
  assignAgent(ticketId: string, agentId: string) {
    return this.http.put(
      `${this.baseUrl}/${ticketId}/assign`,
      null,
      { params: { agentId } }
    );
  }
  updateStatus(id: string, status: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}/status`, { status });
  }

  close(id: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}/close`, {});
  }

  reopen(id: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}/reopen`, {});
  }

  cancel(id: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}/cancel`, {});
  }
}
