import com.github.jreddit.entity.Comment;
import com.github.jreddit.entity.Submission;
import com.github.jreddit.entity.User;
import com.github.jreddit.exception.RedditError;
import com.github.jreddit.exception.RetrievalFailedException;
import com.github.jreddit.retrieval.Comments;
import com.github.jreddit.retrieval.ExtendedComments;
import com.github.jreddit.retrieval.ExtendedSubmissions;
import com.github.jreddit.retrieval.Submissions;
import com.github.jreddit.retrieval.params.*;
import com.github.jreddit.utils.RedditConstants;
import com.github.jreddit.utils.restclient.HttpRestClient;
import com.github.jreddit.utils.restclient.RestClient;
import examples.Authentication;
import jdk.internal.org.xml.sax.SAXException;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class should do all XML Scraping work
 * Things like returning the top post information
 * and whether it is time to update the images or not
 */
public class XMLScraper {

    /**
     * @param subreddit
     * @return This grabs the top post from the subreddit which should
     * be the modpost if the automoderator is working properly
     */
    public String grabContestURL(String subreddit) {
        String modPostID = null;
        /*
        try {
            String html = Jsoup.connect(url).get().html();
            File fXmlFile = new File(html);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            NodeList items = doc.getElementsByTagName("item");

            NodeList postInfo = items.item(0).getChildNodes();

            modPostURL = postInfo.item(1).getNodeValue();

        } catch (Exception e) {
            e.printStackTrace();
        }*/
        RestClient restClient = new HttpRestClient();
        restClient.setUserAgent("bot/1.0 by name");

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

        try {

            /***************************************************************************************************
             * First: basic API functionality
             */

            // Handle to Submissions, which offers the basic API functionality
            Submissions subms = new Submissions(restClient, user);

            // Retrieve submissions of a submission
            System.out.println("\n============== Basic subreddit submissions ==============");
            List<Submission> submissionsSubreddit = subms.ofSubreddit(subreddit, SubmissionSort.HOT, 1, 10, null, null, false);

            //subms.of
            //printSubmissionsList(submissionsSubreddit);
            if (submissionsSubreddit.get(0).getTitle().contains("Photography and Homescreen")) {
                //System.out.println(submissionsSubreddit.get(0).getIdentifier());
                return (submissionsSubreddit.get(0).getIdentifier());
            } else
                return ("Error");

        } catch (RetrievalFailedException e) {
            e.printStackTrace();
        } catch (RedditError e) {
            e.printStackTrace();
        }

        return (modPostID);
    }

    /*
    public Array grabTopPosterInfo(String url){
        try {
            String html = Jsoup.connect(grabContestURL(url)).get().html();
            File fXmlFile = new File(html);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            NodeList items = doc.getElementsByTagName("item");


        } catch (Exception e) {
            e.printStackTrace();
        }
        return();
    }*/
    public List<Comment>  grabTopPosterInfo(String url) {
        RestClient restClient = new HttpRestClient();
        restClient.setUserAgent("bot/1.0 by name");
        List<Comment> commentsSubmission = null;
        if (url == "Error") {
            return (null);
        }
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

        try {

            /***************************************************************************************************
             * First: basic API functionality
             */

            // Handle to Comments, which offers the basic API functionality
            Comments coms = new Comments(restClient, user);

            // Retrieve comments of a submission
            System.out.println("\n============== Basic submission comments ==============");
            commentsSubmission = coms.ofSubmission(grabContestURL(url), null, 0, 0, 100, CommentSort.TOP);
            Comments.printCommentTree(commentsSubmission);
            Collections.reverse(commentsSubmission);
            System.out.println(commentsSubmission);
            System.out.println(commentsSubmission.get(1).getScore());
        } catch (RetrievalFailedException e) {
            e.printStackTrace();
        } catch (RedditError e) {
            e.printStackTrace();
        }
        /**
         * Here is where I grab the top homescreen related comment
         */
        List<Comment> returnedComments = new ArrayList<Comment>();
        for (int i = 0; i < commentsSubmission.size(); i++) {
            if (commentsSubmission.get(i).getBody().contains("omescreen")) {
                System.out.println(commentsSubmission.get(i));
                returnedComments.add(commentsSubmission.get(i));
                break;
            }
        }
        for (int j = 0; j < commentsSubmission.size(); j++) {
            if (commentsSubmission.get(j).getBody().contains("hoto")) {
                if (!returnedComments.contains(commentsSubmission.get(j))) {
                    returnedComments.add(commentsSubmission.get(j));
                    break;
                }
            }
        }
        System.out.println(returnedComments);
        return (returnedComments);
    }
}
