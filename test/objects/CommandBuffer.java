package test.objects;

import cz.mg.vulkan.*;


public class CommandBuffer implements AutoCloseable {
    protected final Vk vk;
    protected final VkDevice device;
    protected final VkCommandBuffer commandBuffer;
    protected final VkCommandPool commandPool;

    public CommandBuffer(Vk vk, VkDevice device, VkCommandPool commandPool, int level) {
        this.vk = vk;
        this.device = device;
        this.commandBuffer = new VkCommandBuffer();
        this.commandPool = commandPool;

        VkCommandBufferAllocateInfo commandBufferAllocateInfo = new VkCommandBufferAllocateInfo();
        commandBufferAllocateInfo.setCommandPool(commandPool);
        commandBufferAllocateInfo.setLevel(level);
        commandBufferAllocateInfo.setCommandBufferCount(1);

        vk.vkAllocateCommandBuffersP(device, commandBufferAllocateInfo, commandBuffer);
    }

    public VkCommandBuffer getCommandBuffer() {
        return commandBuffer;
    }

    @Override
    public void close() {
        vk.vkFreeCommandBuffers(device, commandPool, 1, commandBuffer);
    }
}
