# Stray Care System – Frontend

**Tyler Cox – Frontend Developer**
CST8319 – Software Development Project
Student ID: 041164510
Algonquin College – Demo 4 (Final) Submission

---

## What I Built

This is the frontend for the Stray Care System — a web app that lets anyone report a stray dog sighting by scanning a QR code on the dog's collar. No app download, no account needed. You scan, see the dog's profile, tap a button to share your location, and submit. Done in under a minute.

I built six pages:

- **index.html** — the landing page
- **dog_profile.html** — loads after a QR scan, shows the dog's info and sighting history
- **sighting_form.html** — the GPS-enabled form for submitting a sighting
- **volunteer_dashboard.html** — where volunteers manage all the dogs (login required)
- **admin_dashboard.html** — where admins manage user accounts and roles (admin JWT required)
- **login.html** — JWT login for volunteers and admins

Everything is plain HTML, CSS, and vanilla JavaScript. No frameworks.

---

## Running It

The frontend connects to a Spring Boot backend on `http://localhost:8080`. Make sure that's running first (that's Alex's side).

**Easiest way — just open the file:**
Double-click `index.html` and it opens in your browser.

**Better way — run a local server:**
```bash
python3 -m http.server 3000
```
Then go to `http://localhost:3000`.

**VS Code:**
Install Live Server, right-click any HTML file, click "Open with Live Server".

**Testing GPS on your phone:**
GPS requires HTTPS, so `localhost` won't work on mobile. Use ngrok to expose your local server:
```bash
ngrok http 3000
```
Then open the HTTPS URL it gives you on your phone.

---

## API Endpoints Used

The backend needs to have these working:

| Method | Endpoint | Who uses it |
|--------|----------|-------------|
| `GET` | `/api/dogs` | Dashboard, sighting form |
| `GET` | `/api/dogs/{id}` | Dog profile page |
| `GET` | `/api/sightings/dog/{id}` | Dog profile — full sighting history |
| `GET` | `/api/sightings` | Dashboard — heatmap data |
| `POST` | `/api/sightings` | Sighting form (multipart/form-data with optional photo) |
| `GET` | `/api/admin/users` | Admin dashboard — user list |
| `PUT` | `/api/admin/users/{id}/role` | Admin dashboard — role update |
| `DELETE` | `/api/admin/users/{id}` | Admin dashboard — delete user |
| `POST` | `/api/auth/login` | Login page |

---

## Pages in Detail

### dog_profile.html

Reads the `?id=` from the URL and fetches the dog's data. If the ID is missing, invalid, or the API returns a 404, it shows a friendly error message instead of breaking. There are three different error states depending on what went wrong.

**Demo 4 update:** The profile now shows a full, scrollable sighting history (newest first) instead of just the most recent sighting. Each entry displays the date, notes, and a verified/awaiting-review badge. The newest sighting is tagged with a "Latest" pill. The list is capped at 380px height with its own scroll area so the rest of the page layout stays stable.

The "Report a Sighting" button at the bottom pre-fills the dogId into the sighting form URL automatically.

### sighting_form.html

The main thing here is the GPS button. Tap it, allow location, and it fills in your latitude and longitude automatically. If GPS gets denied or times out, there's a checkbox to enter coordinates manually instead — it doesn't just leave you stuck.

The notes field is optional. The submit button stays disabled until coordinates are filled in. After a successful submit it shows a thank-you screen instead of just resetting the form silently.

**Demo 4 update:** Photo upload now includes client-side validation — file type whitelist (JPG, PNG, WebP, HEIC/HEIF), 5 MB size cap, and empty-file guard. Rejected files show a specific inline error ("That photo is 7.42 MB. Please choose one under 5 MB."). Accepted photos show the filename and size in the preview. The form now submits as `multipart/form-data` so photos upload directly to the backend/Cloudinary pipeline.

### volunteer_dashboard.html

Checks for a JWT token in localStorage on load. If there's no token it redirects straight to login. The search bar filters cards by name as you type, and the filter buttons (All / Active / Adopted / Relocated) let you narrow things down quickly.

Logout clears the token and sends you back to the login page.

### admin_dashboard.html

**New for Demo 4.** JWT-protected admin page that loads the full user list from `GET /api/admin/users` and renders it in a sortable table. Each row has:
- A **role dropdown** (Public / Volunteer / Admin) that fires a `PUT` to update the user's role immediately on change.
- A **delete button** with a confirmation dialog — calls `DELETE /api/admin/users/{id}`.

If the JWT is missing the page redirects to login. If any API call returns 401 or 403, the token is cleared and the user is sent back to login with a redirect parameter so they land back on the admin page after signing in. Toast notifications confirm every action.

### login.html

If you already have a valid token stored it skips the login screen entirely. Supports a `?redirect=` parameter so after logging in you land back on the page you were trying to reach.

---

## Known Limitations

- **Photo upload**: Photos are now uploaded via multipart/form-data and stored through Cloudinary. Client-side validation enforces a 5 MB limit and image-type whitelist.
- **Token expiry**: Tokens expire after 24 hours. There's no refresh flow — any 401 or 403 response clears the stored token and redirects to `login.html` with a `?redirect=` parameter so the user lands back where they were.
- **Offline**: No service worker. Needs a connection to work.

---

## A Couple Things That Gave Me Trouble

**GPS on iOS Safari**
iOS Safari blocks `navigator.geolocation` over plain HTTP, so testing on localhost doesn't work on a real phone. The error callback doesn't even fire in some cases — it just silently does nothing. I added ngrok to the setup instructions and made the form fall back to manual entry gracefully so it's not a dead end.

**Keeping the submit button in sync**
The button needed to stay disabled until coordinates were filled in, but coordinates could come from three different places: GPS success, manual input, or the GPS-failed fallback. I ended up centralizing it into one `updateSubmitState()` function that every input and GPS callback runs through, so the button state is always based on what's actually in the fields.

---

## File Structure

```
frontend/
├── index.html                 — landing page with featured dogs
├── dog_profile.html           — dog profile + scrollable sighting history
├── sighting_form.html         — GPS sighting form with photo validation
├── volunteer_dashboard.html   — JWT-protected volunteer dashboard with heatmap
├── admin_dashboard.html       — JWT-protected admin user management
├── login.html                 — volunteer / admin login (JWT)
├── styles.css                 — shared design system
├── config.js                  — API keys (Google Maps)
├── maps.js                    — Google Maps + sighting normalization utils
├── images/                    — placeholder photos and icons
└── README.md
```
