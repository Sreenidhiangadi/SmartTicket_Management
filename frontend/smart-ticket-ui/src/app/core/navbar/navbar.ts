import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.scss']
})
export class NavbarComponent {

  constructor(
    public auth: AuthService,
    private router: Router
  ) {}

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }

  hasRole(role: string): boolean {
    return this.auth.hasRole(role);
  }

  goHome(): void {
    if (this.hasRole('ADMIN')) {
      this.router.navigate(['/admin']);
    } else if (this.hasRole('MANAGER')) {
      this.router.navigate(['/manager/dashboard']);
    } else if (this.hasRole('AGENT')) {
      this.router.navigate(['/agent/queue']);
    } else if (this.hasRole('USER')) {
      this.router.navigate(['/']);
    } else {
      this.router.navigate(['/']);
    }
  }
}
