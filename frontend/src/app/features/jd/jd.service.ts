import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JdAnalyzeRequest, JdCompatibility } from '../../models/jd.model';

@Injectable({ providedIn: 'root' })
export class JdApiService {
  constructor(private http: HttpClient) {}

  analyze(request: JdAnalyzeRequest): Observable<JdCompatibility> {
    return this.http.post<JdCompatibility>('/api/v1/jd/analyze', request);
  }
}
