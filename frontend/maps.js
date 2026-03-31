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

function buildSightingInfoWindow(sighting) {
    const reported = sighting.reportedAt
        ? new Date(sighting.reportedAt).toLocaleString()
        : "Unknown";

    const notes = sighting.notes?.trim()
        ? sighting.notes
        : "No notes provided";

    return `
        <div style="max-width: 240px;">
            <div><strong>Reported:</strong> ${reported}</div>
            <div style="margin-top: 6px;"><strong>Notes:</strong> ${notes}</div>
        </div>
    `;
}

function renderSightingsHeatmap(sightings) {
    const mapEl = document.getElementById("dashboard-map");
    const emptyEl = document.getElementById("heatmap-empty");

    if (!mapEl || !emptyEl) {
        return;
    }

    const validSightings = sightings.filter(s =>
        Number.isFinite(s.latitude) && Number.isFinite(s.longitude)
    );

    if (validSightings.length === 0) {
        mapEl.style.display = "none";
        emptyEl.style.display = "block";
        emptyEl.textContent = "No sightings available for heatmap.";
        return;
    }

    mapEl.style.display = "block";
    emptyEl.style.display = "none";

    const sorted = sortSightingsNewestFirst(validSightings);
    const latest = sorted[0];

    const map = new google.maps.Map(mapEl, {
        center: { lat: latest.latitude, lng: latest.longitude },
        zoom: 12
    });

    const points = validSightings.map(
        s => new google.maps.LatLng(s.latitude, s.longitude)
    );

    const heatmap = new google.maps.visualization.HeatmapLayer({
        data: points
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

    sorted.forEach(sighting => {
        const marker = new google.maps.Marker({
            position: {
                lat: sighting.latitude,
                lng: sighting.longitude
            },
            map
        });

        const infoWindow = new google.maps.InfoWindow({
            content: buildSightingInfoWindow(sighting)
        });

        marker.addListener("click", () => {
            infoWindow.open(map, marker);
        });
    });
}