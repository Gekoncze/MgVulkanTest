package test2.utilities;

import cz.mg.vulkan.oop.CommandPool;
import cz.mg.vulkan.oop.Device;
import cz.mg.vulkan.oop.Queue;
import cz.mg.vulkan.oop.extended.Texture2D;
import java.awt.*;
import java.awt.image.BufferedImage;
import static cz.mg.vulkan.Vk.*;


public class ImageUtilities {
    public static Texture2D bufferedImageToTexture(BufferedImage image, Device device, CommandPool commandPool, Queue queue){
        BufferedImage[] mipmaps = ImageUtilities.generateMipmaps(image);
        Texture2D texture = new Texture2D(device, image.getWidth(), image.getHeight(), mipmaps.length, 1);
        texture.setLayout(commandPool, queue, VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL);
        TransferUtilities.setImageData(texture, image, mipmaps, commandPool, queue);
        texture.setLayout(commandPool, queue, VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);
        return texture;
    }

    public static BufferedImage[] generateMipmaps(BufferedImage image){
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
