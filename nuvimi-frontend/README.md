# Nuvimi — Nutrients · Vitamins · Minerals

Search any fruit, vegetable, or ingredient by name — or snap a photo of it — and see
its calories, macronutrients, vitamins, and minerals, looked up live from the internet.

## How it works

```
                         ┌───────────────────────────┐
                         │   nuvimi-discovery (8761)  │   Eureka registry
                         │   every service below      │
                         │   registers itself here    │
                         └─────────────▲───────────────┘
                                        │ registers
        ┌───────────────────────────────┼───────────────────────────────┐
        │                                │                                │
┌───────┴────────┐              ┌────────┴─────────┐           ┌──────────┴─────────┐
│ nuvimi-gateway  │   routes →   │ food-recognition  │           │     nutrition       │
│     (8080)      │ ───────────► │      (8081)        │           │       (8082)         │
│ Spring Cloud    │              │ Google Cloud Vision │           │ USDA FoodData Central │
│ Gateway + CORS  │ ───────────► │ → identifies item   │           │ → calories/macros/    │
└───────▲─────────┘              └────────────────────┘           │   vitamins/minerals   │
        │                                                          └───────────────────────┘
        │ HTTP (REST + multipart)
┌───────┴─────────┐
│ nuvimi-frontend  │   Angular 22, standalone + signals
│     (4200)       │   search box, photo upload, results "label"
└──────────────────┘
```

1. The Angular app sends a search term, **or** an uploaded/captured photo, to the gateway.
2. For photos, **nuvimi-food-recognition** calls the Google Cloud Vision API to figure out
   what's in the picture (e.g. "Banana").
3. **nuvimi-nutrition** takes that name (typed or detected) and looks it up against
   **USDA FoodData Central** — the U.S. government's free, public nutrition database —
   returning calories, protein/fat/carbs/fiber/sugar, and every vitamin and mineral on record.
4. The Angular app renders the result as a redesigned "Nutrition Facts" label.

## A couple of deliberate decisions worth knowing about

- **Nutrition data comes from USDA FoodData Central, not by scraping Google search results.**
  Scraping Google's results page directly is against Google's Terms of Service, breaks
  constantly, and returns unstructured text. USDA FoodData Central is a free, public,
  authoritative U.S. government API built exactly for this purpose, so it's what actually
  gets you reliable vitamins/minerals data "from the internet."
- **Image recognition uses the Google Cloud Vision API** (Google's own image-recognition
  product), since identifying a photo needs a real vision model, not a search engine.
  You'll need a free API key — see [Setup](#setup) below. Searching by name works
  immediately with no setup.
