import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ReactiveFormsModule,FormBuilder,Validators,FormGroup} from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { ChangeDetectorRef } from '@angular/core';
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
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

  submit(): void {
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
  if (err?.error && typeof err.error === 'object' && err.error.message) {
    this.errorMessage = err.error.message;
    return;
  }
  if (typeof err?.error === 'string') {
    try {
      const parsed = JSON.parse(err.error);
      if (parsed.message) {
        this.errorMessage = parsed.message;
        return;
      }
      this.errorMessage = err.error;
      return;
    } catch {
      this.errorMessage = err.error;
      return;
    }
  }
  this.errorMessage = 'Login failed.';
  this.cdr.detectChanges();
}

    });
  }
}
