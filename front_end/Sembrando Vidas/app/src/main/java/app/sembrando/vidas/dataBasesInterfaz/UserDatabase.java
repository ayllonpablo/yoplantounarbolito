package app.sembrando.vidas.dataBasesInterfaz;

public class UserDatabase {


    private String firstname;
    private String lastname;
    private String nick;
    private String email;
    private String phone;
    private String password;
    private String password_confirmation;
    private String points;
    private String age;
    private String organization;

    public UserDatabase(){
        firstname = "firstname";
        lastname = "lastname";
        nick = "nick";
        email = "email";
        phone = "phone";
        password = "password";
        points = "points";
        password_confirmation = "password_confirmation";
        age = "age";
        organization = "organization";
    }

    /*public String getName() {
        return firstname;
    }

    public void setName(String name) {
        this.firstname = name;
    }

    public String getLast_name() {
        return lastname;
    }

    public void setLast_name(String last_name) {
        this.lastname = last_name;
    }*/

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword_confirmation() {
        return password_confirmation;
    }

    public void setPassword_confirmation(String password_confirmation) {
        this.password_confirmation = password_confirmation;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getAge() {
        return age;
    }

    public String getOrganization() {
        return organization;
    }
}

