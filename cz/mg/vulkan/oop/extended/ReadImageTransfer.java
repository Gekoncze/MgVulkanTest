package cz.mg.vulkan.oop.extended;

import cz.mg.vulkan.VkBufferImageCopy;
import cz.mg.vulkan.oop.CommandBuffer;
import cz.mg.vulkan.oop.CommandPool;
import cz.mg.vulkan.oop.Image;
import cz.mg.vulkan.oop.Queue;
import static cz.mg.vulkan.Vk.*;


public class ReadImageTransfer extends ImageTransfer {
    public ReadImageTransfer(Image image, CommandPool commandPool, int aspect, int bpp) {
        super(image, commandPool, aspect, bpp);
    }

    @Override
    protected CommandBuffer createCommandBuffer(int mipLevel, int arrayLayer, CommandPool commandPool, int aspect){
        CommandBuffer cmds = new CommandBuffer(commandPool, VK_COMMAND_BUFFER_LEVEL_PRIMARY);
        cmds.begin(0);

        VkBufferImageCopy.Array region = new VkBufferImageCopy.Array(1);
        region.setBufferOffset(0);
        region.setBufferRowLength(0);
        region.setBufferImageHeight(0);
        region.getImageSubresource().setAspectMask(aspect);
        region.getImageSubresource().setMipLevel(mipLevel);
        region.getImageSubresource().setBaseArrayLayer(arrayLayer);
        region.getImageSubresource().setLayerCount(1);
        region.getImageOffset().set(0, 0, 0);
        region.getImageExtent().set(getMipLevelWidth(mipLevel), getMipLevelHeight(mipLevel), getMipLevelDepth(mipLevel));

        cmds.cmdCopyImageToBuffer(
                image,
                VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL,
                buffer,
                region
        );

        cmds.end();
        return cmds;
    }

    public void read(Queue queue, int mipLevel, int arrayLayer){
        queue.submit(commandBuffers.get(mipLevel, arrayLayer));
        queue.waitIdle();
    }
}
