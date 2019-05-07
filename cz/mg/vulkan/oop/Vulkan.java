package cz.mg.vulkan.oop;

import cz.mg.vulkan.*;
import cz.mg.collections.array.Array;


public class Vulkan extends VulkanObject {
    private final Vk vk;

    public Vulkan() {
        this.vk = new Vk();
        addToResourceManager(vk, null);
    }

    public Vulkan(String mgVulkanlibraryFilename) {
        this.vk = new Vk(mgVulkanlibraryFilename);
        addToResourceManager(vk, null);
    }

    public Vk getVk() {
        return vk;
    }

    public Array<String> getAvailableExtensions(){
        VkUInt32 count = new VkUInt32();
        vk.vkEnumerateInstanceExtensionPropertiesP(null, count, null);
        VkExtensionProperties.Array extensions = new VkExtensionProperties.Array(count.getValue());
        vk.vkEnumerateInstanceExtensionPropertiesP(null, count, extensions);
        Array<String> availableExtensions = new Array<>(extensions.count());
        for(int i = 0; i < extensions.count(); i++) availableExtensions.set(i, extensions.get(i).getExtensionNameQ());
        return availableExtensions;
    }

    public Array<String> getAvailableLayers(){
        VkUInt32 count = new VkUInt32();
        vk.vkEnumerateInstanceLayerPropertiesP(count, null);
        VkLayerProperties.Array layers = new VkLayerProperties.Array(count.getValue());
        vk.vkEnumerateInstanceLayerPropertiesP(count, layers);
        Array<String> availableLayers = new Array<>(layers.count());
        for(int i = 0; i < layers.count(); i++) availableLayers.set(i, layers.get(i).getLayerNameQ());
        return availableLayers;
    }
}
