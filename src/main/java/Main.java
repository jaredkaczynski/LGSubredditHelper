/**
 * Created by razrs on 5/23/2015.
 */
class Main {
    public static void main(String[] args) {
        //String url = args[0];
        String subreddit = "lgg2";
        XMLScraper test = new XMLScraper();
        test.grabContestURL(subreddit);
        test.grabTopPosterInfo(subreddit);
    }

    void isUpdateTime() {

    }

}
