export interface NextAction {
  title: string;
  description: string;
  route: string;
}

export interface Dashboard {
  readinessScore: number;
  atsScore: number | null;
  jdScore: number | null;
  roadmapCompletionPercent: number | null;
  topGaps: string[];
  nextActions: NextAction[];
}
