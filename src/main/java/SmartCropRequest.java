import com.github.jreddit.entity.User;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

/**
 * Created by razrs on 5/27/2015.
 */
public class SmartCropRequest {
    private final String USER_AGENT = "User-Agent: LGG Bot (by /u/amdphenom)";
    User user;
    public SmartCropRequest(User userTemp) {
        user = userTemp;
    }


    public void smartCropImage(Image imageType) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        BufferedImage bImage = new BufferedImage(imageType.getWidth(null), imageType.getHeight(null), BufferedImage.TYPE_INT_RGB);

//obtain it's graphics
        Graphics2D bImageGraphics = bImage.createGraphics();

//draw the Image (image) into the BufferedImage (bImage)
        bImageGraphics.drawImage(imageType, null, null);

// cast it to rendered image

        RenderedImage rImage = bImage;
        ImageIO.write(bImage, "jpg", new File("temp.jpg"));
        File tempImageFile = new File("temp.jpg");
        //FileBody fileBody = new FileBody(tempImageFile,"image/jpeg");

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost("http://croppola.com/croppola/image.jpg");
        byte[] fileContent = Files.readAllBytes(tempImageFile.toPath());
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("aspectRatio", "3.2");
        builder.addTextBody("width", "90%");
        builder.addTextBody("algorithm", "croppola");
        builder.addBinaryBody("yourimage.jpg", fileContent);

        HttpEntity multipart = builder.build();

        uploadFile.setEntity(multipart);

        HttpResponse response = httpclient.execute(uploadFile);
        HttpEntity resEntity = response.getEntity();
    }

    //?aspectRatio=1&minimumWidth=80%&minimumHeight=80%&maximumWidth=80%&maximumHeight=80%&algorithm=croppola&localId=14e1e20515ca
}
