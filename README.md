# Stray Care System – Frontend

**Tyler Cox – Frontend Developer**
CST8319 – Software Development Project
Student ID: 041164510
Algonquin College – Demo 3 Submission

---

## Demo 3 – Feature Coverage

### Pages Implemented

| Page | File | Auth Required | Description |
|------|------|---------------|-------------|
| Landing Page | `index.html` | No | App entry point with navigation |
| Dog Profile | `dog_profile.html` | No | Public dog profile view after QR scan |
| Report Sighting | `sighting_form.html` | No | GPS-enabled sighting report form |
| Volunteer Dashboard | `volunteer_dashboard.html` | Yes (JWT) | Manage all dogs with search/filter |
| Login | `login.html` | No | JWT authentication for volunteers |
| Shared Styles | `styles.css` | — | Mobile-first CSS with variables |

---

## Setup Instructions

### Prerequisites

- Modern web browser (Chrome 120+, Firefox 121+, Safari iOS 17+, Edge 120+)
- Backend Spring Boot API running on `http://localhost:8080`

### Running Locally

**Option 1 – Open directly in browser**

```bash
# Windows: double-click any HTML file, or drag to browser
# Mac/Linux:
open index.html
```

**Option 2 – Python HTTP server (recommended)**

```bash
cd "path/to/straycare-frontend"
python3 -m http.server 3000
# Visit: http://localhost:3000
```

**Option 3 – VS Code Live Server extension**

1. Install the "Live Server" extension in VS Code
2. Right-click any HTML file → "Open with Live Server"

### Testing GPS (requires HTTPS)

GPS geolocation requires a secure context (HTTPS) in Chrome and Safari.

```bash
# Install ngrok: https://ngrok.com
ngrok http 3000
# Use the HTTPS URL provided (e.g. https://abc123.ngrok.io)
```

---

## Backend API Requirements

The Spring Boot backend must be running on `http://localhost:8080`.

| Method | Endpoint | Auth | Used By |
|--------|----------|------|---------|
| `GET` | `/api/dogs` | Public | Dashboard, sighting form dropdown |
| `GET` | `/api/dogs/{id}` | Public | Dog profile page |
| `GET` | `/api/sightings/dog/{id}` | Public | Dog profile sighting summary |
| `POST` | `/api/sightings` | Public | Sighting form submission |
| `POST` | `/api/auth/login` | Public | Login page |

---

## Design Specifications

### Mobile-First Responsive Breakpoints

| Range | Layout |
|-------|--------|
| 375px – 767px | Single column, full-width elements |
| 768px – 1279px | 2-column dog card grid, centred content |
| 1280px+ | 3-column dog card grid, side-by-side profile layout |

### Color System (CSS Variables)

| Variable | Hex | Usage |
|----------|-----|-------|
| `--color-active` | `#27AE60` | Active status, primary buttons |
| `--color-adopted` | `#3498DB` | Adopted status |
| `--color-relocated` | `#95A5A6` | Relocated status |
| `--color-warning` | `#E67E22` | Warnings, nearly-full character count |
| `--color-error` | `#C0392B` | Errors, validation messages |

### Accessibility

- All interactive elements meet minimum **44×44 px** touch target size
- All images have descriptive `alt` text
- Colour is never the sole method of conveying information
- ARIA roles, `aria-live`, and `aria-label` used throughout
- Full keyboard navigation with visible `:focus-visible` indicators
- Skip-to-content links on every page
- Passes **WCAG AA** contrast ratio (4.5:1 minimum)

---

## Feature Highlights

### dog_profile.html

- Reads `?id=` from URL; validates input before fetching
- Parallel-friendly: fetches dog + sightings, handles sightings failure gracefully
- Three distinct error states: invalid ID, 404 not found, network error
- Photo fallback (emoji placeholder) if `photoUrl` is null or image fails to load
- Web Share API integration with clipboard fallback
- Status badge colours match design specification exactly

### sighting_form.html

- Pre-fills `dogId` from `?dogId=` URL parameter
- Falls back to a dog dropdown (loaded from `GET /api/dogs`) if no parameter
- GPS flow: request permission → fill latitude/longitude → button turns green
- Three GPS error codes handled individually (PERMISSION_DENIED, POSITION_UNAVAILABLE, TIMEOUT)
- Manual entry unlocked via checkbox when GPS is unavailable
- Submit button disabled until both coordinates are populated
- Photo file input with live preview thumbnail
- Character counter with warning at 90% of 500-character limit
- Inline field validation with ARIA error announcements

### volunteer_dashboard.html

