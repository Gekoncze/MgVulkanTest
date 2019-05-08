package cz.mg.vulkan.oop;

import cz.mg.vulkan.*;


public class PhysicalDevice extends VulkanObject {
    private final Instance instance;
    private final Vk v;
    private final VkPhysicalDevice vk;

    PhysicalDevice(Instance instance, VkPhysicalDevice vk) {
        this.instance = instance;
        this.v = instance.getVulkan().getVk();
        this.vk = vk;
        addToResourceManager(vk, instance);
    }

    public Instance getInstance() {
        return instance;
    }

    public VkPhysicalDevice getVk() {
        return vk;
    }

    public VkPhysicalDeviceProperties getProperties(){
        VkPhysicalDeviceProperties properties = new VkPhysicalDeviceProperties();
        v.vkGetPhysicalDeviceProperties(vk, properties);
        return properties;
    }

    public VkPhysicalDeviceMemoryProperties getMemoryProperties(){
        VkPhysicalDeviceMemoryProperties memoryProperties = new VkPhysicalDeviceMemoryProperties();
        v.vkGetPhysicalDeviceMemoryProperties(vk, memoryProperties);
        return memoryProperties;
    }

    public VkFormatProperties getFormatProperties(int format){
        VkFormatProperties formatProperties = new VkFormatProperties();
        v.vkGetPhysicalDeviceFormatProperties(vk, format, formatProperties);
        return formatProperties;
    }

    public VkImageFormatProperties getImageFormatProperties(int format, int type, int tilting, int usage, int flags){
        VkImageFormatProperties imageFormatProperties = new VkImageFormatProperties();
        v.vkGetPhysicalDeviceImageFormatProperties(vk, format, type, tilting, usage, flags, imageFormatProperties);
        return imageFormatProperties;
    }

    public VkSparseImageFormatProperties.Array getSparseImageFormatProperties(int format, int type, int samples, int usage, int tilting){
        VkUInt32 count = new VkUInt32();
        v.vkGetPhysicalDeviceSparseImageFormatProperties(vk, format, type, samples, usage, tilting, count, null);
        VkSparseImageFormatProperties.Array sparseProperties = new VkSparseImageFormatProperties.Array(count.getValue());
        v.vkGetPhysicalDeviceSparseImageFormatProperties(vk, format, type, samples, usage, tilting, count, sparseProperties);
        return sparseProperties;
    }

    public VkPhysicalDeviceFeatures getFeatures(){
        VkPhysicalDeviceFeatures features = new VkPhysicalDeviceFeatures();
        v.vkGetPhysicalDeviceFeatures(vk, features);
        return features;
    }

    public VkQueueFamilyProperties.Array getQueueFamilyProperties(){
        VkUInt32 count = new VkUInt32();
        v.vkGetPhysicalDeviceQueueFamilyProperties(vk, count, null);
        VkQueueFamilyProperties.Array queueFamilyProperties = new VkQueueFamilyProperties.Array(count.getValue());
        v.vkGetPhysicalDeviceQueueFamilyProperties(vk, count, queueFamilyProperties);
        return queueFamilyProperties;
    }

    public int findQueueFamilyIndex(int requiredFlags){
        VkQueueFamilyProperties.Array queueFamilyProperties = getQueueFamilyProperties();
        for(int i = 0; i < queueFamilyProperties.count(); i++){
            VkQueueFamilyProperties properties = queueFamilyProperties.get(i);
            if((properties.getQueueFlagsQ() & requiredFlags) != requiredFlags) continue;
            return i;
        }
        throw new UnsupportedOperationException("Could not find suitable queue family. (" + Integer.toHexString(requiredFlags) + ")");
    }

    public int findMemoryTypeIndex(int requiredTypes, int requiredProperties){
        VkMemoryType.Array memoryTypes = getMemoryProperties().getMemoryTypesQ();
        for(int i = 0; i < memoryTypes.count(); i++){
            if((requiredTypes & (1 << i)) == 0) continue;
            if((memoryTypes.get(i).getPropertyFlagsQ() & requiredProperties) != requiredProperties) continue;
            return i;
        }
        throw new UnsupportedOperationException("Could not find suitable memory type. (0x" + Integer.toHexString(requiredTypes) + ", 0x" + Integer.toHexString(requiredProperties) + ")");
    }
}
