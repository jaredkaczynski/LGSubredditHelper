import com.github.jreddit.entity.Comment;
import com.github.jreddit.entity.Submission;
import com.github.jreddit.entity.User;
import com.github.jreddit.exception.RedditError;
import com.github.jreddit.exception.RetrievalFailedException;
import com.github.jreddit.retrieval.Comments;
import com.github.jreddit.retrieval.Submissions;
import com.github.jreddit.retrieval.params.*;
import com.github.jreddit.utils.restclient.HttpRestClient;
import com.github.jreddit.utils.restclient.RestClient;
import examples.Authentication;
import org.json.simple.parser.ParseException;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class should do all XML Scraping work
 * Things like returning the top post information
 * and whether it is time to update the images or not
 */
public class XMLScraper {
    RestClient restClient = new HttpRestClient();
    User user = new User(restClient, Authentication.getUsername(), Authentication.getPassword());

    public void connectUser() {
        restClient.setUserAgent("User-Agent: LGG Bot (by /u/amdphenom)");
        user = new User(restClient, Authentication.getUsername(), Authentication.getPassword());
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
    }

    /**
     * @param subreddit
     * @return This grabs the top post from the subreddit which should
     * be the modpost if the automoderator is working properly
     */
    public String getLastWeekContestURL(String subreddit) {

        try {


            // Handle to Submissions, which offers the basic API functionality
            Submissions subms = new Submissions(restClient, user);

            List<Submission> submissionsSubreddit = subms.ofSubreddit(subreddit, SubmissionSort.HOT, 1, 10, null, null, false);
            submissionsSubreddit = subms.search("subreddit:" + subreddit + " Photography and Homescreen", null, SearchSort.NEW, TimeSpan.MONTH, -1, 100, null, null, true);
            if (submissionsSubreddit.get(1).getTitle().contains("Photography and Homescreen")) {

                return (submissionsSubreddit.get(1).getIdentifier());
            } else
                return ("Error");

        } catch (RetrievalFailedException e) {
            e.printStackTrace();
        } catch (RedditError e) {
            e.printStackTrace();
        }

        return (null);
    }

    /**
     * @param subreddit
     * @return This grabs the top post from the subreddit which should
     * be the modpost if the automoderator is working properly
     */
    public String getContestURL(String subreddit) {

        try {


            // Handle to Submissions, which offers the basic API functionality
            Submissions subms = new Submissions(restClient, user);

            List<Submission> submissionsSubreddit = subms.ofSubreddit(subreddit, SubmissionSort.HOT, 1, 10, null, null, false);
            submissionsSubreddit = subms.search("subreddit:" + subreddit + " Photography and Homescreen", null, SearchSort.NEW, TimeSpan.MONTH, -1, 100, null, null, true);
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

    public List<Comment> grabTopPosterInfo(String url, String timeSpan) {
        List<Comment> commentsSubmission = new ArrayList<Comment>();
        if (url.equalsIgnoreCase("empty")) {
            return (null);
        }

        try {

            // Handle to Comments, which offers the basic API functionality
            Comments coms = new Comments(restClient, user);

            // Retrieve comments of a submission
            System.out.println("\n============== Basic submission comments ==============");
            if (timeSpan.equalsIgnoreCase("current")) {
                commentsSubmission = coms.ofSubmission(getContestURL(url), null, 0, 0, 100, CommentSort.TOP);
            } else {
                commentsSubmission = coms.ofSubmission(getLastWeekContestURL(url), null, 0, 0, 100, CommentSort.TOP);
            }
            Comments.printCommentTree(commentsSubmission);
            //Collections.reverse(commentsSubmission);
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

    public User getUser() {
        return user;
    }

    public ArrayList<String> imageURL(String URL, String timespan) {
        ArrayList<String> imageURL = new ArrayList<String>();
        Pattern urlPattern = Pattern.compile("(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)");

        XMLScraper test = new XMLScraper();
        List<Comment> topComments = test.grabTopPosterInfo(URL, timespan);
        if (topComments.size() == 2) {
            Matcher m = urlPattern.matcher(topComments.get(0).getBody());
            if (m.find()) {
                System.out.println(m.group());
                imageURL.add(m.group());
            }
            //System.out.println(urlPattern.matcher(topComments.get(0).getBody()).find());
        }
        Matcher m = urlPattern.matcher(topComments.get(1).getBody());
        if (m.find()) {
            System.out.println(m.group());
            imageURL.add(m.group());
        }
        return (imageURL);
    }

    public ArrayList<String> usernameRetrieval(String URL, String timespan) {
        ArrayList<String> usernameArrayList = new ArrayList<String>();
        XMLScraper test = new XMLScraper();
        List<Comment> topComments = test.grabTopPosterInfo(URL, timespan);
        usernameArrayList.add(topComments.get(0).getAuthor());
        usernameArrayList.add(topComments.get(1).getAuthor());
        return (usernameArrayList);
    }

    public ArrayList<String> scoreRetrieval(String URL, String timespan) {
        ArrayList<String> pointsArrayList = new ArrayList<String>();
        XMLScraper test = new XMLScraper();
        List<Comment> topComments = test.grabTopPosterInfo(URL, timespan);
        pointsArrayList.add(topComments.get(0).getScore().toString());
        pointsArrayList.add(topComments.get(1).getScore().toString());
        return (pointsArrayList);
    }

    public String[][] returnCommentInformation(String URL, String timespan) {
        String[][] returnInforArray = new String[3][5];
        ArrayList<String> imageURLArrayList = imageURL(URL, timespan);
        ArrayList<String> usernameArrayList = usernameRetrieval(URL, timespan);
        ArrayList<String> scoreArrayList = scoreRetrieval(URL, timespan);
        returnInforArray[0][0] = imageURLArrayList.get(0).substring(1, imageURLArrayList.get(0).length() - 1);
        returnInforArray[1][0] = imageURLArrayList.get(1).substring(1, imageURLArrayList.get(1).length() - 1);
        returnInforArray[0][1] = usernameArrayList.get(0);
        returnInforArray[1][1] = usernameArrayList.get(1);
        returnInforArray[0][2] = scoreArrayList.get(0);
        returnInforArray[1][2] = scoreArrayList.get(1);
        System.out.println(Arrays.deepToString(returnInforArray));
        return returnInforArray;
    }
}
