package test;

import cz.mg.collections.list.chainlist.ChainList;
import cz.mg.vulkan.VkPointer;
import cz.mg.vulkan.VkUInt32;
import cz.mg.vulkan.VkUInt8;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;


public class Utilities {
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

    public static VkUInt32.Array createBuffer(byte[] bytes){
        VkUInt8.Array buffer = new VkUInt8.Array(bytes);
        return new VkUInt32.Array(new VkPointer(buffer.getVkAddress()), buffer.count() / 4);
    }

    public static void dataToBufferedImage(VkUInt8.Array data, BufferedImage bufferedImage){
        int bpp = 4;
        for(int x = 0; x < bufferedImage.getWidth(); x++){
            for(int y = 0; y < bufferedImage.getHeight(); y++){
                int i = (x + y*bufferedImage.getWidth())*bpp;
                int r = data.get(i+0).getValue();
                int g = data.get(i+1).getValue();
                int b = data.get(i+2).getValue();
                int a = data.get(i+3).getValue();
                if(r < 0) r += 256;
                if(g < 0) g += 256;
                if(b < 0) b += 256;
                if(a < 0) a += 256;
                bufferedImage.setRGB(x, y, new Color(r, g, b, a).getRGB());
            }
        }
    }

    public static void bufferedImageToData(BufferedImage bufferedImage, VkUInt8.Array data){
        for(int i = 0; i < data.count(); i += 4){
            int x = (i / 4) % bufferedImage.getWidth();
            int y = (i / 4) / bufferedImage.getHeight();
            Color c = new Color(bufferedImage.getRGB(x, y));
            int r = c.getRed();
            int g = c.getGreen();
            int b = c.getBlue();
            int a = c.getAlpha();
            if(r > 127) r -= 256;
            if(g > 127) g -= 256;
            if(b > 127) b -= 256;
            if(a > 127) a -= 256;
            data.setValue(i+0, (byte) r);
            data.setValue(i+1, (byte) g);
            data.setValue(i+2, (byte) b);
            data.setValue(i+3, (byte) a);
        }
    }
}
