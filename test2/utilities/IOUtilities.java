package test2.utilities;

import cz.mg.collections.list.chainlist.ChainList;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;


public class IOUtilities {
    public static byte[] loadBytes(Class location, String name) {
        try(InputStream stream = location.getResourceAsStream(name);){
            int bufferSize = 2048;

            ChainList<byte[]> buffers = new ChainList<>();
            byte[] buffer = new byte[bufferSize];
            int lastSize;
            do {
                lastSize = stream.read(buffer);
                buffers.addLast(buffer);
                buffer = new byte[bufferSize];
            } while(lastSize == bufferSize);

            int totalSize = buffers.count()*bufferSize;
            totalSize -= bufferSize - lastSize;

            byte[] bytes = new byte[totalSize];
            int i = 0;
            for(byte[] b : buffers){
                int max = b == buffers.getLast() ? lastSize : bufferSize;
                for(int ii = 0; ii < max; ii++){
                    bytes[i] = b[ii];
                    i++;
                }
            }

            return bytes;
        } catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage loadImage(Class location, String name){
        try {
            return ImageIO.read(location.getResourceAsStream(name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
