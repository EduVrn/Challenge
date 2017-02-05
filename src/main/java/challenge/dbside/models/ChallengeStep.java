package challenge.dbside.models;

import challenge.dbside.models.common.IdAttrGet;
import challenge.dbside.models.dbentity.DBSource;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ChallengeStep extends BaseEntity implements Commentable {

    public ChallengeStep() {
        super(ChallengeStep.class.getSimpleName());
    }

    public ChallengeStep(DBSource dataSource) {
        super(dataSource);
    }

    @NotNull
    @Size(min = 5, max = 40, message = "{error.name.length}")
    public String getName() {
        return getDataSource().getAttributes().get(IdAttrGet.IdName()).getValue();
    }

    public void setName(String name) {
        getDataSource().getAttributes().get(IdAttrGet.IdName()).setValue(name);
    }

    public String getMessage() {
        return getDataSource().getAttributes().get(IdAttrGet.IdMessage()).getValue();
    }

    public void setMessage(String msg) {
        getDataSource().getAttributes().get(IdAttrGet.IdMessage()).setValue(msg);
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

}
