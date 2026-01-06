import { Component, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from './notification.service';
import { Notification } from '../../shared/models/notification.model';
import { ChangeDetectorRef } from '@angular/core';
@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification.html',
  styleUrls: ['./notification.scss']
})
export class NotificationsComponent implements OnInit {

  @Input() mode: 'navbar' | 'profile' = 'navbar';

  notifications: Notification[] = [];
  loading = false;

  constructor(private notificationService: NotificationService, private cdRef: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.loading = true;

    this.notificationService.getMyNotifications().subscribe({
      next: res => {
        this.notifications = res;
        this.loading = false;
        this.cdRef.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdRef.detectChanges();
      }
    });
  }

  get unreadCount(): number {
    return this.notifications.filter(n => !n.read).length;
  }

  iconFor(type: string): string {
    switch (type) {
      case 'TICKET_CREATED': return 'bi-plus-circle';
      case 'TICKET_ASSIGNED': return 'bi-person-check';
      case 'STATUS_CHANGED': return 'bi-arrow-repeat';
      case 'SLA_BREACH': return 'bi-alarm';
      case 'ESCALATION': return 'bi-exclamation-triangle';
      default: return 'bi-bell';
    }
  }
}
