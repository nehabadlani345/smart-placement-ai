import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AtsApiService } from './ats.service';
import { AtsReport } from '../../models/ats.model';

@Component({
  selector: 'app-ats-analyzer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ats-analyzer.html',
})
export class AtsAnalyzer implements OnInit {
  report = signal<AtsReport | null>(null);
  loading = signal(false);
  initializing = signal(true);
  error = signal<string | null>(null);

  constructor(private atsApi: AtsApiService) {}

  ngOnInit(): void {
    this.atsApi.getLatest().subscribe({
      next: (r) => {
        this.report.set(r);
        this.initializing.set(false);
      },
      error: () => {
        this.initializing.set(false);
      }, // no report yet — show the empty state, not an error
    });
  }

  runAnalysis(): void {
    this.loading.set(true);
    this.error.set(null);

    this.atsApi.analyze().subscribe({
      next: (report) => {
        this.report.set(report);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message ?? 'Could not analyze resume');
        this.loading.set(false);
      },
    });
  }

  scoreColor(score: number): string {
    if (score >= 75) return 'text-emerald-600';
    if (score >= 50) return 'text-amber-600';
    return 'text-red-500';
  }

  scoreBg(score: number): string {
    if (score >= 75) return 'bg-emerald-50';
    if (score >= 50) return 'bg-amber-50';
    return 'bg-red-50';
  }
}
