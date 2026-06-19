import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { RecognitionResponse } from '../models/nutrition.model';

@Injectable({ providedIn: 'root' })
export class RecognitionApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/api/recognition`;

  identifyImage(file: File): Observable<RecognitionResponse> {
    const formData = new FormData();
    formData.append('image', file);
    return this.http.post<RecognitionResponse>(`${this.baseUrl}/image`, formData);
  }
}
