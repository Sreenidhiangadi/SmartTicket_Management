import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environment';

@Injectable({ providedIn: 'root' })
export class SlaService {

  private baseUrl =
    environment.apiBaseUrl + '/assignment-service/api/sla';

  constructor(private http: HttpClient) {}

checkSla(dryRun = true): Observable<any[]> {
  return this.http.post<any[]>(
    `${this.baseUrl}/check`,
    { dryRun },
    {
      withCredentials: true
    }
  );
}

  getAgentWorkload(): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.baseUrl}/workload`
    );
  }
}
