package com.fooddelivery.model;

/** Simple lat/lng point. Distance uses the haversine formula (km). */
public record Location(double lat, double lng) {
    private static final double EARTH_RADIUS_KM = 6371.0;

    public double distanceTo(Location other) {
        double dLat = Math.toRadians(other.lat - lat);
        double dLng = Math.toRadians(other.lng - lng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(other.lat))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
