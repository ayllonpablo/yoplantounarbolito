# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Sembrando Vidas** (YoPlantoUnArbolito) is a tree planting and adoption mobile application with two main components:

1. **Backend** - Laravel 8.75 REST API (`backend/yoPlantoUnArbolito/`)
2. **Frontend** - Native Android app (`front_end/Sembrando Vidas/`)

The app allows users to register trees, adopt them, track care actions, and compete in rankings for environmental conservation.

## Architecture

### Backend (Laravel API)

**Technology Stack:**
- Laravel 8.75 with Sanctum for API authentication
- MySQL database
- PHP 7.3|8.0+
- Key dependencies: phpgeo (geospatial calculations), laravel-cors, Firebase support

**API Structure:**
- Modular route organization in `routes/api/` directory:
  - `AuthRoutes.php` - login, logout, register, refresh token
  - `UserRoutes.php` - user CRUD operations
  - `ActionRoutes.php` - tree care actions (plantar, regar, limpieza, etc.)
  - `DeviceRoutes.php` - Firebase FCM token management
- Main routes in `routes/api.php` with Sanctum middleware for protected endpoints

**Key Models:**
- `User` - firstname, lastname, email, age, phone, organization, points
- `Tree` - tree registration and location data
- `tree_user` - pivot table for tree adoption
- `Action` - care actions for trees
- `Device` - push notification tokens

**Authentication Flow:**
- Uses Laravel Sanctum for token-based API auth
- Returns `accessToken` and user object on login/register
- Token must be included in `Authorization: Bearer {token}` header for protected routes

### Frontend (Android)

**Technology Stack:**
- Native Android (Java)
- Gradle 7.3.3, AGP 8.2.0
- Min SDK 24, Target SDK 34
- View Binding enabled

**Key Dependencies:**
- Volley for HTTP networking
- Google Maps API integration
- Firebase Cloud Messaging
- Material Design Components

**Architecture Pattern:**
- Activity-based architecture (no MVVM/MVP)
- Network layer: Volley with `JsonObjectRequest`
- Key classes:
  - `Variables.java` - Contains API base URL configuration
  - `Validations.java` - Error handling and data conversion utilities
  - `Preferences.java` - SharedPreferences wrapper for token storage
  - `UserDatabase.java` - Field name constants for API requests

**Critical Configuration:**
- API URL is configured in `app/src/main/java/app/sembrando/vidas/java_class/Variables.java`
- For emulator: `http://10.0.2.2:8000/api`
- For physical devices: Use local network IP

**Known Issues:**
- Content-Type headers use `application/vnd.api+json` but backend expects `application/json`
- No input validation before API calls
- Network errors can cause NullPointerException if not handled (fixed in Validations.java)

## Development Commands

### Backend Setup

```bash
cd backend/yoPlantoUnArbolito

# Install dependencies
composer install

# Configure environment
cp .env.example .env
php artisan key:generate

# Database setup (ensure MySQL is running)
# Update .env with database credentials
php artisan migrate

# Start development server (accessible from emulator)
php artisan serve --host=0.0.0.0 --port=8000

# Run tests
php artisan test
./vendor/bin/phpunit

# Clear caches
php artisan config:clear
php artisan cache:clear
php artisan route:clear
```

### Frontend Setup

```bash
cd "front_end/Sembrando Vidas"

# Build debug APK
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk

# Build release APK
./gradlew assembleRelease

# Clean build
./gradlew clean

# Run tests
./gradlew test

# Check for dependency updates
./gradlew dependencyUpdates
```

### Android Testing with Emulator

```bash
# List available AVDs
$ANDROID_HOME/emulator/emulator -list-avds

# Start emulator
$ANDROID_HOME/emulator/emulator -avd Pixel_5_API_32 &

# Install APK
adb install -r "front_end/Sembrando Vidas/app/build/outputs/apk/debug/app-debug.apk"

# View logs in real-time
adb logcat | grep "app.sembrando.vidas"

# View crash logs
adb logcat AndroidRuntime:E *:S

# Clear logs
adb logcat -c

# Force stop app
adb shell am force-stop app.sembrando.vidas

# Launch app
adb shell am start -n app.sembrando.vidas/app.sembrando.vidas.MainActivity
```

