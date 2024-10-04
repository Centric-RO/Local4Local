/**
 * Default settings for the map view
 */
export const MAP_DEFAULTS = {
    /**
    * Initial zoom level set to 8 to provide a balanced view of the region.
    * 
    * Zoom levels follow a base-2 exponential scale. At zoom level 8:
    * - Resolution ≈ 610 meters per pixel.
    * 
    * For an 800x600 pixel map view:
    * - Width coverage ≈ 488 km
    * - Height coverage ≈ 366 km
    * 
    * This level offers a good balance, showing substantial area with sufficient detail.
    */
    ZOOM_LEVEL: 8,
    /**
   * Scale factor for the map marker icon.
   * 
   * The marker SVG has a size of 24x31 pixels. Applying a scale factor of 0.8:
   * - Width: 24 * 0.8 = 19.2 pixels
   * - Height: 31 * 0.8 = 24.8 pixels
   * 
   * This scaled size is more suitable for map display, ensuring markers are visible without overwhelming the map view.
   */
    MARKER_SCALE: 0.8,
    /**
     * 200 km in meters
     */
    DEFAULT_RADIUS: 200000,

    /**
     * Conversion factor from meters to degrees.
     * 
     * One degree of latitude is approximately 111 kilometers. This conversion factor
     * is used to convert radius in meters to degrees for latitude and longitude calculations.
     */
    METERS_PER_DEGREE: 111000, // Conversion factor from meters to degrees
    /**
     * Coordinate system identifiers.
     * EPSG:4326: Uses latitude and longitude in degrees; suitable for global datasets and GPS.
     * EPSG:3857: Uses meters in a projected coordinate system; suitable for web maps and visualization.
     */
    COORDINATE_SYSTEMS: {
        GEOGRAPHIC: 'EPSG:4326', 
        WEB_MERCATOR: 'EPSG:3857' 
    }
}; 