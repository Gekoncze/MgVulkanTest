package cz.mg.vulkan.oop.resources;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkInstance;


public class VkInstanceResource extends VkInstance implements VulkanResource {
    private boolean closed = false;
    private final Vk v;

    public VkInstanceResource(Vk v) {
        this.v = v;
    }

    @Override
    public void close() {
        if(!closed){
            v.vkDestroyInstance(this, null);
            closed = true;
        }
    }
}