package pureplus;

import java.io.PipedOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ImageStreamFactory {
    Thread  th;
    
    class ImageOutputThread extends Thread {
        BufferedImage       img;
        PipedOutputStream   outstm;

        ImageOutputThread(BufferedImage image, PipedOutputStream outstm) {
            this.img = image;
            this.outstm = outstm;
        }

        public void run() {
            try {
                ImageIO.write(img, "PNG", outstm);
            } catch(IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

    public PipedInputStream getImageImageStream(BufferedImage image) throws IOException {
        PipedOutputStream   outstm = new PipedOutputStream();
        PipedInputStream    instm = new PipedInputStream(outstm, 4096);

        th = new ImageOutputThread(image, outstm);
        th.start();

        return instm;
    }
}
