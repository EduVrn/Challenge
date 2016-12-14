package challenge.dao;

import challenge.model.Challenge;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class ChallengesDao {

    List<Challenge> challenges;
    Challenge mainChallenge;

    ChallengesDao() {
        challenges = new ArrayList<>();
        mainChallenge = new Challenge(1, "images/race.jpg", "Race till death", "17/11/2016", "screw 'em all", "");
        challenges.add(new Challenge(2, "images/wheely.jpg", "Race till death", "17/11/2016", "screw 'em all", ""));
        challenges.add(new Challenge(3, "images/wheely.jpg", "Byke Stunnin'", "17/11/2016", "Потрюкачить на байке", ""));
        challenges.add(new Challenge(4, "images/jump1.jpg", "Challenge N2", "17/11/2016", "AWESOME challenge", ""));
        challenges.add(new Challenge(5, "images/dive.jpg", "Challenge N4", "17/11/2016", "SCARY challenge", ""));
        challenges.add(new Challenge(6, "images/jump.jpg", "Challenge N5", "17/11/2016", "WOW challenge", ""));
        challenges.add(new Challenge(7, "images/break.png", "Challenge N6", "17/11/2016", "break something", ""));
        challenges.add(new Challenge(8, "images/onTheEdge.jpeg", "Challenge N7", "17/11/2016", "on the hella edge", ""));
        challenges.add(new Challenge(9, "images/onTheEdge.jpeg", "Challenge N7", "17/11/2016", "on the hella edge", ""));
        challenges.add(new Challenge(10, "images/onTheEdge.jpeg", "Challenge N7", "17/11/2016", "on the hella edge", ""));
        challenges.add(new Challenge(11, "images/onTheEdge.jpeg", "Challenge N7", "17/11/2016", "on the hella edge", ""));
    }

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public Challenge getMainChallenge() {
        return mainChallenge;
    }

    public Challenge getChallengeById(int id) {
        if (mainChallenge.getId() == id) {
            return mainChallenge;
        }
        for (Challenge challenge : challenges) {
            if (challenge.getId() == id) {
                return challenge;
            }
        }
        //fixit
        return null;
        //throw new Exception("Challenge not found");
    }
}
