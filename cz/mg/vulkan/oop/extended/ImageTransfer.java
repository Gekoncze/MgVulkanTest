package cz.mg.vulkan.oop.extended;

import cz.mg.collections.array.Array2D;
import cz.mg.vulkan.oop.CommandBuffer;
import cz.mg.vulkan.oop.CommandPool;
import cz.mg.vulkan.oop.Image;


public abstract class ImageTransfer {
    protected final Image image;
    protected final StagingBuffer buffer;
    protected final Array2D<CommandBuffer> commandBuffers;

    protected ImageTransfer(Image image, CommandPool commandPool, int aspect, int bpp) {
        this.image = image;
        this.buffer = new StagingBuffer(image.getDevice(), image.getWidth() * image.getHeight() * image.getDepth() * bpp);
        this.commandBuffers = new Array2D<>(image.getMipLevelCount(), image.getArrayLayerCount());
        for(int m = 0; m < image.getMipLevelCount(); m++){
            for(int a = 0; a < image.getArrayLayerCount(); a++){
                commandBuffers.set(m, a, createCommandBuffer(m, a, commandPool, aspect));
            }
        }
    }

    protected abstract CommandBuffer createCommandBuffer(int mipLevel, int arrayLayer, CommandPool commandPool, int aspect);

    protected int getMipLevelWidth(int mipLevel){
        int width = image.getWidth();
        for(int i = 0; i < mipLevel; i++) width /= 2;
        if(width <= 0) width = 1;
        return width;
    }

    protected int getMipLevelHeight(int mipLevel){
        int height = image.getHeight();
        for(int i = 0; i < mipLevel; i++) height /= 2;
        if(height <= 0) height = 1;
        return height;
    }

    protected int getMipLevelDepth(int mipLevel){
        int depth = image.getDepth();
        for(int i = 0; i < mipLevel; i++) depth /= 2;
        if(depth <= 0) depth = 1;
        return depth;
    }

    public StagingBuffer getStagingBuffer() {
        return buffer;
    }
}
