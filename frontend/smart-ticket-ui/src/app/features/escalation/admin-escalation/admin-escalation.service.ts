import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environment';

@Injectable({ providedIn: 'root' })
export class AdminEscalationService {

  private baseUrl =
    environment.apiBaseUrl + '/assignment-service/api/assign';

  constructor(private http: HttpClient) {}

  getAllEscalations(): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.baseUrl}/escalations`
    );
  }
}
