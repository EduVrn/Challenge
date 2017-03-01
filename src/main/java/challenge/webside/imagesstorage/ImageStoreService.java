package challenge.webside.imagesstorage;

import challenge.dbside.models.Image;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.jcr.Binary;
import javax.jcr.SimpleCredentials;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.core.TransientRepository;
import org.springframework.stereotype.Component;

@Component
public class ImageStoreService {

    private static Repository repository;
    private static Session session;
    private final static String DEFAULT_IMAGE_ROUTE = "defaultImage";
    private final static String DEFAULT_USER_IMAGE_ROUTE = "defaultUserImage";

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

    public static void saveBareImage(File imageFile, String route) throws RepositoryException, FileNotFoundException {
        if (session == null || !session.isLive()) {
            session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        }
        InputStream stream = new BufferedInputStream(new FileInputStream(imageFile));
        Node folder = session.getRootNode();
        Node file = folder.addNode(route, "nt:file");
        Node content = file.addNode("jcr:content", "nt:resource");
        Binary binary = session.getValueFactory().createBinary(stream);
        content.setProperty("jcr:data", binary);
        content.setProperty("jcr:mimeType", "image/jpg");
        session.save();
    }

//default way to save
    public static void saveImage(File imageFile, Image imageEntity) throws Exception {
        String route = "image" + imageEntity.getId();
        saveBareImage(imageFile, route);
        imageEntity.setImageRef(route);
    }

    public static void saveDefaultImage(File imageFile) throws Exception {
        saveBareImage(imageFile, DEFAULT_IMAGE_ROUTE);
    }

    public static void saveDefaultUserImage(File imageFile) throws Exception {
        saveBareImage(imageFile, DEFAULT_USER_IMAGE_ROUTE);
    }

    public static byte[] getDefaultImageRef() throws Exception {
        if (session == null || !session.isLive()) {
            session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        }
        Node folder = session.getRootNode();
        Node file = folder.getNode("default");
        Node content = file.getNode("jcr:content");
        String path = content.getPath();
        Binary bin = session.getNode(path).getProperty("jcr:data").getBinary();
        InputStream stream = bin.getStream();
        return IOUtils.toByteArray(stream);
    }

    public static byte[] restoreImage(Image image) throws Exception {
        if (session == null || !session.isLive()) {
            session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        }
        Node folder = session.getRootNode();
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

    public static String getDEFAULT_IMAGE_ROUTE() {
        return DEFAULT_IMAGE_ROUTE;
    }

    public static String getDEFAULT_USER_IMAGE_ROUTE() {
        return DEFAULT_USER_IMAGE_ROUTE;
    }
}
