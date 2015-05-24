import com.github.jreddit.entity.User;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.*;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by razrs on 5/23/2015.
 */
public class CSSUpdater {
    User user;

    public CSSUpdater(User tempUser) {
        user = tempUser;
    }

    public void updateSidebar(String subreddit) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost("https://www.reddit.com/api/site_admin");
        MultipartEntity nvps = new MultipartEntity();
        httpPost.setHeader("User-Agent", "User-Agent: LGG Bot (by /u/amdphenom");
        //httpPost.setHeader("Cookie","reddit_session=" + user.getCookie());
        httpPost.addHeader("Cookie", "reddit_session=" + user.getCookie());
        nvps.addPart("r", new StringBody(subreddit));
        nvps.addPart("uh", new StringBody(user.getModhash()));
        nvps.addPart("img_type", new StringBody("png"));
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

    private String getSideBar(String subreddit) throws IOException {
        // The fluent API relieves the user from having to deal with manual deallocation of system
        // resources at the cost of having to buffer response content in memory in some cases.
        //return(Request.Get("http://reddit.com/r/" + subreddit + "/sidebar")
        //        .execute().returnContent().asString());

        //CloseableHttpClient httpclient = HttpClients.createDefault();
        BasicCookieStore cookieStore = new BasicCookieStore();
        BasicClientCookie cookie = new BasicClientCookie("Cookie", "reddit_session=" + user.getCookie());
        cookie.setDomain(".reddit.com");
        cookie.setPath("/");
        cookieStore.addCookie(cookie);
        CloseableHttpClient client = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).setUserAgent("User-Agent: LGG Bot (by /u/amdphenom").build();
        HttpGet httpGet = new HttpGet("http://www.reddit.com/r/" + subreddit + "/about/edit.json" + "?uh=" + user.getModhash());

        CloseableHttpResponse response1 = client.execute(httpGet);
        try {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity1);
        } finally {
            response1.close();
        }
        return response1.toString();
    }


    public void getAndChangeSubredditInfo(String subreddit) throws IOException {
        //String out = new Scanner(new URL("https://www.reddit.com/r/" + subreddit + "/about/edit.json").openStream(), "UTF-8").useDelimiter("\\A").next();
        System.out.println(getSideBar(subreddit) + "Sideafsgs");
        JSONObject obj = new JSONObject(getSideBar(subreddit));
        String[] subredditInfo = new String[30];
        JSONArray arr = obj.getJSONArray("data");
        System.out.println(arr.toString());

        CloseableHttpClient httpclient = HttpClients.createDefault();
        /*
        HttpPost httpPost = new HttpPost("https://www.reddit.com/api/site_admin");
        MultipartEntity nvps = new MultipartEntity();
        httpPost.setHeader("User-Agent","User-Agent: LGG Bot (by /u/amdphenom");
        //httpPost.setHeader("Cookie","reddit_session=" + user.getCookie());
        httpPost.addHeader("Cookie","reddit_session=" + user.getCookie());
        nvps.addPart("r", new StringBody(subreddit));
        nvps.addPart("allow_top", new StringBody("true"));
        nvps.addPart("collapse_deleted_comments", new StringBody("false"));
        nvps.addPart("comment_score_hide_mins", new StringBody("0"));
        nvps.addPart("comment_score_hide_mins", new StringBody("0"));
        nvps.addPart("description",new StringBody(getSideBar(subreddit)));
        nvps.addPart("exclude_banned_modqueue",new StringBody("true"));
        nvps.addPart("header-title",new StringBody("true"));
        CloseableHttpResponse response2 = httpclient.execute(httpPost);

        try {
            System.out.println(response2.getStatusLine());
            HttpEntity entity2 = response2.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity2);
        } finally {
            response2.close();
        }*/
    }

    private String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

}
