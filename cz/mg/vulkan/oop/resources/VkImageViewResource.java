package cz.mg.vulkan.oop.resources;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkDevice;
import cz.mg.vulkan.VkImageView;


public class VkImageViewResource extends VkImageView implements VulkanResource {
    private boolean closed = false;
    private final Vk v;
    private final VkDevice device;

    public VkImageViewResource(Vk v, VkDevice device) {
        this.v = v;
        this.device = device;
    }

    @Override
    public void close() {
        if(!closed){
            v.vkDestroyImageView(device, this, null);
            closed = true;
        }
    }
}
