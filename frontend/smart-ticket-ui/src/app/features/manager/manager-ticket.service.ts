import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environment';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ManagerTicketService {

  private baseUrl =
    environment.apiBaseUrl + '/ticket-service/tickets';

  constructor(private http: HttpClient) {}

  getTickets(
    page: number,
    size: number,
    status?: string,
    priority?: string
  ): Observable<any[]> {

    let params = new HttpParams()
      .set('page', page)
      .set('size', size);

    if (status) {
      params = params.set('status', status);
    }

    if (priority) {
      params = params.set('priority', priority);
    }

    return this.http.get<any[]>(this.baseUrl, { params });
  }
}
