package cz.mg.vulkan.oop.resources;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkCommandPool;
import cz.mg.vulkan.VkDevice;


public class VkCommandPoolResource extends VkCommandPool implements VulkanResource {
    private boolean closed = false;
    private final Vk v;
    private final VkDevice device;

    public VkCommandPoolResource(Vk v, VkDevice device) {
        this.v = v;
        this.device = device;
    }

    @Override
    public void close() {
        if(!closed){
            v.vkDestroyCommandPool(device, this, null);
            closed = true;
        }
    }
}