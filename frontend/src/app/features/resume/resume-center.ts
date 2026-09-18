import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ResumeService } from './resume.service';
import { ResumeSummary } from '../../models/resume.model';

@Component({
  selector: 'app-resume-center',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './resume-center.html',
})
export class ResumeCenter implements OnInit {
  resumes = signal<ResumeSummary[]>([]);
  uploading = signal(false);
  error = signal<string | null>(null);
  dragOver = signal(false);

  constructor(private resumeService: ResumeService) {}

  ngOnInit(): void {
    this.loadResumes();
  }

  loadResumes(): void {
    this.resumeService.list().subscribe({
      next: (resumes) => this.resumes.set(resumes),
      error: () => this.error.set('Could not load resumes'),
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) this.uploadFile(input.files[0]);
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.dragOver.set(false);
    const file = event.dataTransfer?.files?.[0];
    if (file) this.uploadFile(file);
  }

  private uploadFile(file: File): void {
    this.uploading.set(true);
    this.error.set(null);

    this.resumeService.upload(file).subscribe({
      next: () => {
        this.uploading.set(false);
        this.loadResumes();
      },
      error: (err) => {
        this.uploading.set(false);
        this.error.set(err.error?.message ?? 'Upload failed');
      },
    });
  }

  deleteResume(id: string): void {
    this.resumeService.delete(id).subscribe(() => this.loadResumes());
  }

  formatSize(bytes: number): string {
    return (bytes / 1024).toFixed(0) + ' KB';
  }
}
