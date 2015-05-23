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

            if (submissionsSubreddit.get(0).getTitle().contains("Photography and Homescreen")) {

                return (submissionsSubreddit.get(0).getIdentifier());
            } else
                return ("Error");

        } catch (RetrievalFailedException e) {
            e.printStackTrace();
        } catch (RedditError e) {
            e.printStackTrace();
        }

        return (null);
    }

    public List<Comment> grabTopPosterInfo(String url) {
        RestClient restClient = new HttpRestClient();
        restClient.setUserAgent("bot/1.0 by name");
        List<Comment> commentsSubmission = new ArrayList<Comment>();
        if (url.equalsIgnoreCase("empty")) {
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
        int requiredsize = 2;
        assert commentsSubmission != null;
        for (Comment aCommentsSubmission : commentsSubmission) {
            if (aCommentsSubmission.getBody().contains("omescreen")) {
                System.out.println(aCommentsSubmission);
                returnedComments.add(aCommentsSubmission);
                break;
            }
        }
        requiredsize = requiredsize - returnedComments.size();
        for (Comment aCommentsSubmission : commentsSubmission) {
            if (aCommentsSubmission.getBody().contains("hoto")) {
                if (!returnedComments.contains(aCommentsSubmission)) {
                    returnedComments.add(aCommentsSubmission);
                    //requiredsize--;
                    break;
                }
            }
        }
        /*if (requiredsize == 1) {
            for (Comment aCommentsSubmission : commentsSubmission) {
                if (aCommentsSubmission.getBody().contains("hoto")) {
                    returnedComments.add(aCommentsSubmission);
                    break;
                }
            }
        }*/
        System.out.println(returnedComments);
        return (returnedComments);
    }
}
