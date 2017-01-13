package challenge.webside.authorization.thymeleaf;

import challenge.webside.authorization.Action;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

@Component
public class AuthorizationDialect extends AbstractDialect {

    private static final Set<IProcessor> processors = new HashSet<>();

    static {
        processors.add(new AuthorizationTagProcessor());
    }

    public AuthorizationDialect() {
        super();
    }

    @Override
    public String getPrefix() {
        return "auth";
    }

    @Override
    public Set<IProcessor> getProcessors() {
        return Collections.unmodifiableSet(processors);
    }

    public void setActions(Set<Action> actions) {
        processors.forEach((processor)
                -> ((AuthorizationTagProcessor) processor).setActions(actions));
    }

}
