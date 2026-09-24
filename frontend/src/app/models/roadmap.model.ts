export interface RoadmapTask {
  id: string;
  title: string;
  description: string;
  estimatedHours: number;
  priority: 'HIGH' | 'MEDIUM' | 'LOW';
  completed: boolean;
}

export interface RoadmapPhase {
  title: string;
  periodNumber: number;
  periodLabel: string;
  tasks: RoadmapTask[];
}

export interface Roadmap {
  id: string;
  targetRole: string;
  durationValue: number;
  durationUnit: 'DAY' | 'WEEK' | 'MONTH';
  studyHoursPerPeriod: number;
  version: number;
  phases: RoadmapPhase[];
}

export interface RoadmapGenerateRequest {
  targetRole: string;
  durationValue: number;
  durationUnit: 'DAY' | 'WEEK' | 'MONTH';
  studyHoursPerPeriod: number;
}
