# Stray Care System – Frontend

**Tyler Cox – Frontend Developer**
CST8319 – Software Development Project
Student ID: 041164510
Algonquin College – Demo 3 Submission

---

## What I Built

This is the frontend for the Stray Care System — a web app that lets anyone report a stray dog sighting by scanning a QR code on the dog's collar. No app download, no account needed. You scan, see the dog's profile, tap a button to share your location, and submit. Done in under a minute.

I built five pages:

- **index.html** — the landing page
- **dog_profile.html** — loads after a QR scan, shows the dog's info and sighting history
- **sighting_form.html** — the GPS-enabled form for submitting a sighting
- **volunteer_dashboard.html** — where volunteers manage all the dogs (login required)
- **login.html** — JWT login for volunteers

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
| `GET` | `/api/sightings/dog/{id}` | Dog profile sighting count |
| `POST` | `/api/sightings` | Sighting form |
| `POST` | `/api/auth/login` | Login page |

---

## Pages in Detail

### dog_profile.html

Reads the `?id=` from the URL and fetches the dog's data. If the ID is missing, invalid, or the API returns a 404, it shows a friendly error message instead of breaking. There are three different error states depending on what went wrong.

The sighting count comes from a separate API call. If that one fails it just shows nothing — it doesn't take down the whole page.

The "Report a Sighting" button at the bottom pre-fills the dogId into the sighting form URL automatically.

### sighting_form.html

The main thing here is the GPS button. Tap it, allow location, and it fills in your latitude and longitude automatically. If GPS gets denied or times out, there's a checkbox to enter coordinates manually instead — it doesn't just leave you stuck.

The notes field is optional. The submit button stays disabled until coordinates are filled in. After a successful submit it shows a thank-you screen instead of just resetting the form silently.

Photo upload is wired up with a live preview — the actual Cloudinary integration is on Alex's end.

### volunteer_dashboard.html

Checks for a JWT token in localStorage on load. If there's no token it redirects straight to login. The search bar filters cards by name as you type, and the filter buttons (All / Active / Adopted / Relocated) let you narrow things down quickly.

Logout clears the token and sends you back to the login page.

### login.html

If you already have a valid token stored it skips the login screen entirely. Supports a `?redirect=` parameter so after logging in you land back on the page you were trying to reach.

---

## Known Limitations

- **Photo upload**: The file input and preview work, but the actual upload to Cloudinary is handled by the backend. Right now `photoUrl` is submitted as `null`.
- **Token expiry**: Tokens expire after 24 hours. There's no refresh flow — it just redirects to login.
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
stray-care-system/
├── index.html
├── dog_profile.html
├── sighting_form.html
├── volunteer_dashboard.html
├── login.html
├── styles.css
├── images/
└── README.md
```
