import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DashboardApiService } from './dashboard.service';
import { AuthService } from '../../core/auth/auth.service';
import { Dashboard as DashboardData } from '../../models/dashboard.model';

const RING_RADIUS = 70;
const RING_CIRCUMFERENCE = 2 * Math.PI * RING_RADIUS;

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
})
export class Dashboard implements OnInit {
  data = signal<DashboardData | null>(null);
  loading = signal(true);

  animatedScore = signal(0);

  readonly ringRadius = RING_RADIUS;
  readonly ringCircumference = RING_CIRCUMFERENCE;

  ringOffset = computed(() => {
    const pct = this.animatedScore() / 100;
    return this.ringCircumference * (1 - pct);
  });

  constructor(
    private dashboardApi: DashboardApiService,
    public authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.dashboardApi.getDashboard().subscribe({
      next: (d: DashboardData) => {
        this.data.set(d);
        this.loading.set(false);
        setTimeout(() => this.animatedScore.set(d.readinessScore), 50);
      },
      error: () => {
        this.loading.set(false);
      },
    });
  }

  scoreLabel(score: number): string {
    if (score >= 75) return 'Strong';
    if (score >= 50) return 'Building';
    if (score > 0) return 'Getting started';
    return 'Not started';
  }

  metricColor(score: number | null): string {
    if (score === null) return 'text-gray-300';
    if (score >= 75) return 'text-emerald-600';
    if (score >= 50) return 'text-amber-600';
    return 'text-red-500';
  }
}
