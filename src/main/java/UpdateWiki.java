import com.github.jreddit.entity.User;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by razrs on 5/26/2015.
 */
public class UpdateWiki {

    private final String USER_AGENT = "User-Agent: LGG Bot (by /u/amdphenom)";
    User user;
    public UpdateWiki(User userTemp) {
        user = userTemp;
    }

    public void editWikiPage(String[][] currentCommentInformation, String subreddit) throws IOException {


        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://www.reddit.com/r/"+subreddit+"/api/wiki/edit");
        MultipartEntity nvps = new MultipartEntity();
        httpPost.addHeader("User-Agent","User-Agent: LGG Bot (by /u/amdphenom");
        httpPost.addHeader("Cookie","reddit_session=" + user.getCookie());
        nvps.addPart("r", new StringBody(subreddit));
        nvps.addPart("uh", new StringBody(user.getModhash()));
        nvps.addPart("formid", new StringBody("image-upload"));
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
