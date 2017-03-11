package challenge.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;
import org.springframework.core.io.ClassPathResource;

public class AccessProp {

    private static AccessProp properties;

    private AccessProp() {
        property = new Properties();
    }

    private Properties property;

    public static AccessProp getProperties() throws FileNotFoundException, IOException {
        if (properties == null) {
            InputStream input = new ClassPathResource("application.properties").getInputStream();
            properties = new AccessProp();
            properties.setInput(input);
        }
        return properties;
    }

    private void setInput(InputStream input) throws IOException {
        property.load(input);
    }

    public String getCurrentVersionDB() {
        return property.getProperty("dbversion");
    }

    public String getStructureDBPath() {

        return property.getProperty("dbversion.filePath");
    }

}
