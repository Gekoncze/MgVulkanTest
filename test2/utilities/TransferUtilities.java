package test2.utilities;

import cz.mg.vulkan.VkFloat;
import cz.mg.vulkan.VkPointer;
import cz.mg.vulkan.VkUInt8;
import cz.mg.vulkan.oop.CommandPool;
import cz.mg.vulkan.oop.Queue;
import cz.mg.vulkan.oop.extended.StagingBuffer;
import cz.mg.vulkan.oop.extended.Texture2D;
import cz.mg.vulkan.oop.extended.WriteImageTransfer;
import java.awt.*;
import java.awt.image.BufferedImage;


public class TransferUtilities {
    public static void setImageData(Texture2D texture, BufferedImage image, BufferedImage[] mipmaps, CommandPool commandPool, Queue queue){
        WriteImageTransfer transfer = new WriteImageTransfer(texture.getImage(), commandPool, texture.getImageView().getAspect(), 4);
        for(int i = 0; i < mipmaps.length; i++){
            imageToStagingBuffer(mipmaps[i], transfer.getStagingBuffer());
            transfer.write(queue, i, 0);
        }
    }

    public static void imageToStagingBuffer(BufferedImage image, StagingBuffer stagingBuffer){
        int sizeInBytes = image.getWidth() * image.getHeight() * 4;
        if(stagingBuffer.getSize() < sizeInBytes) throw new RuntimeException();
        VkPointer location = stagingBuffer.getDeviceMemory().mapMemory();
        VkUInt8.Array data = new VkUInt8.Array(location, sizeInBytes);
        imageToData(image, data);
        stagingBuffer.getDeviceMemory().unmapMemory();
    }

    public static void stagingBufferToImage(BufferedImage image, StagingBuffer stagingBuffer){
        int sizeInBytes = image.getWidth() * image.getHeight() * 4;
        if(stagingBuffer.getSize() < sizeInBytes) throw new RuntimeException();
        VkPointer location = stagingBuffer.getDeviceMemory().mapMemory();
        VkUInt8.Array data = new VkUInt8.Array(location, sizeInBytes);
        dataToImage(image, data);
        stagingBuffer.getDeviceMemory().unmapMemory();
    }

    public static void floatsToStagingBuffer(VkFloat.Array floats, StagingBuffer stagingBuffer){
        if(stagingBuffer.getSize() < floats.count() * VkFloat.sizeof()) throw new RuntimeException();
        VkPointer location = stagingBuffer.getDeviceMemory().mapMemory();
        VkFloat.Array data = new VkFloat.Array(location, floats.count());
        for(int i = 0; i < data.count(); i++) data.setValue(i, floats.getValue(i));
        stagingBuffer.getDeviceMemory().unmapMemory();
    }

    public static void stagingBufferToFloats(VkFloat.Array floats, StagingBuffer stagingBuffer){
        if(stagingBuffer.getSize() < floats.count() * VkFloat.sizeof()) throw new RuntimeException();
        VkPointer location = stagingBuffer.getDeviceMemory().mapMemory();
        VkFloat.Array data = new VkFloat.Array(location, floats.count());
        for(int i = 0; i < data.count(); i++) floats.setValue(i, data.getValue(i));
        stagingBuffer.getDeviceMemory().unmapMemory();
    }

    private static void imageToData(BufferedImage image, VkUInt8.Array data){
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

    private static void dataToImage(BufferedImage bufferedImage, VkUInt8.Array data){
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
}
