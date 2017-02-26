package challenge.webside.controllers.util;

import challenge.dbside.models.Tag;
import challenge.dbside.services.ini.MediaService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class TagsUtil {
    
    @Autowired
    @Qualifier("storageServiceUser")
    private MediaService serviceEntity;

    public List<Tag> filterTags(String filter) {
        List<Tag> allTags = serviceEntity.getAll(Tag.class);
        List<Tag> filteredTags = new ArrayList<>();
        for (Tag tag : allTags) {
            String name = tag.getName();
            if (name.toLowerCase().startsWith(filter.toLowerCase())) {
                filteredTags.add(tag);
            }
        }
        return filteredTags;
    }
    
    public void setModelForTags(HttpServletRequest request, Principal currentUser, Model model) {
        List<Tag> tags = serviceEntity.getAll(Tag.class);
        Collections.sort(tags, Tag.COMPARE_BY_COUNT);
        model.addAttribute("tags", tags);
    }
}
