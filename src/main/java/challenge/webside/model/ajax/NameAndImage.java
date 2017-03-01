package challenge.webside.model.ajax;

public class NameAndImage {

    private String name;
    
    private String image;
    
    private boolean isFriend;
    
    private boolean isSubscriber;
    
    private boolean isSubscripted;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }

    public boolean getIsSubscriber() {
        return isSubscriber;
    }

    public void setIsSubscriber(boolean isSubscriber) {
        this.isSubscriber = isSubscriber;
    }

    public boolean isIsSubscripted() {
        return isSubscripted;
    }

    public void setIsSubscripted(boolean isSubscripted) {
        this.isSubscripted = isSubscripted;
    }

    @Override
    public String toString() {
        return "NameAndImage{" + "name=" + name + ", image=" + image + ", isFriend=" + isFriend + ", isSubscriber=" + isSubscriber + ", isSubscripted=" + isSubscripted + '}';
    }

}
