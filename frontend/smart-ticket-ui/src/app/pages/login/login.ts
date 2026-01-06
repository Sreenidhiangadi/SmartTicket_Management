import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  Validators,
  FormGroup,
  ReactiveFormsModule
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class LoginComponent {
  loading = false;
  errorMessage = '';
  loginForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.cdr.detectChanges();

    const { email, password } = this.loginForm.value;

    this.authService.login({ email, password }).subscribe({
      next: () => {
        this.loading = false;
        this.cdr.detectChanges();

        if (this.authService.hasRole('ADMIN')) {
          this.router.navigate(['/admin']);
        } else if (this.authService.hasRole('MANAGER')) {
          this.router.navigate(['/manager/dashboard']);
        } else if (this.authService.hasRole('AGENT')) {
          this.router.navigate(['/agent/queue']);
        } else {
          this.router.navigate(['/']);
        }
      },
      error: err => {
        this.loading = false;
        this.errorMessage =
          err?.error?.message ||
          (typeof err?.error === 'string' ? err.error : 'Login failed.');
        this.cdr.detectChanges();
      }
    });
  }
}
