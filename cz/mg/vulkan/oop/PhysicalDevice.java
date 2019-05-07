package cz.mg.vulkan.oop;

import cz.mg.vulkan.*;


public class PhysicalDevice extends VulkanObject {
    private final Instance instance;
    private final VkPhysicalDevice vk;

    PhysicalDevice(Instance instance, VkPhysicalDevice vk) {
        this.instance = instance;
        this.vk = vk;
    }

    public VkPhysicalDevice getVk() {
        return vk;
    }

    public VkPhysicalDeviceProperties getProperties(){
        VkPhysicalDeviceProperties properties = new VkPhysicalDeviceProperties();
        instance.getVulkan().getVk().vkGetPhysicalDeviceProperties(vk, properties);
        return properties;
    }

    public VkPhysicalDeviceMemoryProperties getMemoryProperties(){
        VkPhysicalDeviceMemoryProperties memoryProperties = new VkPhysicalDeviceMemoryProperties();
        instance.getVulkan().getVk().vkGetPhysicalDeviceMemoryProperties(vk, memoryProperties);
        return memoryProperties;
    }

    public VkFormatProperties getFormatProperties(int format){
        VkFormatProperties formatProperties = new VkFormatProperties();
        instance.getVulkan().getVk().vkGetPhysicalDeviceFormatProperties(vk, format, formatProperties);
        return formatProperties;
    }

    public VkImageFormatProperties getImageFormatProperties(int format, int type, int tilting, int usage, int flags){
        VkImageFormatProperties imageFormatProperties = new VkImageFormatProperties();
        instance.getVulkan().getVk().vkGetPhysicalDeviceImageFormatProperties(vk, format, type, tilting, usage, flags, imageFormatProperties);
        return imageFormatProperties;
    }

    public VkSparseImageFormatProperties.Array getSparseImageFormatProperties(int format, int type, int samples, int usage, int tilting){
        VkUInt32 count = new VkUInt32();
        instance.getVulkan().getVk().vkGetPhysicalDeviceSparseImageFormatProperties(vk, format, type, samples, usage, tilting, count, null);
        VkSparseImageFormatProperties.Array sparseProperties = new VkSparseImageFormatProperties.Array(count.getValue());
        instance.getVulkan().getVk().vkGetPhysicalDeviceSparseImageFormatProperties(vk, format, type, samples, usage, tilting, count, sparseProperties);
        return sparseProperties;
    }

    public VkPhysicalDeviceFeatures getFeatures(){
        VkPhysicalDeviceFeatures features = new VkPhysicalDeviceFeatures();
        instance.getVulkan().getVk().vkGetPhysicalDeviceFeatures(vk, features);
        return features;
    }
}
