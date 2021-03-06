package challenge.webside.authorization;

import challenge.dbside.models.ChallengeDefinition;
import challenge.dbside.models.ChallengeInstance;
import challenge.dbside.models.User;
import challenge.dbside.models.status.ChallengeInstanceStatus;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class UserActionsProvider {

    public Set<Action> getActionsForProfile(User userWhichMakesRequest, User userWhoseProfileRequested) {
        Set<Action> actions = new HashSet<>();
        if (userWhichMakesRequest.getId().equals(userWhoseProfileRequested.getId())) {
            actions.add(Action.EDIT_CHALLENGE);
            actions.add(Action.CREATE_CHALLENGE);
            actions.add(Action.DELETE_CHALLENGE);
            actions.add(Action.THROW_CHALLENGE_DEF);
            actions.add(Action.WATCH_UNACCEPTED_CHALLENGES);
            actions.add(Action.EDIT_PROFILE);
        } else {
            actions.add(Action.THROW_CHALLENGE_FOR_USER);
            if (!userWhichMakesRequest.getFriends().contains(userWhoseProfileRequested)
                    && !userWhoseProfileRequested.getIncomingFriendRequestSenders().contains(userWhichMakesRequest)
                    && !userWhichMakesRequest.getIncomingFriendRequestSenders().contains(userWhoseProfileRequested)) {
                actions.add(Action.ADD_FRIEND);
            }
        }
        return actions;
    }

    public Set<Action> getActionsForChallengeDefinition(User user, ChallengeDefinition challenge) {
        Set<Action> actions = new HashSet<>();
        if (user != null) {
            boolean canAccept = true;
            List<ChallengeInstance> instances = challenge.getChallengeInstances();
            for (ChallengeInstance chal : instances) {
                if (chal.getAcceptor().equals(user) && chal.getStatus() == ChallengeInstanceStatus.ACCEPTED) {
                    canAccept = false;
                    break;
                }
            }
            if (canAccept) {
                actions.add(Action.ACCEPT_CHALLENGE_DEF);
            }
            if (user.getId().equals(challenge.getCreator().getId())) {
                actions.add(Action.EDIT_CHALLENGE);
                actions.add(Action.DELETE_CHALLENGE);
                actions.add(Action.THROW_CHALLENGE_DEF);
            }
        }
        return actions;
    }

    public Set<Action> getActionsForChallengeInstance(User user, ChallengeInstance challenge) {
        Set<Action> actions = new HashSet<>();
        if (user.getId().equals(challenge.getAcceptor().getId())) {
            if (challenge.getStatus() == ChallengeInstanceStatus.ACCEPTED) {
                actions.add(Action.ADD_STEPS);
            }
            switch (challenge.getStatus()) {
                case ACCEPTED:
                    actions.add(Action.CLOSE_CHALLENGE);
                    break;
                default:
                    break;
            }
        }
        if (user.getSubscriptions().contains(challenge)) {
            if (challenge.getStatus() == ChallengeInstanceStatus.PUT_TO_VOTE) {
                if (challenge.getVotesFor().contains(user) || challenge.getVotesAgainst().contains(user)) {
                    actions.add(Action.WATCH_VOTES);
                } else {
                    actions.add(Action.VOTE_FOR_CHALLENGE);
                }
            } else if (challenge.getStatus() == ChallengeInstanceStatus.COMPLETED || challenge.getStatus() == ChallengeInstanceStatus.FAILED) {
                actions.add(Action.WATCH_VOTES);
            }
        } else {
            if (!user.getId().equals(challenge.getAcceptor().getId()) && 
                    challenge.getStatus() == ChallengeInstanceStatus.ACCEPTED) {
                actions.add(Action.SUBSCRIBE_CHALLENGE);
            }
            switch (challenge.getStatus()) {
                case COMPLETED:
                case FAILED:
                case PUT_TO_VOTE:
                    actions.add(Action.WATCH_VOTES);
                    break;
                default:
                    break;
            }
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
