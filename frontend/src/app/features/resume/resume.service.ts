import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResumeSummary } from '../../models/resume.model';

@Injectable({ providedIn: 'root' })
export class ResumeService {
  constructor(private http: HttpClient) {}

  list(): Observable<ResumeSummary[]> {
    return this.http.get<ResumeSummary[]>('/api/v1/resumes');
  }

  upload(file: File): Observable<ResumeSummary> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ResumeSummary>('/api/v1/resumes/upload', formData);
  }

  download(id: string): void {
    window.open(`/api/v1/resumes/${id}/download`, '_blank');
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`/api/v1/resumes/${id}`);
  }
}
