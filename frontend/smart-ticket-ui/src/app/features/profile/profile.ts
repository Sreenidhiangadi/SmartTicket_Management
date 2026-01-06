import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environment';
import { NotificationsComponent } from '../notification/notification';
import { ChangeDetectorRef } from '@angular/core';
@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, NotificationsComponent],
  templateUrl: './profile.html',
  styleUrls: ['./profile.scss']
})
export class ProfileComponent implements OnInit {

  private readonly baseUrl =
    `${environment.apiBaseUrl}/user-service/auth/me`;

  loading = true;
  errorMessage: string | null = null;

  showNotifications = false;

  user: {
    id: string;
    name: string;
    email: string;
    roles: string[];
    active: boolean;
  } | null = null;

  @ViewChild(NotificationsComponent)
  notificationsComponent!: NotificationsComponent;

  constructor(private http: HttpClient, private cdRef: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.http.get<any>(this.baseUrl).subscribe({
      next: res => {
        this.user = res;
        this.loading = false;
        this.cdRef.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Failed to load profile';
        this.loading = false;
        this.cdRef.detectChanges();
      }
    });
  }

  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
  }
  get canViewNotifications(): boolean {
  if (!this.user) return false;

  return this.user.roles.includes('USER')
      || this.user.roles.includes('AGENT');
}

}
