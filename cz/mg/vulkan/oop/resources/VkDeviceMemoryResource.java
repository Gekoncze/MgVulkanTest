package cz.mg.vulkan.oop.resources;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkDevice;
import cz.mg.vulkan.VkDeviceMemory;


public class VkDeviceMemoryResource extends VkDeviceMemory implements VulkanResource {
    private boolean closed = false;
    private final Vk v;
    private final VkDevice device;

    public VkDeviceMemoryResource(Vk v, VkDevice device) {
        this.v = v;
        this.device = device;
    }

    @Override
    public void close() {
        if(!closed){
            v.vkFreeMemory(device, this, null);
            closed = true;
        }
    }
}
