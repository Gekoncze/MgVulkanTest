package cz.mg.vulkan.oop;

import cz.mg.vulkan.*;
import cz.mg.collections.array.Array;


public class Vulkan extends VulkanObject<Vk, VulkanObject> {
    public Vulkan() {
        this.self = new Vk();
        addToResourceManager();
    }

    public Vulkan(String mgVulkanlibraryFilename) {
        this.self = new Vk(mgVulkanlibraryFilename);
        addToResourceManager();
    }

    public Array<String> getAvailableExtensions(){
        VkUInt32 count = new VkUInt32();
        self.vkEnumerateInstanceExtensionPropertiesP(null, count, null);
        VkExtensionProperties.Array extensions = new VkExtensionProperties.Array(count.getValue());
        self.vkEnumerateInstanceExtensionPropertiesP(null, count, extensions);
        Array<String> availableExtensions = new Array<>(extensions.count());
        for(int i = 0; i < extensions.count(); i++) availableExtensions.set(i, extensions.get(i).getExtensionNameQ());
        return availableExtensions;
    }

    public Array<String> getAvailableLayers(){
        VkUInt32 count = new VkUInt32();
        self.vkEnumerateInstanceLayerPropertiesP(count, null);
        VkLayerProperties.Array layers = new VkLayerProperties.Array(count.getValue());
        self.vkEnumerateInstanceLayerPropertiesP(count, layers);
        Array<String> availableLayers = new Array<>(layers.count());
        for(int i = 0; i < layers.count(); i++) availableLayers.set(i, layers.get(i).getLayerNameQ());
        return availableLayers;
    }
}
