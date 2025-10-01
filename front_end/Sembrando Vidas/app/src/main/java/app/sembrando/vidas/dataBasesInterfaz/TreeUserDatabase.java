package app.sembrando.vidas.dataBasesInterfaz;

public class TreeUserDatabase {
    private String tree_id;
    private  String user_id;

    public TreeUserDatabase(){
        tree_id = "tree_id";
        user_id = "user_id";
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
