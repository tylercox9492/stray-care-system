// --- Google Maps Integration Utilities ---

function getGoogleMapsApiKey() {
    const key = window.APP_CONFIG?.GOOGLE_MAPS_API_KEY;
    if (!key) {
        throw new Error("Google Maps API key is missing.");
    }
    return key;
}

function loadGoogleMapsApi() {
    return new Promise((resolve, reject) => {
        if (window.google && window.google.maps) {
            resolve();
            return;
        }

        const existing = document.querySelector('script[data-google-maps="true"]');
        if (existing) {
            existing.addEventListener('load', resolve);
            existing.addEventListener('error', reject);
            return;
        }

        const script = document.createElement('script');
        script.src = `https://maps.googleapis.com/maps/api/js?key=${getGoogleMapsApiKey()}&libraries=visualization`;
        script.async = true;
        script.defer = true;
        script.dataset.googleMaps = "true";
        script.onload = resolve;
        script.onerror = () => reject(new Error("Failed to load Google Maps API."));
        document.head.appendChild(script);
    });
}

function normalizeSighting(raw) {
    return {
        id: raw.id,
        latitude: Number(raw.latitude),
        longitude: Number(raw.longitude),
        notes: raw.notes ?? "",
        reportedAt: raw.reportedAt ?? null,
        photoUrl: raw.photoUrl ?? null,
        verified: raw.verified ?? false,
        dogId: raw.dog?.id ?? null
    };
}

function sortSightingsNewestFirst(sightings) {
    return [...sightings].sort((a, b) => {
        const aTime = a.reportedAt ? new Date(a.reportedAt).getTime() : 0;
        const bTime = b.reportedAt ? new Date(b.reportedAt).getTime() : 0;
        return bTime - aTime;
    });
}

function escapeHtml(value) {
    return String(value ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");
}

function buildCloudinaryThumbnailUrl(photoUrl) {
    if (!photoUrl) {
        return null;
    }

    if (!photoUrl.includes("/res.cloudinary.com/")) {
        return photoUrl;
    }

    if (photoUrl.includes("/upload/")) {
        return photoUrl.replace(
            "/upload/",
            "/upload/c_fill,w_120,h_120,q_auto,f_auto/"
        );
    }

    return photoUrl;
}

function buildSightingInfoWindow(sighting) {
    const reported = sighting.reportedAt
        ? new Date(sighting.reportedAt).toLocaleDateString("en-US", {
            year: "numeric",
            month: "long",
            day: "numeric"
        })
        : "Unknown";

    const notes = sighting.notes?.trim()
        ? sighting.notes
        : "No notes provided";

    const thumbnailUrl = buildCloudinaryThumbnailUrl(sighting.photoUrl);
    const thumbnailMarkup = thumbnailUrl
        ? `
            <div style="margin-top: 10px;">
                <img
                    src="${escapeHtml(thumbnailUrl)}"
                    alt="Sighting photo thumbnail"
                    loading="lazy"
                    style="width: 120px; height: 120px; object-fit: cover; border-radius: 10px; display: block;"
                >
            </div>
        `
        : "";

    return `
        <div style="max-width: 240px;">
            <div><strong>Date:</strong> ${escapeHtml(reported)}</div>
            <div style="margin-top: 6px;"><strong>Notes:</strong> ${escapeHtml(notes)}</div>
            ${thumbnailMarkup}
        </div>
    `;
}

function renderSightingsHeatmap(sightings, options = {}) {
    const {
        mapElementId = "dashboard-map",
        emptyElementId = "heatmap-empty",
        emptyMessage = "No sightings available for heatmap.",
        zoom = 12,
        radius = 28,
        opacity = 0.7,
        compact = false
    } = options;

    const mapEl = document.getElementById(mapElementId);
    const emptyEl = document.getElementById(emptyElementId);

    if (!mapEl || !emptyEl) {
        return;
    }

    const validSightings = sightings.filter(s =>
        Number.isFinite(s.latitude) && Number.isFinite(s.longitude)
    );

    if (validSightings.length === 0) {
        mapEl.style.display = "none";
        emptyEl.style.display = "block";
        emptyEl.textContent = emptyMessage;
        return;
    }

    mapEl.style.display = "block";
    emptyEl.style.display = "none";

    const sorted = sortSightingsNewestFirst(validSightings);
    const latest = sorted[0];

    const map = new google.maps.Map(mapEl, {
        center: { lat: latest.latitude, lng: latest.longitude },
        zoom,
        mapTypeControl: false,
        streetViewControl: !compact,
        fullscreenControl: !compact
    });

    const points = validSightings.map(
        s => new google.maps.LatLng(s.latitude, s.longitude)
    );

    const heatmap = new google.maps.visualization.HeatmapLayer({
        data: points,
        radius,
        opacity
    });

    heatmap.setMap(map);
}

function renderDogSightingsMap(sightings) {
    const mapEl = document.getElementById("dog-map");
    const emptyEl = document.getElementById("dog-map-empty");

    if (!mapEl || !emptyEl) return;

    const validSightings = sightings.filter(s =>
        Number.isFinite(s.latitude) && Number.isFinite(s.longitude)
    );

    if (validSightings.length === 0) {
        mapEl.style.display = "none";
        emptyEl.style.display = "block";
        return;
    }

    const sorted = sortSightingsNewestFirst(validSightings);
    const latest = sorted[0];

    const map = new google.maps.Map(mapEl, {
        center: { lat: latest.latitude, lng: latest.longitude },
        zoom: 14
    });

    const infoWindow = new google.maps.InfoWindow();

    sorted.forEach(sighting => {
        const marker = new google.maps.Marker({
            position: {
                lat: sighting.latitude,
                lng: sighting.longitude
            },
            map,
            title: sighting.reportedAt
                ? `Sighting on ${new Date(sighting.reportedAt).toLocaleDateString("en-US")}`
                : "Sighting"
        });

        marker.addListener("click", () => {
            infoWindow.setContent(buildSightingInfoWindow(sighting));
            infoWindow.open(map, marker);
        });
    });
}
