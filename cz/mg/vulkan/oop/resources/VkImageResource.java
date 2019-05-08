package cz.mg.vulkan.oop.resources;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkDevice;
import cz.mg.vulkan.VkImage;


public class VkImageResource extends VkImage implements VulkanResource {
    private boolean closed = false;
    private final Vk v;
    private final VkDevice device;

    public VkImageResource(Vk v, VkDevice device) {
        this.v = v;
        this.device = device;
    }

    @Override
    public void close() {
        if(!closed){
            v.vkDestroyImage(device, this, null);
            closed = true;
        }
    }
}
