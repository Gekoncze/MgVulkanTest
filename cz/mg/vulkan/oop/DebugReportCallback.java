package cz.mg.vulkan.oop;

import cz.mg.vulkan.*;


public class DebugReportCallback extends VulkanObject {
    private final Instance instance;
    private final VkDebugReportCallbackResource vk;

    public DebugReportCallback(Instance instance, int flags) {
        this.instance = instance;
        this.vk = new VkDebugReportCallbackResource(instance.getVulkan().getVk(), instance.getVk());

        VkDebugReportCallbackCreateInfoEXT reportCallbackCreateInfo = new VkDebugReportCallbackCreateInfoEXT();
        reportCallbackCreateInfo.setPfnCallback(VkDebug.getDefaultPFNvkDebugReportCallbackEXT());
        reportCallbackCreateInfo.setFlags(flags);

        vk.vk.vkCreateDebugReportCallbackEXTP(vk.vkInstance, reportCallbackCreateInfo, null, vk);

        addToResourceManager(vk, instance);
    }

    public Instance getInstance() {
        return instance;
    }

    private static class VkDebugReportCallbackResource extends VkDebugReportCallbackEXT implements VulkanResource {
        private final Vk vk;
        private final VkInstance vkInstance;

        public VkDebugReportCallbackResource(Vk vk, VkInstance vkInstance) {
            this.vk = vk;
            this.vkInstance = vkInstance;
        }

        @Override
        public void close() {
            vk.vkDestroyDebugReportCallbackEXT(vkInstance, this, null);
        }
    }
}
