import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormBuilder,FormGroup,Validators,ReactiveFormsModule} from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register.html',
  styleUrls: ['./register.scss']
})
export class RegisterComponent {

  registerForm: FormGroup;
  loading = false;
  errorMessage: string | null = null;
  backendErrors: Record<string, string> | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(5)]]
    });
  }

  submit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.backendErrors = null;

    this.authService.register(this.registerForm.value).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/login']);
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
  this.errorMessage = 'Registration failed.';
}
    });
  }
}
