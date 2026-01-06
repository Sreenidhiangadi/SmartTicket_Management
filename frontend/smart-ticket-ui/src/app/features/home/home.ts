import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.html',
  styleUrls: ['./home.scss']
})
export class HomeComponent {
  constructor(
    public authService: AuthService,
    private router: Router
  ) {}

  goToHomeByRole(): void {
    if (this.authService.hasRole('ADMIN')) {
      this.router.navigate(['/admin']);
      return;
    }

    if (this.authService.hasRole('MANAGER')) {
      this.router.navigate(['/manager/dashboard']);
      return;
    }

    if (this.authService.hasRole('AGENT')) {
      this.router.navigate(['/agent/queue']);
      return;
    }

    if (this.authService.hasRole('USER')) {
      this.router.navigate(['/tickets/my']);
      return;
    }

    this.router.navigate(['/login']);
  }
}
