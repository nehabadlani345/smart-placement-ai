export interface JdAnalyzeRequest {
  companyName: string;
  role: string;
  requiredSkills: string[];
  preferredSkills: string[];
  minExperience?: number;
  maxExperience?: number;
}

export interface JdCompatibility {
  jobId: string;
  companyName: string;
  role: string;
  atsScore: number;
  matchedRequiredSkills: string[];
  missingRequiredSkills: string[];
  matchedPreferredSkills: string[];
  missingPreferredSkills: string[];
  scoreBreakdown: Record<string, number>;
  aiExplanation: string;
  aiRecommendedActions: string[];
}
