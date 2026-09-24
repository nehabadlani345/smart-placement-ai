import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RoadmapApiService } from './roadmap.service';
import { Roadmap, RoadmapTask } from '../../models/roadmap.model';

type DurationUnit = 'DAY' | 'WEEK' | 'MONTH';

@Component({
  selector: 'app-roadmap',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './roadmap.html',
})
export class RoadmapPage implements OnInit {
  // =========================================================
  // ROADMAP STATE
  // =========================================================

  roadmap = signal<Roadmap | null>(null);

  loading = signal(false);

  generating = signal(false);

  error = signal<string | null>(null);

  notFound = signal(false);

  // =========================================================
  // ROADMAP GENERATION FORM
  // =========================================================

  targetRole = '';

  durationValue = 4;

  durationUnit = signal<DurationUnit>('WEEK');

  studyHoursPerPeriod = 10;

  /*
   * Typed duration units.
   *
   * This prevents Angular from treating the values inside
   * the template as generic strings.
   */
  readonly durationUnits: DurationUnit[] = ['DAY', 'WEEK', 'MONTH'];

  // =========================================================
  // PROGRESS
  // =========================================================

  progressPercent = computed(() => {
    const r = this.roadmap();

    if (!r) {
      return 0;
    }

    const allTasks = r.phases.flatMap((phase) => phase.tasks);

    if (allTasks.length === 0) {
      return 0;
    }

    const completedTasks = allTasks.filter((task) => task.completed).length;

    return Math.round((completedTasks / allTasks.length) * 100);
  });

  // =========================================================
  // UNIT LABEL
  // =========================================================

  unitLabel = computed(() => {
    const unit = this.durationUnit();

    switch (unit) {
      case 'DAY':
        return 'day';

      case 'MONTH':
        return 'month';

      case 'WEEK':
      default:
        return 'week';
    }
  });

  // =========================================================
  // CONSTRUCTOR
  // =========================================================

  constructor(private roadmapApi: RoadmapApiService) {}

  // =========================================================
  // INITIAL LOAD
  // =========================================================

  ngOnInit(): void {
    this.loading.set(true);

    this.error.set(null);

    this.roadmapApi.getCurrent().subscribe({
      next: (roadmap) => {
        this.roadmap.set(roadmap);

        this.notFound.set(false);

        this.loading.set(false);
      },

      error: () => {
        this.roadmap.set(null);

        this.notFound.set(true);

        this.loading.set(false);
      },
    });
  }

  // =========================================================
  // CHANGE DURATION UNIT
  // =========================================================

  setUnit(unit: DurationUnit): void {
    this.durationUnit.set(unit);
  }

  // =========================================================
  // GENERATE ROADMAP
  // =========================================================

  onGenerate(): void {
    /*
     * Basic frontend validation.
     */
    if (!this.targetRole.trim()) {
      this.error.set('Please enter a target role.');

      return;
    }

    if (!this.durationValue || this.durationValue < 1) {
      this.error.set('Duration must be at least 1.');

      return;
    }

    if (!this.studyHoursPerPeriod || this.studyHoursPerPeriod < 1) {
      this.error.set('Study hours must be at least 1.');

      return;
    }

    this.generating.set(true);

    this.error.set(null);

    this.roadmapApi
      .generate({
        targetRole: this.targetRole.trim(),

        durationValue: this.durationValue,

        durationUnit: this.durationUnit(),

        studyHoursPerPeriod: this.studyHoursPerPeriod,
      })
      .subscribe({
        next: (roadmap) => {
          this.roadmap.set(roadmap);

          this.notFound.set(false);

          this.generating.set(false);
        },

        error: (err) => {
          this.error.set(err?.error?.message ?? 'Could not generate roadmap');

          this.generating.set(false);
        },
      });
  }

  // =========================================================
  // COMPLETE / UNCOMPLETE TASK
  // =========================================================

  toggleTask(task: RoadmapTask): void {
    const previousState = task.completed;

    const newState = !previousState;

    /*
     * Optimistic UI update.
     */
    task.completed = newState;

    this.roadmapApi.completeTask(task.id, newState).subscribe({
      error: () => {
        /*
         * Revert if backend update fails.
         */
        task.completed = previousState;
      },
    });
  }

  // =========================================================
  // PRIORITY STYLE
  // =========================================================

  priorityStyle(priority: string): string {
    switch (priority?.toUpperCase()) {
      case 'HIGH':
        return 'bg-red-50 text-red-600';

      case 'MEDIUM':
        return 'bg-amber-50 text-amber-600';

      case 'LOW':
        return 'bg-gray-100 text-gray-600';

      default:
        return 'bg-gray-100 text-gray-600';
    }
  }
}
