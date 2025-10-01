package app.sembrando.vidas.dataBasesInterfaz;

public class ActionDatabase {
    private String firstname;
    private String lat;
    private String lng;
    private String user_id;
    private String tree_id;
    private String path_photo;

    public String getFirstname() {
        return firstname;
    }

    public String getPoints() {
        return points;
    }

    private String points;

    public ActionDatabase(){
        firstname = "name";
        user_id = "userId";
        tree_id= "treeId";
        lng = "longitude";
        lat = "latitude";
        points= "";
    }

    public String getName() {
        return firstname;
    }

    public void setName(String name) {
        this.firstname = name;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTree_id() {
        return tree_id;
    }

    public void setTree_id(String tree_id) {
        this.tree_id = tree_id;
    }

    public String getPath_photo() {
        return path_photo;
    }

    public void setPath_photo(String path_photo) {
        this.path_photo = path_photo;
    }
}
