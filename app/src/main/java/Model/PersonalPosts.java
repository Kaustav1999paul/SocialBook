package Model;

public class PersonalPosts  {

private String postimage, title;


public PersonalPosts(){}

    public PersonalPosts(String postimage, String title) {
        this.postimage = postimage;
        this.title = title;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
