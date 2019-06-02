package a1door.monopoly.Boundaries;

public class BoundaryLocation {

    private double lat;
    private double lng;

    public BoundaryLocation() {

    }
    public BoundaryLocation(double lat, double lng) {
        super();
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public double getLng() {
        return lng;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }


}
