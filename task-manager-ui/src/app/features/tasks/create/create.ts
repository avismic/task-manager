import { Component } from '@angular/core';
import { TaskService } from '../../../core/services/task.service';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-create',
  templateUrl: './create.html',
  styleUrl: './create.css',
  standalone: false,
})
export class Create {
  title = '';
  description = '';
  isLoading: boolean = false;

  constructor(
    private taskService: TaskService,
    private router: Router,
  ) {}

  onSubmit() {
    if (this.isLoading) return;

    this.isLoading = true;

    const data = {
      title: this.title,
      description: this.description,
    };

    this.taskService.createTask(data).subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigate(['/tasks']);
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  goBack() {
    window.history.back();
  }
}
