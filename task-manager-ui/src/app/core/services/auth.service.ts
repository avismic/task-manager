import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private baseUrl = `${environment.apiBaseUrl}/users`;

  constructor(private http: HttpClient) {}

  login(data: { email: string; password: string }): Observable<any> {
    return new Observable((observer) => {
      this.http.post<any>(`${this.baseUrl}/login`, data).subscribe({
        next: (res) => {
          localStorage.setItem('token', res.token);
          localStorage.setItem('name', res.name);
          localStorage.setItem('email', res.email);
          observer.next(res);
          observer.complete();
        },
        error: (err) => observer.error(err),
      });
    });
  }

  // register(data: any): Observable<any> {
  //   return this.http.post(`${this.baseUrl}/register`, data);
  // }

  register(data: { name: string; email: string; password: string }) {
    return this.http.post(`${this.baseUrl}/register`, data);
  }

  updateName(data: { name: string }) {
    return this.http.put(`${this.baseUrl}/name`, data);
  }

  changePassword(data: { oldPassword: string; newPassword: string }) {
    return this.http.put(`${this.baseUrl}/password`, data, { responseType: 'text' });
  }
}
