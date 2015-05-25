

import org.eclipse.jgit.lib.BitmapIndex;
import org.eclipse.jgit.lib.BitmapIndex.Bitmap;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;


/**
 * Created by razrs on 5/23/2015.
 */
public class ImageResize {


    public Image resizeHomeScreenImage(String urlTest) throws IOException {
        if (urlTest.endsWith(".jp"))
            urlTest = urlTest + "g";
        Image image = ImageIO.read(new URL(urlTest));

        Image scaleImage = image.getScaledInstance(116, 204, Image.SCALE_DEFAULT);
        return scaleImage;
    }
    public Image resizeHeaderImage(String urlTest) throws IOException {
        if (urlTest.endsWith(".jp"))
            urlTest = urlTest + "g";
        Image image = ImageIO.read(new URL(urlTest));
        Image scaleImage = image.getScaledInstance(image.getWidth(null), image.getHeight(null), Image.SCALE_DEFAULT);
        if(image.getWidth(null)>2000){
            scaleImage = image.getScaledInstance(1920,((1080 * image.getWidth(null))/image.getHeight(null)),Image.SCALE_FAST);
        }
        Iterator iter = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = (ImageWriter)iter.next();
// instantiate an ImageWriteParam object with default compression options
        ImageWriteParam iwp = writer.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(0.5f);   // an integer between 0 and 1
// 1 specifies minimum compression and maximum quality
        IIOImage compressedImage = new IIOImage(imageToBufferedImage(scaleImage),null,null);
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
            return (BufferedImage)img;
        }
        ColorModel cm = img.getColorModel();
        int width = img.getWidth();
        int height = img.getHeight();
        WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        Hashtable properties = new Hashtable();
        String[] keys = img.getPropertyNames();
        if (keys!=null) {
            for (int i = 0; i < keys.length; i++) {
                properties.put(keys[i], img.getProperty(keys[i]));
            }
        }
        BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
        img.copyData(raster);
        return result;
    }
}
