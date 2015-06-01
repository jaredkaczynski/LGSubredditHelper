import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by razrs on 5/23/2015.
 */
public class ImageResize {
    /**
     *
     * @param address
     * @return
     * @throws IOException
     * This expands shortened URLs if needed
     *
     */
    private String expandShortURL(String address) throws IOException {
        URL url = new URL(address);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY); //using proxy may increase latency
        connection.setInstanceFollowRedirects(false);
        connection.connect();
        String expandedURL = connection.getHeaderField("Location");
        connection.getInputStream().close();
        return expandedURL;
    }

    /**
     *
     * @param urlTest
     * @param resizeOption
     * @return
     * @throws IOException
     * This validates and fixes links such as non direct links
     * using google drive or flickr
     * As well as shortened versions of them
     */
    public Image fixLink(String urlTest, String resizeOption) throws IOException {
        if (expandShortURL(urlTest)!=null) {
            urlTest = expandShortURL(urlTest);
        }
        Pattern pattern = Pattern.compile("[0-9][0-9][0-9][0-9]\\\\\\/[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]_[0-9a-z][0-9a-z][0-9a-z][0-9a-z][0-9a-z][0-9a-z][0-9a-z][0-9a-z][0-9a-z][0-9a-z]_h.jpg");

        if (urlTest.endsWith(".jp"))
            urlTest = urlTest + "g";
        if (urlTest.matches("(http|https):\\/\\/(www.|)drive.google.com\\/file\\/[A-z]\\/[A-z0-9]*\\/view") && urlTest.endsWith("view")) {
            urlTest = "https://drive.google.com/uc?export=download&id=" + urlTest.split("(http|https):\\/\\/drive.google.com\\/file\\/[A-z]\\/")[1].split("/view")[0];
        }
        if (urlTest.matches("(http|https):\\/\\/(www.|)www.flickr.com\\/photos\\/camerarec\\/[0-9]*")) {
            Matcher matcher = pattern.matcher(urlTest);
            if (matcher.find()) {
                urlTest = "http://farm8.staticflickr.com/" + matcher.group();
            }

        }
        if(urlTest.matches("(http|https):\\/\\/(www.|)imgur.com\\/[A-Z0-9][A-z0-9]*(\\/|)")){
            if(urlTest.endsWith("/")){
                urlTest.substring(0,urlTest.length()-1);
            }
            urlTest = "http://i." + urlTest.substring(7,urlTest.length()) + ".jpg";
        }
        if (resizeOption.equals("home")) {
            return resizeHomeScreenImage(urlTest);
        } else {
            return resizeHeaderImage(urlTest);
        }
    }

    public Image resizeHomeScreenImage(String urlTest) throws IOException {
        Image image = ImageIO.read(new URL(urlTest));

        Image scaleImage = image.getScaledInstance(116, 204, Image.SCALE_SMOOTH);
        return scaleImage;
    }

    public Image resizeHeaderImage(String urlTest) throws IOException {
        Image image = ImageIO.read(new URL(urlTest));
        Image scaleImage = image.getScaledInstance(image.getWidth(null), image.getHeight(null), Image.SCALE_DEFAULT);
        if (image.getWidth(null) > 2000) {
            scaleImage = image.getScaledInstance(1920, ((1920 * image.getWidth(null)) / image.getHeight(null)), Image.SCALE_SMOOTH);
        }
        Iterator iter = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = (ImageWriter) iter.next();
// instantiate an ImageWriteParam object with default compression options
        ImageWriteParam iwp = writer.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(0.8f);   // an integer between 0 and 1
// 1 specifies minimum compression and maximum quality
        IIOImage compressedImage = new IIOImage(imageToBufferedImage(scaleImage), null, null);
        scaleImage = convertRenderedImage(compressedImage.getRenderedImage());
        return scaleImage;
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

    public BufferedImage convertRenderedImage(RenderedImage img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        ColorModel cm = img.getColorModel();
        int width = img.getWidth();
        int height = img.getHeight();
        WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        Hashtable properties = new Hashtable();
        String[] keys = img.getPropertyNames();
        if (keys != null) {
            for (int i = 0; i < keys.length; i++) {
                properties.put(keys[i], img.getProperty(keys[i]));
            }
        }
        BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
        img.copyData(raster);
        return result;
    }
}
