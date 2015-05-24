

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by razrs on 5/23/2015.
 */
public class ImageResize {


    public Image resizeHomeScreenImage(String urlTest) throws IOException {
        Image image = ImageIO.read(new URL(urlTest));
        Image scaleImage = image.getScaledInstance(116, 204, Image.SCALE_DEFAULT);
        return scaleImage;
    }
    public Image resizeHeaderImage(String urlTest) throws IOException {
        Image image = ImageIO.read(new URL(urlTest));
        Image scaleImage = image.getScaledInstance(image.getWidth(null), image.getHeight(null), Image.SCALE_DEFAULT);
        return scaleImage;
    }
}
