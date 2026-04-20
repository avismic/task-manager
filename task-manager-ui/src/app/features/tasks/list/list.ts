import { Component, OnInit } from '@angular/core';
import { TaskService } from '../../../core/services/task.service';
import { Task } from '../../../models/task.model';
import { Page } from '../../../models/page.model';
import { Observable, switchMap, Subject, BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-list',
  templateUrl: './list.html',
  styleUrl: './list.css',
  standalone: false,
})
export class List {
  isLoading: boolean = true;
  private refresh$ = new BehaviorSubject<void>(undefined);
  page: number = 0;
  userName: string | null = null;
  deletingId: number | null = null;
  togglingId: number | null = null;
  sort: string = 'id,desc';
  theme: 'light' | 'dark' = 'light';

  tasks$ = this.refresh$.pipe(
    switchMap(() => {
      this.isLoading = true;

      const params: any = {
        page: this.page,
        size: 10,
        sort: this.sort,
      };

      if (this.search?.trim()) {
        params.search = this.search.trim();
      }

      if (this.status) {
        params.status = this.status;
      }

      return this.taskService.getTasks(params);
    }),
    tap(() => {
      this.isLoading = false;
    }),
  );

  confirmDeleteId: number | null = null;
  search: string = '';
  status: string = '';

  constructor(
    private taskService: TaskService,
    private router: Router,
    private route: ActivatedRoute,
  ) {
    this.userName = localStorage.getItem('name');
  }
  onDeleteClick(id: number) {
    if (this.confirmDeleteId === id) {
      this.deletingId = id;

      this.taskService.deleteTask(id).subscribe({
        next: () => {
          console.log('Deleted:', id);
          this.confirmDeleteId = null;
          this.deletingId = null;

          this.refresh$.next();
        },
        error: () => {
          this.deletingId = null;
        },
      });
    } else {
      this.confirmDeleteId = id;
    }
  }

  onToggle(id: number) {
    if (this.togglingId === id) return;

    this.togglingId = id;

    this.taskService.toggleTask(id).subscribe({
      next: () => {
        console.log('Toggled:', id);
        this.togglingId = null;
        this.refresh$.next();
      },
      error: () => {
        this.togglingId = null;
      },
    });
  }

  onFilterChange() {
    this.refresh$.next();
  }

  onPageChange(page: number) {
    this.page = page;
    this.refresh$.next();
  }

  goToCreate() {
    this.router.navigate(['/tasks/create']);
  }

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/auth/login']);
  }

  goToProfile() {
    this.router.navigate(['/profile']);
  }

  goToEdit(id: number) {
    this.router.navigate(['/tasks/edit', id]);
  }

  onSortChange() {
    this.page = 0; // reset to first page
    this.refresh$.next();
  }

  toggleTheme() {
    this.theme = this.theme === 'light' ? 'dark' : 'light';

    document.body.classList.remove('light', 'dark');
    document.body.classList.add(this.theme);

    localStorage.setItem('theme', this.theme);
  }

  ngOnInit() {
    const savedTheme = localStorage.getItem('theme') as 'light' | 'dark';

    if (savedTheme) {
      this.theme = savedTheme;
      document.body.classList.add(this.theme);
    }

    const params = this.route.snapshot.queryParams;

    this.page = params['page'] ? +params['page'] : 0;
    this.search = params['search'] || '';
    this.status = params['status'] || '';
    this.sort = params['sort'] || 'id,desc';
  }
}
