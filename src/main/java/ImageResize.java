
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
        ImageIcon icon = new ImageIcon(urlTest);
        Image image = ImageIO.read(new URL(urlTest));
        Image scaleImage = image.getScaledInstance(116, 204, Image.SCALE_DEFAULT);
        return scaleImage;
    }

    /*public Image resizePhotoImage(String URL) throws IOException {
        ImageIcon icon = new ImageIcon(URL);

            Image image = ImageIO.read(new URL(URL));

        //Image scaleImage = null;
        //if (icon.getIconWidth() > 2600) {
        Image scaleImage = image.getScaledInstance(1920, 1920 * image.getHeight(null) / image.getWidth(null)
                , Image.SCALE_DEFAULT);
        //}
        return scaleImage;
    }*/
}
