package challenge.dbside.models;

import challenge.dbside.models.common.IdAttrGet;
import challenge.dbside.models.dbentity.DBSource;

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
    
}
