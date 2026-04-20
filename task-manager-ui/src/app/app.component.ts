import { Component } from '@angular/core';
import { ErrorService } from './core/services/error.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: false,
  styleUrl: './app.component.css',
})
export class AppComponent {
  error$;
  theme: 'light' | 'dark' = 'light';

  constructor(private errorService: ErrorService) {
    this.error$ = this.errorService.error$;
  }

  ngOnInit() {
    const savedTheme = localStorage.getItem('theme') as 'light' | 'dark';

    if (savedTheme) {
      this.theme = savedTheme;
      document.body.classList.add(this.theme);
    }
  }

  clearError() {
    this.errorService.clearError();
  }

  toggleTheme() {
    this.theme = this.theme === 'light' ? 'dark' : 'light';

    document.body.classList.remove('light', 'dark');
    document.body.classList.add(this.theme);

    localStorage.setItem('theme', this.theme);
  }
}
