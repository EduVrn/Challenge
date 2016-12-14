package challenge.model;

public class Challenge {
    private int id;
    private String imageURL;
    private String title;
    private String date;
    private String shortDescription;
    private String description;

    public int getId() {
        return id;
    }

    public Challenge(int id, String imageURL, String title, String date, String shortDescription, String description) {
        this.id = id;
        this.imageURL = imageURL;
        this.title = title;
        this.date = date;
        this.shortDescription = shortDescription;
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
}
