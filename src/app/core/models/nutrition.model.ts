export interface NutrientValue {
  name: string;
  amount: number;
  unit: string;
}

export interface MacroNutrients {
  proteinG: number | null;
  fatG: number | null;
  carbsG: number | null;
  fiberG: number | null;
  sugarG: number | null;
}

export interface NutritionResponse {
  foodName: string;
  servingDescription: string;
  calories: number;
  macros: MacroNutrients;
  vitamins: NutrientValue[];
  minerals: NutrientValue[];
  dataSource: string;
  sourceUrl: string;
}

export interface RecognitionResponse {
  detectedName: string;
  confidence: number;
  alternatives: string[];
}

export interface ApiErrorBody {
  error: string;
  message: string;
}
