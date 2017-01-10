package challenge.webside.authorization.thymeleaf;

import challenge.webside.authorization.Action;
import java.util.Set;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractConditionalVisibilityAttrProcessor;

public class AuthorizationTagProcessor extends AbstractConditionalVisibilityAttrProcessor {

    private Set<Action> actions;

    public AuthorizationTagProcessor() {
        super("can");
    }

    @Override
    public int getPrecedence() {
        return 300;
    }

    @Override
    protected boolean isVisible(Arguments arguments, Element element, String attributeName) {
        //i.e. "CREATE_CHALLENGE"
        final String attributeValue = element.getAttributeValue(attributeName);
        return (actions.contains(Action.valueOf(attributeValue)));
    }

    public void setActions(Set<Action> actions) {
        this.actions = actions;
    }
}
