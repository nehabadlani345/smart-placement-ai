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
  weekNumber: number;
  tasks: RoadmapTask[];
}

export interface Roadmap {
  id: string;
  targetRole: string;
  weeklyStudyHours: number;
  version: number;
  phases: RoadmapPhase[];
}

export interface RoadmapGenerateRequest {
  targetRole: string;
  weeklyStudyHours: number;
}
