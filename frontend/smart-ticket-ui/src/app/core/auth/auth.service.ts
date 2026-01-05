import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { jwtDecode } from 'jwt-decode';
import { environment } from '../../../environment';

interface JwtPayload {
  sub: string;
  roles: string[];
  exp: number; 
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private baseUrl = environment.apiBaseUrl + '/user-service/auth';
  private user: JwtPayload | null = null;

  constructor(private http: HttpClient) {}

  login(payload: { email: string; password: string }) {
    return this.http
      .post<{ token: string }>(`${this.baseUrl}/login`, payload)
      .pipe(
        tap(res => this.setSession(res.token))
      );
  }
register(payload: {
  name: string;
  email: string;
  password: string;
}) {
  return this.http.post(
    `${this.baseUrl}/register`,
    payload,
    {
      responseType: 'text',
      observe: 'response'
    }
  );
}


  private setSession(token: string): void {
    localStorage.setItem('token', token);
    this.user = jwtDecode<JwtPayload>(token);
  }

  loadUserFromToken(): void {
    const token = this.getToken();
    if (!token) return;

    const decoded = jwtDecode<JwtPayload>(token);

    if (this.isTokenExpired(decoded)) {
      this.logout();
      return;
    }

    this.user = decoded;
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) return false;

    const decoded = jwtDecode<JwtPayload>(token);
    return !this.isTokenExpired(decoded);
  }

  private isTokenExpired(payload: JwtPayload): boolean {
    const now = Math.floor(Date.now() / 1000);
    return payload.exp < now;
  }

  getRoles(): string[] {
    return this.user?.roles ?? [];
  }

  hasRole(role: string): boolean {
    return this.getRoles().includes(role);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }
getUserId(): string | null {
  return this.user?.sub ?? null;
}

  logout(): void {
    localStorage.clear();
    this.user = null;
  }
}
