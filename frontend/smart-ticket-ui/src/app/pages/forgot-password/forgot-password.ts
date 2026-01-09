import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './forgot-password.html'
})
export class ForgotPasswordComponent {

  form!: FormGroup;
  loading = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService
  ) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = null;

    this.authService.forgotPassword(this.form.value.email!)
      .subscribe({
        next: () => {
          this.loading = false;
          this.successMessage =
            'If an account exists, a reset link has been sent to your email.';
        },
        error: () => {
          this.loading = false;
          this.successMessage =
            'If an account exists, a reset link has been sent to your email.';
        }
      });
  }
}
