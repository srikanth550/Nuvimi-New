import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NutritionResponse } from '../models/nutrition.model';

@Injectable({ providedIn: 'root' })
export class NutritionApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/api/nutrition`;

  search(query: string): Observable<NutritionResponse> {
    return this.http.get<NutritionResponse>(`${this.baseUrl}/search`, {
      params: { query }
    });
  }
}
