import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RoadmapApiService } from './roadmap.service';
import { Roadmap, RoadmapTask } from '../../models/roadmap.model';

@Component({
  selector: 'app-roadmap',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './roadmap.html',
})
export class RoadmapPage implements OnInit {
  roadmap = signal<Roadmap | null>(null);
  loading = signal(false);
  generating = signal(false);
  error = signal<string | null>(null);
  notFound = signal(false);

  targetRole = '';
  weeklyStudyHours = 10;

  progressPercent = computed(() => {
    const r = this.roadmap();
    if (!r) return 0;
    const allTasks = r.phases.flatMap((p) => p.tasks);
    if (allTasks.length === 0) return 0;
    const done = allTasks.filter((t) => t.completed).length;
    return Math.round((done / allTasks.length) * 100);
  });

  constructor(private roadmapApi: RoadmapApiService) {}

  ngOnInit(): void {
    this.loading.set(true);
    this.roadmapApi.getCurrent().subscribe({
      next: (r) => {
        this.roadmap.set(r);
        this.loading.set(false);
      },
      error: () => {
        this.notFound.set(true);
        this.loading.set(false);
      },
    });
  }

  onGenerate(): void {
    this.generating.set(true);
    this.error.set(null);

    this.roadmapApi
      .generate({ targetRole: this.targetRole, weeklyStudyHours: this.weeklyStudyHours })
      .subscribe({
        next: (r) => {
          this.roadmap.set(r);
          this.notFound.set(false);
          this.generating.set(false);
        },
        error: (err) => {
          this.error.set(err.error?.message ?? 'Could not generate roadmap');
          this.generating.set(false);
        },
      });
  }

  toggleTask(task: RoadmapTask): void {
    const newState = !task.completed;
    task.completed = newState; // optimistic update
    this.roadmapApi.completeTask(task.id, newState).subscribe({
      error: () => {
        task.completed = !newState;
      }, // revert on failure
    });
  }

  priorityStyle(priority: string): string {
    switch (priority) {
      case 'HIGH':
        return 'bg-red-50 text-red-600';
      case 'MEDIUM':
        return 'bg-amber-50 text-amber-600';
      default:
        return 'bg-gray-100 text-gray-600';
    }
  }
}
