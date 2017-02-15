package challenge.webside.controllers.util;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

public class ControllerUtil {

    public static Optional<String> getPreviousPageByRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Referer")).map(requestUrl -> "redirect:" + requestUrl);
    }

}
