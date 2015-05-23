import com.github.jreddit.entity.Comment;
import com.github.jreddit.retrieval.Comments;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by razrs on 5/23/2015.
 */
class Main {
    public static void main(String[] args) {
        //String url = args[0];

        update("lgg2");
    }

    void isUpdateTime() {

    }
    static void update(String url){
        String subreddit = "lgg2";
        Pattern urlPattern = Pattern.compile("(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)");

        XMLScraper test = new XMLScraper();
        List<Comment> topComments = test.grabTopPosterInfo(subreddit);
        if(topComments.size() == 2){
            Matcher m = urlPattern.matcher(topComments.get(0).getBody());
           if(m.find()){
               System.out.println(m.group());
           }
        //System.out.println(urlPattern.matcher(topComments.get(0).getBody()).find());
        }
    }
}
