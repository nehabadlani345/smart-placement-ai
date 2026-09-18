export interface AtsReport {
  resumeId: string;
  totalScore: number;
  formatScore: number;
  sectionScore: number;
  keywordScore: number;
  skillClarityScore: number;
  experiencePresentationScore: number;
  aiStrengths: string[];
  aiImprovements: string[];
  aiSummary: string;
}
