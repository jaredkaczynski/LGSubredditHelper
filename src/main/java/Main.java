import com.github.jreddit.entity.Comment;
import com.github.jreddit.retrieval.Comments;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by razrs on 5/23/2015.
 */
class Main {
    public static void main(String[] args) {
        //String url = args[0];
        performUpdate("lgg2");
    }
    static void performUpdate(String URL){
        XMLScraper test = new XMLScraper();
        String[][] commentInformation = test.returnCommentInformation(URL);
        ImageResize imageResizer = new ImageResize();
        try {
            imageResizer.resizeHomeScreenImage(commentInformation[0][0]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // retrieve image
            BufferedImage bi =  imageToBufferedImage(imageResizer.resizeHomeScreenImage(commentInformation[0][0]));
            File outputfile = new File("saved.png");
            ImageIO.write(bi, "png", outputfile);
        } catch (IOException e) {
        }
    }
    public static BufferedImage imageToBufferedImage(Image im) {
        System.out.println(im.getWidth(null));
        System.out.println(im.getHeight(null));
        BufferedImage bi = new BufferedImage
                (im.getWidth(null),im.getHeight(null),BufferedImage.TYPE_INT_RGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(im, 0, 0, null);
        bg.dispose();
        return bi;
    }
    void isUpdateTime() {

    }

}
