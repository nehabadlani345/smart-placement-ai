import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { ResumeCenter } from '../resume/resume-center';
import { AtsAnalyzer } from '../ats/ats-analyzer';
import { JdMatch } from '../jd/jd-match';

type Tab = 'resume' | 'ats' | 'jd';

@Component({
  selector: 'app-resume-workspace',
  standalone: true,
  imports: [CommonModule, ResumeCenter, AtsAnalyzer, JdMatch],
  templateUrl: './resume-workspace.html',
})
export class ResumeWorkspace {
  activeTab = signal<Tab>('resume');

  constructor(route: ActivatedRoute) {
    const tabParam = route.snapshot.queryParamMap.get('tab') as Tab | null;
    if (tabParam === 'ats' || tabParam === 'jd') {
      this.activeTab.set(tabParam);
    }
  }

  setTab(tab: Tab): void {
    this.activeTab.set(tab);
  }
}
