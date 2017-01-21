package challenge.dbside.models;

import challenge.dbside.models.common.IdAttrGet;
import challenge.dbside.models.dbentity.DBSource;
import challenge.webside.imagesstorage.ImageStoreService;
import org.apache.commons.codec.binary.Base64;

public class Image extends BaseEntity {

    public Image() {
        super(Image.class.getSimpleName());
    }

    public Image(DBSource dataSource) {
        super(dataSource);
    }

    public void setImageRef(String imageRef) {
        getDataSource().getAttributes().get(IdAttrGet.IdImgRef()).setValue(imageRef);
    }

    public String getImageRef() {
        return getDataSource().getAttributes().get(IdAttrGet.IdImgRef()).getValue();
    }

    public Boolean isMain() {
        return getDataSource().getAttributes().get(IdAttrGet.IdIsMain()).getBooleanValue();
    }

    public void setIsMain(Boolean isMain) {
        getDataSource().getAttributes().get(IdAttrGet.IdIsMain()).setBooleanValue(isMain);
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
}
