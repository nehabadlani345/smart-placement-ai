import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { AuthResponse, LoginRequest, RegisterRequest } from '../../models/auth.model';

const TOKEN_KEY = 'spa_token';
const USER_KEY = 'spa_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private currentUserSignal = signal<Omit<AuthResponse, 'token'> | null>(this.loadStoredUser());
  isLoggedIn = computed(() => this.currentUserSignal() !== null);
  currentUser = computed(() => this.currentUserSignal());

  constructor(
    private http: HttpClient,
    private router: Router,
  ) {}

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>('/api/v1/auth/register', request)
      .pipe(tap((res) => this.storeSession(res)));
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>('/api/v1/auth/login', request)
      .pipe(tap((res) => this.storeSession(res)));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.currentUserSignal.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  private storeSession(res: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, res.token);
    const { token, ...user } = res;
    localStorage.setItem(USER_KEY, JSON.stringify(user));
    this.currentUserSignal.set(user);
  }

  private loadStoredUser(): Omit<AuthResponse, 'token'> | null {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) : null;
  }
}
