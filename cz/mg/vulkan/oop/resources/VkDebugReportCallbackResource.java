package cz.mg.vulkan.oop.resources;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkDebugReportCallbackEXT;
import cz.mg.vulkan.VkInstance;


public class VkDebugReportCallbackResource extends VkDebugReportCallbackEXT implements VulkanResource {
    private boolean closed = false;
    private final Vk v;
    private final VkInstance vkInstance;

    public VkDebugReportCallbackResource(Vk v, VkInstance vkInstance) {
        this.v = v;
        this.vkInstance = vkInstance;
    }

    @Override
    public void close() {
        if(!closed){
            v.vkDestroyDebugReportCallbackEXT(vkInstance, this, null);
            closed = true;
        }
    }
}