import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './core/navbar/navbar';
import { AuthService } from './core/auth/auth.service';
import { ToastComponent } from './core/ui/toast/toast';
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, ToastComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.scss']
})
export class AppComponent {
  constructor(private auth: AuthService) {
    this.auth.loadUserFromToken();
  }
}
