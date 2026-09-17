import { Routes } from '@angular/router';
import { authGuard } from './core/auth/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () => import('./core/auth/login/login').then((m) => m.Login),
  },
  {
    path: 'signup',
    loadComponent: () => import('./core/auth/signup/signup').then((m) => m.Signup),
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./core/auth/login/login').then((m) => m.Login),
    // placeholder until Phase 5's real dashboard
  },
];