- Checks `localStorage` for `jwt_token` on load; redirects to login if absent
- Handles 401/403 responses (expired token) separately from other errors
- Real-time search filters by name and breed simultaneously
- Four status filter buttons with `aria-pressed` state
- Stat pills in header show per-status counts
- Dog cards use CSS Grid: 1 → 2 → 3 columns across breakpoints
- Image error fallback per card (no broken images)

### login.html

- Redirects immediately if a valid token is already stored
- Handles 401 (wrong credentials) vs network errors with distinct messages
- `?redirect=` parameter preserves the original destination after login

---

## Testing Checklist

### dog_profile.html

- [x] Loads with valid dog ID in URL (`?id=1`)
- [x] Shows error if ID is missing or non-numeric
- [x] Shows "not found" message on 404 response
- [x] Photo fallback displayed when `photoUrl` is null or broken
- [x] Status badge colour correct for ACTIVE / ADOPTED / RELOCATED
- [x] Sighting count accurate from API array
- [x] Most recent sighting date/notes displayed
- [x] Verified badge shown only when `isVerified=true`
- [x] "Report Sighting" button links to `sighting_form.html?dogId={id}`
- [x] Loading spinner shown while fetching
- [x] Responsive at 375px, 768px, 1280px

### sighting_form.html

- [x] `dogId` pre-filled from URL parameter
- [x] Dog selector dropdown loaded from API when no `dogId` in URL
- [x] GPS button requests geolocation permission
- [x] Latitude and longitude auto-filled on GPS success
- [x] Manual entry enabled when GPS is denied or checkbox checked
- [x] Submit button disabled until coordinates are provided
- [x] Form submits `POST /api/sightings` with correct JSON body
- [x] Success screen shown after submission
- [x] Network error message shown on failure
- [x] Notes character counter (0 / 500)
- [x] Photo file input with preview
- [x] "Report Another" resets form

### volunteer_dashboard.html

- [x] Redirects to login when no JWT in localStorage
- [x] Sends `Authorization: Bearer <token>` header
- [x] Handles 401 (expired session) with redirect and clear
- [x] All dogs displayed as cards
- [x] Search filters by name and breed in real-time
- [x] Status filter buttons work (All / Active / Adopted / Relocated)
- [x] Card click navigates to `dog_profile.html?id={id}`
- [x] Logout clears token and redirects to login
- [x] Card grid: 1 column mobile → 2 tablet → 3 desktop

### login.html

- [x] Submits `POST /api/auth/login` with email + password
- [x] Stores token in localStorage on success
- [x] Redirects to `?redirect=` parameter (or dashboard) after login
- [x] Error message for invalid credentials
- [x] Error message for network failure
- [x] Skips login screen if token already present

---

## Known Limitations

- **Photo upload**: `photoUrl` is submitted as `null` to the API. Cloudinary upload integration is handled by the backend team (Alex). The file input and preview are fully functional on the frontend.
- **JWT refresh**: Tokens expire after 24 hours (backend-defined). Token refresh is not implemented; users are redirected to login.
- **Offline mode**: No service worker or offline caching. Requires active connection.

---

## Challenges & Solutions

**1. GPS permission denied on iOS Safari**
*Challenge:* iOS Safari requires HTTPS for `navigator.geolocation`. On `http://localhost`, permission is silently denied without an error callback on some devices.
*Solution:* Added `ngrok` HTTPS tunnel instructions for local testing. The form gracefully falls back to manual coordinate entry and shows a clear error message describing how to re-enable location access in browser settings.

**2. Keeping submit button state in sync**
*Challenge:* The submit button needed to be disabled until coordinates were filled, but coordinates could arrive from three different paths: GPS success, manual entry via checkbox, or GPS error fallback.
*Solution:* Centralised a `updateSubmitState()` function called by every input event handler and GPS callback, so button state is always derived from current field values rather than individual event paths.

---

## Next Steps (Demo 4)

- Health record entry form for volunteers (`health_record_form.html`)
- Admin QR code generation and printable tag interface
- Analytics dashboard with sighting map (Leaflet.js)
- Progressive Web App (PWA) manifest + service worker for offline support
- Push notifications when a verified sighting is recorded

---

## Browser Compatibility

| Browser | Version | Status |
|---------|---------|--------|
| Chrome | 120+ | Tested |
| Safari iOS | 17+ | Tested |
| Firefox | 121+ | Tested |
| Edge | 120+ | Tested |

---

## File Structure

```
straycare-frontend/
├── index.html                  Landing page
├── dog_profile.html            Public dog profile (post QR scan)
├── sighting_form.html          GPS sighting report form
├── volunteer_dashboard.html    Authenticated volunteer view
├── login.html                  JWT login page
├── styles.css                  Shared mobile-first stylesheet
└── README.md                   This file
```