## Database Configuration

**Required MySQL Setup:**

```sql
-- Create database
CREATE DATABASE IF NOT EXISTS yoPlantoUnArbolito;

-- Create user (recommended instead of root)
CREATE USER IF NOT EXISTS 'laravel_user'@'localhost' IDENTIFIED BY 'laravel_pass123';
GRANT ALL PRIVILEGES ON yoPlantoUnArbolito.* TO 'laravel_user'@'localhost';
FLUSH PRIVILEGES;
```

**Environment Configuration:**
Update `backend/yoPlantoUnArbolito/.env`:
```
DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=yoPlantoUnArbolito
DB_USERNAME=laravel_user
DB_PASSWORD=laravel_pass123
```

## API Endpoint Structure

Base URL: `http://0.0.0.0:8000/api`

**Public Endpoints:**
- `POST /auth/login` - email, password
- `POST /auth/register` - firstname, lastname, email, age, phone, password, password_confirmation
- `POST /trees` - Create tree (no auth required)
- `POST /tree_users` - Create tree adoption
- `GET /get_photo/{id}` - Get tree photo
- `PUT /savephoto/{id}` - Save tree photo

**Protected Endpoints (require Bearer token):**
- `POST /auth/logout`
- `POST /auth/refresh`
- `GET /auth/me`
- `GET /trees` - List all trees
- `GET /trees/{id}` - Get tree details
- `PATCH /trees/{id}` - Update tree
- `GET /tree_users` - List adoptions
- `GET /tree_users/{id}` - Get adoption details
- User, Action, and Device routes (see respective route files)

## Important File Locations

**Backend:**
- API Routes: `backend/yoPlantoUnArbolito/routes/api/`
- Controllers: `backend/yoPlantoUnArbolito/app/Http/Controllers/`
- Models: `backend/yoPlantoUnArbolito/app/Models/`
- Migrations: `backend/yoPlantoUnArbolito/database/migrations/`
- Environment: `backend/yoPlantoUnArbolito/.env`

**Frontend:**
- API URL Config: `front_end/Sembrando Vidas/app/src/main/java/app/sembrando/vidas/java_class/Variables.java`
- Activities: `front_end/Sembrando Vidas/app/src/main/java/app/sembrando/vidas/`
- Layouts: `front_end/Sembrando Vidas/app/src/main/res/layout/`
- Manifest: `front_end/Sembrando Vidas/app/src/main/AndroidManifest.xml`

## Testing Credentials

For development testing:
```
Email: test@test.com
Password: password
```

## Common Issues & Solutions

**Backend not accessible from emulator:**
- Ensure server starts with `--host=0.0.0.0` not just `127.0.0.1`
- Verify firewall allows connections on port 8000

**App crashes on login:**
- Check Validations.java has null checks for NetworkResponse
- Verify backend URL in Variables.java matches running server
- Check logcat for specific error: `adb logcat AndroidRuntime:E *:S`

**Database connection errors:**
- Verify MySQL is running: `service mysql status`
- Check .env credentials match MySQL user
- Ensure database exists and migrations have run

**Gradle build failures:**
- First build downloads Gradle (~100MB), be patient
- Check JDK version compatibility (Java 8 required)
- Run `./gradlew clean` if encountering cache issues

## Project-Specific Conventions

**API Request Headers (Backend expects):**
```java
headers.put("Accept", "application/json");
headers.put("Content-Type", "application/json");
```

**API Response Format:**
```json
{
  "accessToken": "token_string",
  "tokenType": "Bearer",
  "user": { /* user object */ }
}
```

**Android Network Error Handling:**
Always check if `VolleyError.networkResponse` is null before accessing `.data` to prevent crashes when there's no network connectivity.

**Tree Care Action Types:**
Defined in Variables.java: PLANTAR, REGAR, LIMPIEZA, ABONO, AGARRE, JUEGOS

