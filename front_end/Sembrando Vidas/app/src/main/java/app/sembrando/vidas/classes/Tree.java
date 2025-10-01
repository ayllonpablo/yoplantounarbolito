package app.sembrando.vidas.classes;

public class Tree {

    private String id;
    private String name;
    private double lat;
    private double lng;
    private String avatar;
    private String path_photo;
    private String state;

    public Tree(){
        id = "id";
        name = "name";
        lat = 0.0;
        lng = 0.0;
        avatar = "avatar1";
        path_photo = "path_photo";
        state = "state";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPath_photo() {
        return path_photo;
    }

    public void setPath_photo(String path_photo) {
        this.path_photo = path_photo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
