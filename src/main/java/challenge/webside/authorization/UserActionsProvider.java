package challenge.webside.authorization;

import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.ChallengeInstance;
import challenge.dbside.models.User;
import java.util.HashSet;
import java.util.Set;

public class UserActionsProvider {
    
    public static Set<Action> getActionsForProfile(User userWhichMakesRequest, User userWhoseProfileRequested) {
        Set<Action> actions = new HashSet<>();
        actions.add(Action.ACCEPT_CHALLENGE_DEF);
        if (userWhichMakesRequest.getId().equals(userWhoseProfileRequested.getId())) {
            actions.add(Action.EDIT_CHALLENGE);
            actions.add(Action.CREATE_CHALLENGE);
            actions.add(Action.DELETE_CHALLENGE);
            actions.add(Action.THROW_CHALLENGE_DEF);
            actions.add(Action.WATCH_UNACCEPTED_CHALLENGES);
        } else {
            actions.add(Action.THROW_CHALLENGE_FOR_USER);
        }
        return actions;
    }
    
    public static Set<Action> getActionsForChallengeDefinition(User user, ChallengeDefinition challenge) {
        Set<Action> actions = new HashSet<>();
        actions.add(Action.ACCEPT_CHALLENGE_DEF);
        if (user.getId().equals(challenge.getCreator().getId())) {
            actions.add(Action.EDIT_CHALLENGE);
            actions.add(Action.DELETE_CHALLENGE);
        }
        return actions;
    }
    
    public static Set<Action> getActionsForChallengeInstance(User user, ChallengeInstance challenge) {
        Set<Action> actions = new HashSet<>();

        if (user.getId().equals(challenge.getAcceptor().getId())) {
            actions.add(Action.EDIT_CHALLENGE);
            actions.add(Action.DELETE_CHALLENGE);
        }
        return actions;
    }
    
}
