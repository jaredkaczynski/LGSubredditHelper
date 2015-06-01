import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by razrs on 5/23/2015.
 */
class Main {
    public static void main(String[] args) {
        if(args.length > 0) {
            performUpdate(args[0]);
        } else {
            performUpdate("lgg2");
        }
    }

    static void performUpdate(String subredditName) {
        XMLScraper test = new XMLScraper();
        ImageResize imageResizer = new ImageResize();
        test.connectUser();

        String[][] currentCommentInformation = test.returnCommentInformation(subredditName, "current");
        //String[][] commentInformation = test.returnCommentInformation(subredditName, "");

        SidebarUpdater sidebarUpdater = new SidebarUpdater(test.getUser());
        try {
            sidebarUpdater.updateSidebar(currentCommentInformation, currentCommentInformation, subredditName, sidebarUpdater.getSubredditInfo(subredditName));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        ImageUploader uploader = new ImageUploader(test.getUser());

        try {
            uploader.uploadImage(imageResizer.fixLink(currentCommentInformation[0][0],"home"), "winner-screenshot", subredditName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            uploader.uploadImage(imageResizer.fixLink(currentCommentInformation[1][0],"header"), "headerimg", subredditName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        try {
            // retrieve image
            BufferedImage bi = imageToBufferedImage(imageResizer.resizeHomeScreenImage(commentInformation[0][0]));
            File outputfile = new File("saved.jpg");
            ImageIO.write(bi, "jpg", outputfile);
        } catch (IOException e) {
        }*/
        /*UpdateWiki wikiUpdater = new UpdateWiki(test.getUser());
       //wikiUpdater.editWikiPage(currentCommentInformation,subredditName);
        SmartCropRequest newSmartCrop = new SmartCropRequest(test.getUser());
        try {
            newSmartCrop.smartCropImage(imageResizer.fixLink(currentCommentInformation[0][0],"header"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public static BufferedImage imageToBufferedImage(Image im) {
        System.out.println(im.getWidth(null));
        System.out.println(im.getHeight(null));
        BufferedImage bi = new BufferedImage
                (im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(im, 0, 0, null);
        bg.dispose();
        return bi;
    }

    void isUpdateTime() {

    }

}
