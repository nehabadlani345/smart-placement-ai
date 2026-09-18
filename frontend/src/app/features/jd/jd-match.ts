import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { JdApiService } from './jd.service';
import { JdCompatibility } from '../../models/jd.model';

@Component({
  selector: 'app-jd-match',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './jd-match.html',
})
export class JdMatch {
  companyName = '';
  role = '';
  requiredSkillsRaw = '';
  preferredSkillsRaw = '';

  result = signal<JdCompatibility | null>(null);
  loading = signal(false);
  error = signal<string | null>(null);

  constructor(private jdApi: JdApiService) {}

  onSubmit(): void {
    this.loading.set(true);
    this.error.set(null);

    this.jdApi
      .analyze({
        companyName: this.companyName,
        role: this.role,
        requiredSkills: this.splitSkills(this.requiredSkillsRaw),
        preferredSkills: this.splitSkills(this.preferredSkillsRaw),
      })
      .subscribe({
        next: (result) => {
          this.result.set(result);
          this.loading.set(false);
        },
        error: (err) => {
          this.error.set(err.error?.message ?? 'Could not analyze compatibility');
          this.loading.set(false);
        },
      });
  }

  private splitSkills(raw: string): string[] {
    return raw
      .split(',')
      .map((s) => s.trim())
      .filter((s) => s.length > 0);
  }
}
