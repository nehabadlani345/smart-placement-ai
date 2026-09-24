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
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./core/layout/app-layout').then((m) => m.AppLayout),
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard').then((m) => m.Dashboard),
      },
      // {
      //   path: 'resumes',
      //   loadComponent: () => import('./features/resume/resume-center').then((m) => m.ResumeCenter),
      // },
      // {
      //   path: 'ats',
      //   loadComponent: () => import('./features/ats/ats-analyzer').then((m) => m.AtsAnalyzer),
      // },
      // {
      //   path: 'jd-match',
      //   loadComponent: () => import('./features/jd/jd-match').then((m) => m.JdMatch),
      // },
      {
        path: 'resumes',
        loadComponent: () =>
          import('./features/workspace/resume-workspace').then((m) => m.ResumeWorkspace),
      },
      {
        path: 'roadmap',
        loadComponent: () => import('./features/roadmap/roadmap').then((m) => m.RoadmapPage),
      },
    ],
  },
];
