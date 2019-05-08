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

    public static void bufferedImageToData(BufferedImage image, VkUInt8.Array data){
        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                Color c = new Color(image.getRGB(x, y));
                int r = c.getRed();
                int g = c.getGreen();
                int b = c.getBlue();
                int a = c.getAlpha();
                if(r > 127) r -= 256;
                if(g > 127) g -= 256;
                if(b > 127) b -= 256;
                if(a > 127) a -= 256;
                int i = (x + y*image.getWidth())*4;
                data.setValue(i+0, (byte) r);
                data.setValue(i+1, (byte) g);
                data.setValue(i+2, (byte) b);
                data.setValue(i+3, (byte) a);
            }
        }
    }

    public static BufferedImage[] generateMipmapImages(BufferedImage image){
        int mipLevelCount = getMipLevelCount(image);
        BufferedImage[] mipmaps = new BufferedImage[mipLevelCount];
        mipmaps[0] = image;
        int cw = image.getWidth();
        int ch = image.getHeight();
        for(int i = 1; i < mipLevelCount; i++){
            cw /= 2;
            ch /= 2;
            mipmaps[i] = new BufferedImage(cw, ch, BufferedImage.TYPE_4BYTE_ABGR);

            BufferedImage current = mipmaps[i];
            BufferedImage previous = mipmaps[i-1];
            for(int y = 0; y < current.getHeight(); y++){
                for(int x = 0; x < current.getWidth(); x++){
                    int xx = x*2;
                    int yy = y*2;
                    Color c = mix(
                            previous.getRGB(xx, yy),
                            previous.getRGB(xx+1, yy),
                            previous.getRGB(xx, yy+1),
                            previous.getRGB(xx+1, yy+1)
                    );
                    current.setRGB(x, y, c.getRGB());
                }
            }
        }
        return mipmaps;
    }

    private static int getMipLevelCount(BufferedImage image){
        int mipLevelCount = 0;
        int cw = image.getWidth();
        int ch = image.getHeight();
        while(cw > 0 && ch > 0){
            mipLevelCount++;
            cw /= 2;
            ch /= 2;
        }
        if(mipLevelCount <= 0) throw new RuntimeException();
        return mipLevelCount;
    }

    private static Color mix(int c1, int c2, int c3, int c4){
        return mix(
                new Color(c1),
                new Color(c2),
                new Color(c3),
                new Color(c4)
        );
    }

    private static Color mix(Color c1, Color c2, Color c3, Color c4){
        return new Color(
                (c1.getRed() + c2.getRed() + c3.getRed() + c4.getRed()) / 4,
                (c1.getGreen() + c2.getGreen() + c3.getGreen() + c4.getGreen()) / 4,
                (c1.getBlue() + c2.getBlue() + c3.getBlue() + c4.getBlue()) / 4,
                (c1.getAlpha() + c2.getAlpha() + c3.getAlpha() + c4.getAlpha()) / 4
        );
    }
}
