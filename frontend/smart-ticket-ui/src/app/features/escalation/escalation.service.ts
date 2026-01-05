import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environment';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class EscalationService {

  private baseUrl =
    environment.apiBaseUrl + '/assignment-service/api/assign';

  constructor(private http: HttpClient) {}

  getMyEscalations(managerId: string): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.baseUrl}/escalations/manager/${managerId}`
    );
  }
}
