package challenge.webside.imagesstorage;

import challenge.dbside.models.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
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

    private Repository repository;

    public ImageStoreService() {
        repository = new TransientRepository();
    }

    public void saveImage(byte[] image, Image imageEntity) throws Exception {
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        InputStream stream = new ByteArrayInputStream(image);

        //save the image
        Node folder = session.getRootNode();
        //use image id instead of image url
        Node file = folder.addNode("image" + imageEntity.getId(), "nt:file");
        imageEntity.setImageRef("image" + imageEntity.getId());
        Node content = file.addNode("jcr:content", "nt:resource");
        Binary binary = session.getValueFactory().createBinary(stream);
        content.setProperty("jcr:data", binary);

        content.setProperty("jcr:mimeType", "image/jpg");
        session.save();
        session.logout();
    }

    //from file
    public void saveImage(File imageFile, Image imageEntity) throws Exception {
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));

        InputStream stream = new BufferedInputStream(new FileInputStream(imageFile));

        //save the image
        Node folder = session.getRootNode();
        //use image id instead of image url
        Node file = folder.addNode("image" + imageEntity.getId(), "nt:file");
        imageEntity.setImageRef("image" + imageEntity.getId());
        Node content = file.addNode("jcr:content", "nt:resource");
        Binary binary = session.getValueFactory().createBinary(stream);
        content.setProperty("jcr:data", binary);

        content.setProperty("jcr:mimeType", "image/jpg");
        session.save();
        session.logout();
    }

    public byte[] restoreImage(Image image) throws Exception {
        Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        Node folder = session.getRootNode();
        Node file = folder.getNode(image.getImageRef());
        Node content = file.getNode("jcr:content");
        String path = content.getPath();
        Binary bin = session.getNode(path).getProperty("jcr:data").getBinary();
        InputStream stream = bin.getStream();
        session.logout();
        return IOUtils.toByteArray(stream);
    }

    public void print() {
        try {
//            byte[] image = restoreImage();
//            OutputStream out = new FileOutputStream("restoredImage.jpg");
//            out.write(image);
//            out.close();
        } catch (Exception e) {

        }
    }
}
