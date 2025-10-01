package app.sembrando.vidas.classes;

public class TreeUser {
    private String tree_id;
    private  String user_id;

    public TreeUser(){
        tree_id = "0";
        user_id = "0";
    }

    public String getTree_id() {
        return tree_id;
    }

    public void setTree_id(String tree_id) {
        this.tree_id = tree_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
