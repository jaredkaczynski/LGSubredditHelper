import com.github.jreddit.entity.User;
import com.github.jreddit.utils.restclient.HttpRestClient;
import com.github.jreddit.utils.restclient.RestClient;
import examples.Authentication;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.SetCookie;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.*;

/**
 * Created by razrs on 5/23/2015.
 */
public class ImageUploader {
    private final String USER_AGENT = "User-Agent: LGG Bot (by /u/amdphenom)";

    public void uploadImage(Image imageType, String imageUploadName, String subreddit) throws IOException {
        RestClient restClient = new HttpRestClient();

        restClient.setUserAgent("User-Agent: LGG Bot (by /u/amdphenom)");
        // Connect the user
        User user = new User(restClient, Authentication.getUsername(), Authentication.getPassword());
        try {
            user.connect();
        } catch (IOException e1) {
            System.err.println("I/O Exception occured when attempting to connect user.");
            e1.printStackTrace();
            //return;
        } catch (ParseException e1) {
            System.err.println("I/O Exception occured when attempting to connect user.");
            e1.printStackTrace();
            //return;
        }
        System.out.println(user.getCookie());
        // construct the buffered image
        BufferedImage bImage = new BufferedImage(imageType.getWidth(null), imageType.getHeight(null), BufferedImage.TYPE_INT_RGB);

//obtain it's graphics
        Graphics2D bImageGraphics = bImage.createGraphics();

//draw the Image (image) into the BufferedImage (bImage)
        bImageGraphics.drawImage(imageType, null, null);

// cast it to rendered image
        RenderedImage rImage = (RenderedImage) bImage;
        ImageIO.write(bImage, "png", new File("temp.png"));
        File tempImageFile = new File("temp.png");
        FileBody fileBody = new FileBody(tempImageFile);
        /*final String IMAGE_SEND_ID = "/r/"+subreddit+"/api/upload_sr_img";
        JSONObject object = (JSONObject) restClient.post(
                "r=" + subreddit + "&uh=" + user.getModhash() + "&formid=image-upload" + "&img_type=png" + "&name=" + imageUploadName,
                IMAGE_SEND_ID,
                user.getCookie()
        ).getResponseObject();
        */
        //object.toJSONString().length()
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://reddit.com/r/"+subreddit+"/api/upload_sr_img");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
// The underlying HTTP connection is still held by the response object
// to allow the response content to be streamed directly from the network socket.
// In order to ensure correct deallocation of system resources
// the user MUST call CloseableHttpResponse#close() from a finally clause.
// Please note that if response content is not fully consumed the underlying
// connection cannot be safely re-used and will be shut down and discarded
// by the connection manager.
        try {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity1);
        } finally {
            response1.close();
        }
        HttpPost httpPost = new HttpPost("http://reddit.com/r/"+subreddit+"/api/upload_sr_img");
        CookieStore cookieStore = new BasicCookieStore();
        //Cookie("reddit","Reddit Cookie",user.getCookie());

        BasicClientCookie stdCookie = new BasicClientCookie("RedditCookie", user.getCookie());
        //java.util.List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        //java.util.List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        cookieStore.addCookie(stdCookie);
        MultipartEntity nvps = new MultipartEntity();
        /*httpPost.setHeader("Cookie",user.getCookie());
        httpPost.setHeader("r",subreddit);
        httpPost.setHeader("uh",user.getModhash());
        httpPost.setHeader("formid","image-upload"));
        httpPost.setHeader("img_type","png"));
        httpPost.setHeader("name",imageUploadName));*/
        /*nvps.add(new BasicNameValuePair("Cookie", user.getCookie()));
        nvps.add(new BasicNameValuePair("r",subreddit));
        nvps.add(new BasicNameValuePair("uh",user.getModhash()));
        nvps.add(new BasicNameValuePair("formid","image-upload"));
        nvps.add(new BasicNameValuePair("img_type","png"));
        nvps.add(new BasicNameValuePair("name",imageUploadName));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));*/

        nvps.addPart("Cookie", new StringBody(user.getCookie()));
        nvps.addPart("r", new StringBody(subreddit));
        nvps.addPart("uh", new StringBody(user.getModhash()));
        nvps.addPart("formid", new StringBody("image-upload"));
        nvps.addPart("img_type", new StringBody("png"));
        nvps.addPart("name", new StringBody(imageUploadName));
        nvps.addPart("file",fileBody);
        //httpPost.setEntity(new UrlEncodedFormEntity(nvps));

        //httpPost.setEntity();
        //String stringContent = await response.Content.ReadAsStringAsync();


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


        //System.out.println(Request.Post("http://reddit.com/r/" + subreddit + "/api/upload_sr_img"))

                /*Unirest.post("http://reddit.com/r/" + subreddit + "/api/upload_sr_img")

                .field("file", new File("temp.png"))
                .field("header", "0")
                .field("img_type", "png")
                .field("name", imageUploadName)
                .field("X-Modhash", user.getModhash())
                .field("upload_type", "img")
                .asJson());*/
    }

}
