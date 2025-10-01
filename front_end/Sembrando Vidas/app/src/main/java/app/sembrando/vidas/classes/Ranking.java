package app.sembrando.vidas.classes;

public class Ranking {

    private String name_user;
    private String name_tree;
    private String points;
    private int avatar;
    private int indice;

    public Ranking(int indice, String name_user, String name_tree, String points, int avatar) {
        this.name_user = name_user;
        this.name_tree = name_tree;
        this.points = points;
        this.avatar = avatar;
        this.indice = indice;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public String getName_user() {
        return name_user;
    }

    public void setName_user(String name_user) {
        this.name_user = name_user;
    }

    public String getName_tree() {
        return name_tree;
    }

    public void setName_tree(String name_tree) {
        this.name_tree = name_tree;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

}
