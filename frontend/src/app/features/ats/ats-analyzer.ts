import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AtsApiService } from './ats.service';
import { AtsReport } from '../../models/ats.model';

@Component({
  selector: 'app-ats-analyzer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ats-analyzer.html',
})
export class AtsAnalyzer {
  report = signal<AtsReport | null>(null);
  loading = signal(false);
  error = signal<string | null>(null);

  constructor(private atsApi: AtsApiService) {}

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
    if (score >= 75) return 'text-green-600';
    if (score >= 50) return 'text-amber-600';
    return 'text-red-600';
  }
}
