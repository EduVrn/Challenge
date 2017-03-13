package challenge.dbside.models;

import challenge.dbside.models.common.IdAttrGet;
import challenge.dbside.models.dbentity.DBSource;
import challenge.dbside.models.ini.TypeEntity;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotBlank;

public class ChallengeStep extends BaseEntity implements Commentable {

    public ChallengeStep() {
        super(ChallengeStep.class.getSimpleName());
    }

    public ChallengeStep(DBSource dataSource) {
        super(dataSource);
    }

    @NotNull
    @NotBlank(message = "{error.name.length}")
    @Size(min = 5, max = 40, message = "{error.name.length}")
    public String getName() {
        return getDataSource().getAttributes().get(IdAttrGet.IdName()).getValue();
    }

    public void setName(String name) {
        getDataSource().getAttributes().get(IdAttrGet.IdName()).setValue(name.trim());
    }

    @NotNull
    @NotBlank(message = "{error.name.length}")
    @Size(min = 5, max = 250, message = "{error.description.length}")
    public String getMessage() {
        return getDataSource().getAttributes().get(IdAttrGet.IdMessage()).getValue();
    }

    public void setMessage(String msg) {
        getDataSource().getAttributes().get(IdAttrGet.IdMessage()).setValue(msg.trim());
    }

    public void setInstance(ChallengeInstance challengeInstance) {
        getDataSource().setParent(challengeInstance.getDataSource());
    }

    public ChallengeInstance getInstance() {
        return new ChallengeInstance(getDataSource().getParent());
    }

    public Date getDate() {
        return (Date) getDataSource().getAttributes().get(IdAttrGet.IdDate()).getDateValue();
    }

    public void setDate(Date date) {
        getDataSource().getAttributes().get(IdAttrGet.IdDate()).setDateValue(date);
    }

    public Image getMainImageEntity() {
        Set<DBSource> children = (Set<DBSource>) getDataSource().getChildren();
        for (DBSource childDB : children) {
            if (childDB.getEntityType() == TypeEntity.IMAGE.getValue()) {
                Image currentImage = new Image(childDB);
                if (currentImage.isMain()) {
                    return currentImage;
                }
            }
        }
        return new Image();
    }

    public List<Image> getImageEntities() {
        List<Image> images = new ArrayList<>();
        Set<DBSource> children = (Set<DBSource>) getDataSource().getChildren();
        children.forEach((childDB) -> {
            if (childDB.getEntityType() == TypeEntity.IMAGE.getValue()) {
                images.add(new Image(childDB));
            }
        });
        return images;
    }

    public void addImage(Image image) {
        getDataSource().getChildren().add(image.getDataSource());
    }

    public static final Comparator<ChallengeStep> COMPARE_BY_DATE = (ChallengeStep leftToCompare, ChallengeStep rightToCompare)
            -> leftToCompare.getDate().compareTo(rightToCompare.getDate());

}
