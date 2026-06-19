import { Component, ElementRef, computed, inject, signal, viewChild } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { NutritionApiService } from '../../core/services/nutrition-api.service';
import { RecognitionApiService } from '../../core/services/recognition-api.service';
import { ApiErrorBody, NutritionResponse } from '../../core/models/nutrition.model';

interface MacroEntry {
  label: string;
  value: number;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {
  private readonly nutritionApi = inject(NutritionApiService);
  private readonly recognitionApi = inject(RecognitionApiService);

  private readonly fileInput = viewChild.required<ElementRef<HTMLInputElement>>('fileInput');

  readonly suggestions = ['Spinach', 'Banana', 'Almonds', 'Sweet potato', 'Salmon'];

  // Search state
  readonly queryInput = signal('');
  readonly pendingQuery = signal('');
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly result = signal<NutritionResponse | null>(null);

  // Photo recognition state
  readonly imagePreviewUrl = signal<string | null>(null);
  readonly recognizing = signal(false);
  readonly recognitionError = signal<string | null>(null);
  readonly detectedName = signal<string | null>(null);
  readonly confidence = signal(0);
  readonly alternatives = signal<string[]>([]);

  readonly confidencePercent = computed(() => Math.round(this.confidence() * 100));

  readonly macroEntries = computed<MacroEntry[]>(() => {
    const macros = this.result()?.macros;
    if (!macros) {
      return [];
    }
    const entries: MacroEntry[] = [];
    if (macros.proteinG != null) entries.push({ label: 'Protein', value: macros.proteinG });
    if (macros.fatG != null) entries.push({ label: 'Fat', value: macros.fatG });
    if (macros.carbsG != null) entries.push({ label: 'Carbs', value: macros.carbsG });
    if (macros.fiberG != null) entries.push({ label: 'Fiber', value: macros.fiberG });
    if (macros.sugarG != null) entries.push({ label: 'Sugar', value: macros.sugarG });
    return entries;
  });

  onQueryInput(event: Event): void {
    this.queryInput.set((event.target as HTMLInputElement).value);
  }

  onSearchSubmit(event: Event): void {
    event.preventDefault();
    const query = this.queryInput().trim();
    if (query) {
      this.runSearch(query);
    }
  }

  searchSuggestion(name: string): void {
    this.queryInput.set(name);
    this.runSearch(name);
  }

  triggerFileInput(): void {
    this.fileInput().nativeElement.click();
  }

  clearImage(): void {
    this.imagePreviewUrl.set(null);
    this.recognizing.set(false);
    this.recognitionError.set(null);
    this.detectedName.set(null);
    this.alternatives.set([]);
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) {
      return;
    }

    this.imagePreviewUrl.set(URL.createObjectURL(file));
    this.recognizing.set(true);
    this.recognitionError.set(null);
    this.detectedName.set(null);
    this.alternatives.set([]);
    this.result.set(null);
    this.error.set(null);

    this.recognitionApi.identifyImage(file).subscribe({
      next: (response) => {
        this.recognizing.set(false);
        this.detectedName.set(response.detectedName);
        this.confidence.set(response.confidence);
        this.alternatives.set(response.alternatives);
        this.queryInput.set(response.detectedName);
        this.runSearch(response.detectedName);
      },
      error: (err: HttpErrorResponse) => {
        this.recognizing.set(false);
        this.recognitionError.set(this.extractMessage(err, 'Could not identify that photo. Try a clearer, closer shot.'));
      }
    });

    input.value = '';
  }

  private runSearch(query: string): void {
    this.loading.set(true);
    this.error.set(null);
    this.pendingQuery.set(query);

    this.nutritionApi.search(query).subscribe({
      next: (response) => {
        this.result.set(response);
        this.loading.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.result.set(null);
        this.loading.set(false);
        this.error.set(this.extractMessage(err, 'We could not look up that food right now.'));
      }
    });
  }

  private extractMessage(err: HttpErrorResponse, fallback: string): string {
    const body = err.error as ApiErrorBody | undefined;
    return body?.message ?? fallback;
  }
}
