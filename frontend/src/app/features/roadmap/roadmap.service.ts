import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Roadmap, RoadmapGenerateRequest } from '../../models/roadmap.model';

@Injectable({ providedIn: 'root' })
export class RoadmapApiService {
  constructor(private http: HttpClient) {}

  generate(request: RoadmapGenerateRequest): Observable<Roadmap> {
    return this.http.post<Roadmap>('/api/v1/roadmap/generate', request);
  }

  getCurrent(): Observable<Roadmap> {
    return this.http.get<Roadmap>('/api/v1/roadmap/current');
  }

  completeTask(taskId: string, completed: boolean): Observable<Roadmap> {
    return this.http.patch<Roadmap>(
      `/api/v1/roadmap/tasks/${taskId}/complete?completed=${completed}`,
      {},
    );
  }
}
