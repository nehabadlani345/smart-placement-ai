import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AtsReport } from '../../models/ats.model';

@Injectable({ providedIn: 'root' })
export class AtsApiService {
  constructor(private http: HttpClient) {}

  analyze(): Observable<AtsReport> {
    return this.http.get<AtsReport>('/api/v1/ats/analyze');
  }

  getLatest(): Observable<AtsReport> {
  return this.http.get<AtsReport>('/api/v1/ats/latest');
}
}