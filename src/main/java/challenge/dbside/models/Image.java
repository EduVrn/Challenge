package challenge.dbside.models;

import javax.persistence.*;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.annotations.Persister;
import org.hibernate.eav.EAVEntity;
import org.hibernate.eav.EAVGlobalContext;

import challenge.dbside.eav.EAVPersister;
import challenge.webside.imagesstorage.ImageStoreService;

@Entity
@EAVEntity
@Persister(impl = EAVPersister.class)
public class Image extends BaseEntity {

    public Image() {
        super(EAVGlobalContext.getTypeOfEntity(Image.class.getSimpleName().toLowerCase()).getId());
    }

    private String imageRef;
    private Integer isMain;

    private Integer minVersionId;
    private Integer isForComment;

    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }

    public Boolean getIsMain() {
        return isMain == 1;
    }

    public void setIsMain(Boolean isMain) {
        this.isMain = isMain ? 1 : 0;
    }

    public Boolean getIsForComment() {
        return isForComment == 1;
    }

    public void setIsForComment(Boolean isForComment) {
        this.isForComment = isForComment ? 1 : 0;
    }

    public String getBase64() {
        String encoded;
        try {
            encoded = "data:image/jpg;base64," + Base64.encodeBase64String(ImageStoreService.restoreImage(this));
        } catch (Exception ex) {
            encoded = "";
        }
        return encoded;
    }

    public Integer getMinVersionId() {
        return minVersionId;
    }

    public void setMinVersionId(Integer minVersionId) {
        this.minVersionId = minVersionId;
    }

}
