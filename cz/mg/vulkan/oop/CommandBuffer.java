package cz.mg.vulkan.oop;

import cz.mg.vulkan.*;
import cz.mg.vulkan.oop.resources.VkCommandBufferResource;

import static cz.mg.vulkan.Vk.VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
import static cz.mg.vulkan.Vk.VK_PIPELINE_STAGE_TRANSFER_BIT;


public class CommandBuffer extends VulkanObject {
    private final CommandPool commandPool;
    private final Vk v;
    private final VkCommandBufferResource vk;
    private final VkCommandBufferBeginInfo commandBufferBeginInfo = new VkCommandBufferBeginInfo();

    public CommandBuffer(CommandPool commandPool, int level) {
        this.commandPool = commandPool;
        this.v = commandPool.getDevice().getPhysicalDevice().getInstance().getVulkan().getVk();
        this.vk = new VkCommandBufferResource(v, commandPool.getDevice().getVk(), commandPool.getVk());

        VkCommandBufferAllocateInfo commandBufferAllocateInfo = new VkCommandBufferAllocateInfo();
        commandBufferAllocateInfo.setCommandPool(commandPool.getVk());
        commandBufferAllocateInfo.setLevel(level);
        commandBufferAllocateInfo.setCommandBufferCount(1);
        v.vkAllocateCommandBuffersP(commandPool.getDevice().getVk(), commandBufferAllocateInfo, vk);

        addToResourceManager(vk, commandPool);
    }

    public CommandPool getCommandPool() {
        return commandPool;
    }

    public VkCommandBufferResource getVk() {
        return vk;
    }

    public void begin(int flags){
        commandBufferBeginInfo.setFlags(flags);
        v.vkBeginCommandBufferP(vk, commandBufferBeginInfo);
    }

    public void end(){
        v.vkEndCommandBufferP(vk);
    }

    public void cmdCopyImageToBuffer(Image image, int imageLayout, Buffer buffer, VkBufferImageCopy.Array regions){
        v.vkCmdCopyImageToBuffer(
                vk,
                image.getVk(),
                imageLayout,
                buffer.getVk(),
                regions == null ? 0 : regions.count(),
                regions
        );
    }

    public void cmdCopyBufferToImage(Buffer buffer, Image image, int imageLayout, VkBufferImageCopy.Array regions){
        v.vkCmdCopyBufferToImage(
                vk,
                buffer.getVk(),
                image.getVk(),
                imageLayout,
                regions == null ? 0 : regions.count(),
                regions
        );
    }

    public void cmdPipelineBarrier(int srcStageMask, int dstStageMask, int dependencyFlags, VkMemoryBarrier.Array memoryBarriers, VkBufferMemoryBarrier.Array bufferMemoryBarriers, VkImageMemoryBarrier.Array imageMemoryBarriers){
        v.vkCmdPipelineBarrier(
                vk,
                srcStageMask,
                dstStageMask,
                dependencyFlags,
                memoryBarriers == null ? 0 : memoryBarriers.count(),
                memoryBarriers,
                bufferMemoryBarriers == null ? 0 : bufferMemoryBarriers.count(),
                bufferMemoryBarriers,
                imageMemoryBarriers == null ? 0 : imageMemoryBarriers.count(),
                imageMemoryBarriers
        );
    }
}
