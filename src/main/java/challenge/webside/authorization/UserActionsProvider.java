package challenge.webside.authorization;

import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.ChallengeInstance;
import challenge.dbside.models.User;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class UserActionsProvider {

    public Set<Action> getActionsForProfile(User userWhichMakesRequest, User userWhoseProfileRequested) {
        Set<Action> actions = new HashSet<>();
        actions.add(Action.ACCEPT_CHALLENGE_DEF);
        if (userWhichMakesRequest.getId().equals(userWhoseProfileRequested.getId())) {
            actions.add(Action.EDIT_CHALLENGE);
            actions.add(Action.CREATE_CHALLENGE);
            actions.add(Action.DELETE_CHALLENGE);
            actions.add(Action.THROW_CHALLENGE_DEF);
            actions.add(Action.WATCH_UNACCEPTED_CHALLENGES);
            actions.add(Action.EDIT_PROFILE);
        } else {
            actions.add(Action.THROW_CHALLENGE_FOR_USER);
        }
        return actions;
    }

    public Set<Action> getActionsForChallengeDefinition(User user, ChallengeDefinition challenge) {
        Set<Action> actions = new HashSet<>();
        actions.add(Action.ACCEPT_CHALLENGE_DEF);
        if (user.getId().equals(challenge.getCreator().getId())) {
            actions.add(Action.EDIT_CHALLENGE);
            actions.add(Action.DELETE_CHALLENGE);
        }
        return actions;
    }

    public Set<Action> getActionsForChallengeInstance(User user, ChallengeInstance challenge) {
        Set<Action> actions = new HashSet<>();
        if (user.getId().equals(challenge.getAcceptor().getId())) {
            actions.add(Action.EDIT_CHALLENGE);
            actions.add(Action.DELETE_CHALLENGE);
        }
        return actions;
    }

    public void canUpdateChallenge(User user, ChallengeDefinition challenge) {
        if (!getActionsForChallengeDefinition(user, challenge).contains(Action.EDIT_CHALLENGE)) {
            throw new AccessDeniedException("You don't have permission to access this page");
        }
    }

    public void canEditProfile(User user, User profileOwner) {
        if (!getActionsForProfile(user, profileOwner).contains(Action.EDIT_PROFILE)) {
            throw new AccessDeniedException("You don't have permission to access this page");
        }
    }
}
