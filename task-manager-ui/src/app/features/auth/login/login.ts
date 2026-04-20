import { Component } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-login',
  templateUrl: './login.html',
  styleUrl: './login.css',
  standalone: false,
})
export class Login {
  email = '';
  password = '';
  isLoading: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  onLogin() {
    if (this.isLoading) return;
    this.isLoading = true;

    const data = {
      email: this.email,
      password: this.password,
    };

    this.authService.login(data).subscribe({
      next: (res) => {
        console.log('Login success:', res);
        this.isLoading = false;
        this.router.navigate(['/tasks']);
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  goToRegister() {
    this.router.navigate(['/auth/register']);
  }
}
