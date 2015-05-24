import com.github.jreddit.entity.User;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.*;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
        HttpClient client = new DefaultHttpClient();
        URL url = null;
        try {
            url = new URL("http://www.reddit.com/r/" + subreddit + "/about/edit.json");


            HttpGet httpGet = new HttpGet(String.valueOf(url));
            client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, System.getProperty("User-Agent: LGG Bot (by /u/amdphenom"));
            httpGet.addHeader("Cookie", "reddit_session=" + user.getCookie());
            httpGet.addHeader("uh", user.getModhash());

            HttpResponse response = client.execute(httpGet);

            HttpEntity ht = response.getEntity();

            BufferedHttpEntity buf = new BufferedHttpEntity(ht);

            InputStream is = buf.getContent();

            BufferedReader r = new BufferedReader(new InputStreamReader(is));

            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            //System.out.println(total.toString());
            return total.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return("fail");
    }


    public void getAndChangeSubredditInfo(String subreddit) throws IOException, ParseException {
        //String out = new Scanner(new URL("https://www.reddit.com/r/" + subreddit + "/about/edit.json").openStream(), "UTF-8").useDelimiter("\\A").next();

        JSONObject obj = new JSONObject(getSideBar(subreddit));
        ArrayList<String> jsonElementName = new ArrayList();
        ArrayList<String> jsonElementValue = new ArrayList();
        obj = obj.getJSONObject("data");

        System.out.println(obj);
        String[] elementNames = JSONObject.getNames(obj);

        for(int i = 0; i<obj.length(); i++){
            jsonElementName.add(elementNames[i]);
            jsonElementValue.add(obj.get(elementNames[i]).toString());
            //System.out.println(obj.get(elementNames[i]).toString());
        }
        Set keySet = obj.keySet();

        System.out.println("test" + jsonElementName.toString());
        System.out.println("test" + jsonElementValue.toString());
    }

    private String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

}
