package cz.mg.vulkan.oop.resources;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkCommandBuffer;
import cz.mg.vulkan.VkCommandPool;
import cz.mg.vulkan.VkDevice;


public class VkCommandBufferResource extends VkCommandBuffer implements VulkanResource {
    private boolean closed = false;
    private final Vk v;
    private final VkDevice device;
    private final VkCommandPool commandPool;

    public VkCommandBufferResource(Vk v, VkDevice device, VkCommandPool commandPool) {
        this.v = v;
        this.device = device;
        this.commandPool = commandPool;
    }

    @Override
    public void close() {
        if(!closed){
            v.vkFreeCommandBuffers(device, commandPool, 1, this);
            closed = true;
        }
    }
}