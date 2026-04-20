import { Component } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: false,
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile {
  name: string | null = null;
  email: string | null = null;
  newName: string = '';
  oldPassword: string = '';
  newPassword: string = '';

  constructor(private authService: AuthService) {
    this.name = localStorage.getItem('name');
    this.email = localStorage.getItem('email');
  }

  updateName() {
    this.authService.updateName({ name: this.newName }).subscribe({
      next: (res: any) => {
        console.log('Updated:', res);
        localStorage.setItem('name', res.name);
        this.name = res.name;
        this.newName = '';
      },
      error: (err) => console.error(err),
    });
  }

  changePassword() {
    this.authService
      .changePassword({
        oldPassword: this.oldPassword,
        newPassword: this.newPassword,
      })
      .subscribe({
        next: () => {
          console.log('Password updated');
          this.oldPassword = '';
          this.newPassword = '';
        },
        error: (err) => console.error(err),
      });
  }

  goBack() {
    window.history.back();
  }
}
