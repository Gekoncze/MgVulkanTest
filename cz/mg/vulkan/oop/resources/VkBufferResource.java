package cz.mg.vulkan.oop.resources;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkBuffer;
import cz.mg.vulkan.VkDevice;


public class VkBufferResource extends VkBuffer implements VulkanResource {
    private boolean closed = false;
    private final Vk v;
    private final VkDevice device;

    public VkBufferResource(Vk v, VkDevice device) {
        this.v = v;
        this.device = device;
    }

    @Override
    public void close() {
        if(!closed){
            v.vkDestroyBuffer(device, this, null);
            closed = true;
        }
    }
}
