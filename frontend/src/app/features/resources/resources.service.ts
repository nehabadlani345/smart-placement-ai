import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Resource } from '../../models/resource.model';

@Injectable({ providedIn: 'root' })
export class ResourcesApiService {
  constructor(private http: HttpClient) {}

  list(topic?: string, search?: string, bookmarkedOnly = false): Observable<Resource[]> {
    const params = new URLSearchParams();
    if (topic) params.set('topic', topic);
    if (search) params.set('search', search);
    if (bookmarkedOnly) params.set('bookmarkedOnly', 'true');
    return this.http.get<Resource[]>(`/api/v1/resources?${params.toString()}`);
  }

  toggleBookmark(id: number): Observable<void> {
    return this.http.post<void>(`/api/v1/resources/${id}/bookmark`, {});
  }
}
