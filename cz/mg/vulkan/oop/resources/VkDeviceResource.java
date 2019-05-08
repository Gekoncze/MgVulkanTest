package cz.mg.vulkan.oop.resources;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkDevice;


public class VkDeviceResource extends VkDevice implements VulkanResource {
    private boolean closed = false;
    private final Vk v;

    public VkDeviceResource(Vk v) {
        this.v = v;
    }

    @Override
    public void close() {
        if(!closed){
            v.vkDeviceWaitIdleP(this);
            v.vkDestroyDevice(this, null);
            closed = true;
        }
    }
}