package Model;

public class Search {

    private String Email, Name, URL, UserID;

    public Search(){}

    public Search(String email, String name, String URL, String userID) {
        Email = email;
        Name = name;
        this.URL = URL;
        UserID = userID;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

}
