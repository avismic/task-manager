import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TaskService } from '../../../core/services/task.service';
import { Task } from '../../../models/task.model';
import { switchMap, Observable } from 'rxjs';

@Component({
  selector: 'app-edit',
  templateUrl: './edit.html',
  styleUrl: './edit.css',
  standalone: false,
})
export class Edit {
  taskId!: number;
  task$!: Observable<Task>;
  isLoading: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private taskService: TaskService,
    private router: Router,
  ) {
    this.task$ = this.route.paramMap.pipe(
      switchMap((params) => {
        const id = Number(params.get('id'));
        this.taskId = id;
        return this.taskService.getTaskById(id);
      }),
    );
  }

  onSubmit(task: Task) {
    if (this.isLoading) return;

    this.isLoading = true;

    const data = {
      title: task.title,
      description: task.description,
    };

    this.taskService.updateTask(this.taskId, data).subscribe({
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
