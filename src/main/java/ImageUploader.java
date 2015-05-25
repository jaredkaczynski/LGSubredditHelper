import com.github.jreddit.entity.User;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
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

/**
 * Created by razrs on 5/23/2015.
 */
public class ImageUploader {
    private final String USER_AGENT = "User-Agent: LGG Bot (by /u/amdphenom)";
    User user;
    public ImageUploader(User userTemp) {
        user = userTemp;
    }

    public void uploadImage(Image imageType, String imageUploadName, String subreddit) throws IOException {
        System.out.println(user.getCookie());
        // construct the buffered image
        BufferedImage bImage = new BufferedImage(imageType.getWidth(null), imageType.getHeight(null), BufferedImage.TYPE_INT_RGB);

//obtain it's graphics
        Graphics2D bImageGraphics = bImage.createGraphics();

//draw the Image (image) into the BufferedImage (bImage)
        bImageGraphics.drawImage(imageType, null, null);

// cast it to rendered image
        RenderedImage rImage = bImage;
        ImageIO.write(bImage, "jpg", new File("temp.jpg"));
        File tempImageFile = new File("temp.jpg");
        FileBody fileBody = new FileBody(tempImageFile);
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost("https://www.reddit.com/r/"+subreddit+"/api/upload_sr_img");
        MultipartEntity nvps = new MultipartEntity();
        httpPost.setHeader("User-Agent","User-Agent: LGG Bot (by /u/amdphenom");
        httpPost.addHeader("Cookie","reddit_session=" + user.getCookie());
        nvps.addPart("r", new StringBody(subreddit));
        nvps.addPart("uh", new StringBody(user.getModhash()));
        nvps.addPart("formid", new StringBody("image-upload"));
        nvps.addPart("img_type", new StringBody("jpg"));
        nvps.addPart("name", new StringBody(imageUploadName));
        nvps.addPart("file",fileBody);
        httpPost.setEntity(nvps);

        CloseableHttpResponse response2 = httpclient.execute(httpPost);

        try {
            System.out.println(response2.getStatusLine());
            HttpEntity entity2 = response2.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity2);
        } finally {
            response2.close();
        }

    }

}
