package challenge.webside.imagesstorage;

import challenge.dbside.models.Image;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.jcr.Binary;
import javax.jcr.SimpleCredentials;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.Node;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.core.TransientRepository;
import org.springframework.stereotype.Component;

@Component
public class ImageStoreService {

    private static Repository repository;
    private static Session session;

    public ImageStoreService() {
        repository = new TransientRepository();
    }

    static {
        repository = new TransientRepository();
    }

    public static void login() throws Exception {
        session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
    }

    public static void saveImage(byte[] image, Image imageEntity) throws Exception {
        if (session == null || !session.isLive()) {
            session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        }
        InputStream stream = new ByteArrayInputStream(image);

        //save the image
        Node folder = session.getRootNode();
        Node file = folder.addNode("image" + imageEntity.getId(), "nt:file");
        imageEntity.setImageRef("image" + imageEntity.getId());
        Node content = file.addNode("jcr:content", "nt:resource");
        Binary binary = session.getValueFactory().createBinary(stream);
        content.setProperty("jcr:data", binary);

        content.setProperty("jcr:mimeType", "image/jpg");
        session.save();
    }

    //from file
    public static void saveImage(File imageFile, Image imageEntity) throws Exception {
        if (session == null || !session.isLive()) {
            session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        }
        InputStream stream = new BufferedInputStream(new FileInputStream(imageFile));

        //save the image
        Node folder = session.getRootNode();
        Node file = folder.addNode("image" + imageEntity.getId(), "nt:file");
        imageEntity.setImageRef("image" + imageEntity.getId());
        Node content = file.addNode("jcr:content", "nt:resource");
        Binary binary = session.getValueFactory().createBinary(stream);
        content.setProperty("jcr:data", binary);

        content.setProperty("jcr:mimeType", "image/jpg");
        session.save();
    }

    public static byte[] restoreImage(Image image) throws Exception {
        if (session == null || !session.isLive()) {
            session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        }
        Node folder = session.getRootNode();
        String ref = image.getImageRef();
        Node file = folder.getNode(image.getImageRef());
        Node content = file.getNode("jcr:content");
        String path = content.getPath();
        Binary bin = session.getNode(path).getProperty("jcr:data").getBinary();
        InputStream stream = bin.getStream();
        return IOUtils.toByteArray(stream);
    }
    
    public static void logout() {
        if (session != null) {
            session.logout();
        }
    }
}
