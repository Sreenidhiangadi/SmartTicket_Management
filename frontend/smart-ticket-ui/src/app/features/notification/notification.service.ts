import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environment';
import { Notification } from '../../shared/models/notification.model';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private readonly baseUrl =
    `${environment.apiBaseUrl}/notification-service/notifications`;

  constructor(private http: HttpClient) {}

  getMyNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(this.baseUrl);
  }
}
