package challenge.dbside.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Persister;
import org.hibernate.eav.EAVEntity;
import org.hibernate.eav.EAVGlobalContext;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import challenge.dbside.eav.EAVPersister;
import challenge.dbside.eav.collection.EAVCollectionPersister;

@Entity
@EAVEntity
@Persister(impl = EAVPersister.class)
public class ChallengeStep extends BaseEntity {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeStep.class);

    public ChallengeStep() {
        super(EAVGlobalContext.getTypeOfEntity(ChallengeStep.class.getSimpleName().toLowerCase()).getId());
        images = new ArrayList();
    }

    private String name;
    private String message;
    private String date;

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinTable(name = "eav_relationship",
            joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
    )
    @Persister(impl = EAVCollectionPersister.class)
    private List<Image> images;

    @NotNull
    @NotBlank(message = "{error.name.blank}")
    @Size(min = 5, max = 40, message = "{error.name.length}")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    @NotNull
    @NotBlank(message = "{error.name.blank}")
    @Size(min = 5, max = 250, message = "{error.description.length}")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message.trim();
    }

    @NotNull
    @Future(message = "{error.date}")
    public Date getDate() {
        try {
            DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
            String ddt = this.date;
            Date result = df.parse(ddt);
            return result;
        } catch (Exception ex) {
            logger.error("null data" + ex.getMessage());
            return (new Date(0));
        }
    }

    public void setDate(Date date) {
        this.date = date.toString();
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public void addImage(Image img) {
        images.add(img);
    }

        public Image getMainImageEntity() {
        for (Image img : images) {
            if (img.getIsMain()) {
                return img;
            }
        }
        //TODO is it possible?
        return new Image();
    }
    
    public static final Comparator<ChallengeStep> COMPARE_BY_DATE = (ChallengeStep leftToCompare, ChallengeStep rightToCompare)
            -> leftToCompare.getDate().compareTo(rightToCompare.getDate());
}