- **Spring Boot 3.5.15 (not 4.x)**. Spring Boot 4.0 is very new (GA'd late 2025) and
  restructures quite a lot (modular starters, Jackson 3, etc.). 3.5.15 is the final,
  most mature 3.x release, paired with the matching Spring Cloud 2025.0.3 release train,
  so everything here is on stable, well-proven ground. Upgrading later is a normal,
  incremental step whenever you're ready — see Spring's official Boot 4 migration guide.

## Tech stack

| Layer | Technology |
|---|---|
| Frontend | Angular 22 (standalone components, signals, zoneless change detection) |
| API Gateway | Spring Cloud Gateway (WebFlux) |
| Service Discovery | Netflix Eureka |
| Microservices | Spring Boot 3.5.15, Java 21 |
| Nutrition data | USDA FoodData Central API |
| Image recognition | Google Cloud Vision API |
| Build tools | Maven (backend), npm/Angular CLI (frontend) |

## Project structure

```
nuvimi/
├── pom.xml                        Maven parent (shared versions/plugins)
├── nuvimi-discovery/               Eureka server                    :8761
├── nuvimi-gateway/                  Spring Cloud Gateway              :8080
├── nuvimi-food-recognition/         Vision API → item name            :8081
├── nuvimi-nutrition/                USDA FDC → nutrition facts        :8082
├── nuvimi-frontend/                 Angular app                       :4200
├── start-all.sh / stop-all.sh       convenience scripts (macOS/Linux)
└── README.md                        this file
```

## Prerequisites

- **Java 21** (JDK)
- **Maven 3.9+** (IntelliJ IDEA bundles its own, or install separately)
- **Node.js v22.22.3+ or v24.15.0+** and npm (required by Angular 22's CLI — check with `node -v`)

## Setup

### 1. API keys

| Service | Required? | Key |
|---|---|---|
| Nutrition search | No — works out of the box | Uses USDA's public `DEMO_KEY` (30 requests/hour). For real use, get a free key (1,000 requests/hour) at [fdc.nal.usda.gov/api-key-signup](https://fdc.nal.usda.gov/api-key-signup) and `export USDA_API_KEY=...` before starting `nuvimi-nutrition`. |
| Photo recognition | Yes, for the photo feature | Create a key in [Google Cloud Console](https://console.cloud.google.com) → enable the **Cloud Vision API** → **APIs & Services → Credentials** → create an API key, then `export GOOGLE_VISION_API_KEY=...` before starting `nuvimi-food-recognition`. Without it, searching by name still works fine; the photo button returns a clear message telling you it isn't configured yet. |

### 2. Open the project

Open the root `nuvimi` folder in IntelliJ IDEA — it will detect the multi-module Maven
project automatically (`pom.xml` at the root lists all four services as modules).

### 3. Start the backend, in this order

Each is its own Spring Boot app — run them from IntelliJ (right-click the `*Application`
class → Run) or from the command line:

```bash
# 1) Service registry - wait for it to fully start before the rest
cd nuvimi-discovery && mvn spring-boot:run

# 2-4) these three can start in any order, once discovery is up
cd nuvimi-gateway && mvn spring-boot:run
cd nuvimi-food-recognition && GOOGLE_VISION_API_KEY=your-key mvn spring-boot:run
cd nuvimi-nutrition && USDA_API_KEY=your-key mvn spring-boot:run
```

Check http://localhost:8761 — you should see all three services listed once they're
registered.

Or, on macOS/Linux, just run `./start-all.sh` from the project root to start everything
in the right order with one command (`./stop-all.sh` to stop it all).

### 4. Start the frontend

```bash
cd nuvimi-frontend
npm install
npm start
```

Visit **http://localhost:4200**.

## API reference (via the gateway, http://localhost:8080)

```
GET  /api/nutrition/search?query=banana
POST /api/recognition/image      (multipart/form-data, field name "image")
```

`GET /api/nutrition/search?query=banana` →
```json
{
  "foodName": "Bananas, raw",
  "servingDescription": "Per 100g",
  "calories": 89,
  "macros": { "proteinG": 1.09, "fatG": 0.33, "carbsG": 22.84, "fiberG": 2.6, "sugarG": 12.23 },
  "vitamins": [ { "name": "Vitamin C, total ascorbic acid", "amount": 8.7, "unit": "MG" }, ... ],
  "minerals": [ { "name": "Potassium, K", "amount": 358.0, "unit": "MG" }, ... ],
  "dataSource": "USDA FoodData Central",
  "sourceUrl": "https://fdc.nal.usda.gov/food-details/173944/nutrients"
}
```

## Troubleshooting

- **"Could not reach the USDA FoodData Central service" / rate limit errors** — you're
  likely on the default `DEMO_KEY` (30 requests/hour shared across everyone using it).
  Get your own free key (see Setup above).
- **Photo button returns a 503 "Image recognition isn't set up yet"** — `GOOGLE_VISION_API_KEY`
  isn't set for `nuvimi-food-recognition`. Searching by name is unaffected.
- **Frontend can't reach the API / CORS errors** — confirm the gateway is running on
  port 8080 and that `nuvimi.cors.allowed-origins` in `nuvimi-gateway/application.yml`
  includes `http://localhost:4200`.
- **A microservice won't register / gateway returns 404 for everything** — make sure
  `nuvimi-discovery` finished starting *before* the other services, and check
  http://localhost:8761 to confirm they're all listed.

## Possible next steps

- Add a circuit breaker (Resilience4j) around the USDA/Vision calls for graceful degradation.
- Add Spring Cloud Config for centralized configuration instead of per-service `application.yml`.
- Containerize each service with a `Dockerfile` + root `docker-compose.yml`.
- Swap the in-memory cache in `nuvimi-nutrition` for Redis if you need it to survive restarts.
