package cz.mg.vulkan.oop;

import cz.mg.vulkan.*;
import cz.mg.vulkan.oop.resources.VkDebugReportCallbackResource;


public class DebugReportCallback extends VulkanObject {
    private final Instance instance;
    private final Vk v;
    private final VkDebugReportCallbackResource vk;

    public DebugReportCallback(Instance instance, int flags) {
        this.instance = instance;
        this.v = instance.getVulkan().getVk();
        this.vk = new VkDebugReportCallbackResource(v, instance.getVk());

        VkDebugReportCallbackCreateInfoEXT reportCallbackCreateInfo = new VkDebugReportCallbackCreateInfoEXT();
        reportCallbackCreateInfo.setPfnCallback(VkDebug.getDefaultPFNvkDebugReportCallbackEXT());
        reportCallbackCreateInfo.setFlags(flags);
        v.vkCreateDebugReportCallbackEXTP(instance.getVk(), reportCallbackCreateInfo, null, vk);

        addToResourceManager(vk, instance);
    }

    public Instance getInstance() {
        return instance;
    }

    public VkDebugReportCallbackResource getVk() {
        return vk;
    }
}
