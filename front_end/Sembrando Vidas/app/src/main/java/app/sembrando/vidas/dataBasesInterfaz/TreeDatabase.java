package app.sembrando.vidas.dataBasesInterfaz;

public class TreeDatabase {
    private String id;
    private String name;
    private String lat;
    private String lng;
    private String avatar;
    private String path_photo;
    private String state;

    public TreeDatabase(){
        id = "id";
        name = "name";
        lat = "lat";
        lng = "lng";
        avatar = "avatar";
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

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
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
