import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ResourcesApiService } from './resources.service';
import { Resource } from '../../models/resource.model';

const TOPICS = ['Java', 'DSA', 'System Design', 'Spring Boot', 'Angular', 'Web Fundamentals', 'Databases'];

@Component({
  selector: 'app-resources',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './resources.html'
})
export class Resources implements OnInit {
  resources = signal<Resource[]>([]);
  loading = signal(true);
  searchTerm = '';
  activeTopic = signal<string | null>(null);
  bookmarkedOnly = signal(false);
  topics = TOPICS;

  constructor(private resourcesApi: ResourcesApiService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.resourcesApi.list(this.activeTopic() ?? undefined, this.searchTerm || undefined, this.bookmarkedOnly())
      .subscribe({
        next: (r) => { this.resources.set(r); this.loading.set(false); },
        error: () => { this.loading.set(false); }
      });
  }

  setTopic(topic: string | null): void {
    this.activeTopic.set(topic === this.activeTopic() ? null : topic);
    this.load();
  }

  toggleBookmarkFilter(): void {
    this.bookmarkedOnly.set(!this.bookmarkedOnly());
    this.load();
  }

  toggleBookmark(resource: Resource): void {
    resource.bookmarked = !resource.bookmarked; // optimistic
    this.resourcesApi.toggleBookmark(resource.id).subscribe({
      error: () => { resource.bookmarked = !resource.bookmarked; }
    });
  }

  typeIcon(type: string): string {
    switch (type) {
      case 'VIDEO': return '▶';
      case 'REPO': return '⌥';
      case 'CHEATSHEET': return '☰';
      default: return '◈';
    }
  }
}