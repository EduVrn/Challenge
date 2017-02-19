package challenge.webside.model.ajax;

public class NameAndImage {

    private String name;
    
    private String image;
    
    private boolean isFriend;

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

    @Override
    public String toString() {
        return "NameAndImage{" + "name=" + name + ", image=" + image + ", isFriend=" + isFriend + '}';
    }
    
}
