import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environment';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AdminService {

  private baseUrl =
    environment.apiBaseUrl + '/user-service/users/admin';

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<any[]> {
    return this.http.get<any[]>(this.baseUrl);
  }

  updateUser(
    userId: string,
    payload: { roles?: string[]; active?: boolean }
  ): Observable<any> {
    return this.http.patch(`${this.baseUrl}/${userId}`, payload);
  }
}
