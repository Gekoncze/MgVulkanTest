package cz.mg.vulkan.oop.extended;

import cz.mg.vulkan.VkImageMemoryBarrier;
import cz.mg.vulkan.oop.CommandBuffer;
import cz.mg.vulkan.oop.CommandPool;
import cz.mg.vulkan.oop.Image;
import cz.mg.vulkan.oop.Queue;
import static cz.mg.vulkan.Vk.*;


public class ImageTransition {
    protected final Image image;
    protected final CommandBuffer commandBuffer;

    public ImageTransition(Image image, CommandPool commandPool, int oldLayout, int newLayout, int aspect) {
        this.image = image;
        this.commandBuffer = createCommandBuffer(commandPool, oldLayout, newLayout, aspect);
    }

    private CommandBuffer createCommandBuffer(CommandPool commandPool, int oldLayout, int newLayout, int aspect){
        CommandBuffer cmds = new CommandBuffer(commandPool, VK_COMMAND_BUFFER_LEVEL_PRIMARY);
        cmds.begin(0);

        VkImageMemoryBarrier.Array barrier = new VkImageMemoryBarrier.Array(1);
        barrier.setOldLayout(oldLayout);
        barrier.setNewLayout(newLayout);
        barrier.setSrcQueueFamilyIndex((int) VK_QUEUE_FAMILY_IGNORED);
        barrier.setDstQueueFamilyIndex((int) VK_QUEUE_FAMILY_IGNORED);
        barrier.setImage(image.getVk());
        barrier.getSubresourceRange().setAspectMask(aspect);
        barrier.getSubresourceRange().setBaseMipLevel(0);
        barrier.getSubresourceRange().setLevelCount(image.getMipLevelCount());
        barrier.getSubresourceRange().setBaseArrayLayer(0);
        barrier.getSubresourceRange().setLayerCount(image.getArrayLayerCount());

        cmds.cmdPipelineBarrier(
                VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT,
                VK_PIPELINE_STAGE_TRANSFER_BIT,
                0,
                null,
                null,
                barrier
        );

        cmds.end();
        return cmds;
    }

    public void relayout(Queue queue){
        queue.submit(commandBuffer);
        queue.waitIdle();
    }
}
