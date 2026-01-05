import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environment';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AgentDirectoryService {

  private baseUrl =
    environment.apiBaseUrl + '/user-service/users';

  constructor(private http: HttpClient) {}

  getActiveAgents(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/agents`);
  }
}
