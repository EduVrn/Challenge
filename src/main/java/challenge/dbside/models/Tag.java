package challenge.dbside.models;

import challenge.dbside.models.common.IdAttrGet;
import challenge.dbside.models.dbentity.DBSource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Tag extends BaseEntity {

    public Tag() {
        super(Tag.class.getSimpleName());
    }

    public Tag(DBSource dataSource) {
        super(dataSource);
    }

    public void setName(String name) {
        getDataSource().getAttributes().get(IdAttrGet.IdName()).setValue(name);
    }

    public String getName() {
        return getDataSource().getAttributes().get(IdAttrGet.IdName()).getValue();
    }

    public List<ChallengeDefinition> getChallenges() {
        List<DBSource> list = (List<DBSource>) getDataSource().getBackRel().get(IdAttrGet.refChallengeDefTag());
        List<ChallengeDefinition> challenges = new ArrayList<>();
        if (list != null) {
            for (DBSource ds : list) {
                challenges.add(new ChallengeDefinition(ds));
            }
        }
        return challenges;
    }
    
    public void removeChallenge(ChallengeDefinition challenge) {
        getDataSource().getBackRel().removeMapping(IdAttrGet.refChallengeDefTag(), challenge.getDataSource());
    }
    
    public static final Comparator<Tag> COMPARE_BY_COUNT = (Tag left, Tag right)
            -> Integer.signum(right.getChallenges().size() - left.getChallenges().size());
}
