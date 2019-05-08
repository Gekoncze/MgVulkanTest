package cz.mg.vulkan.oop;

import cz.mg.collections.array.Array;
import cz.mg.vulkan.*;
import cz.mg.vulkan.oop.resources.VkInstanceResource;


public class Instance extends VulkanObject {
    private final Vulkan vulkan;
    private final Vk v;
    private final VkInstanceResource vk;

    public Instance(Vulkan vulkan, String applicationName, VkVersion applicationVersion, String engineName, VkVersion engineVersion, String[] enabledExtensions, String[] enabledLayers, VkVersion apiVersion) {
        this.vulkan = vulkan;
        this.v = vulkan.getVk();
        this.vk = new VkInstanceResource(v);

        VkApplicationInfo applicationInfo = new VkApplicationInfo();
        applicationInfo.setPApplicationName(applicationName);
        applicationInfo.setApplicationVersion(applicationVersion);
        applicationInfo.setPEngineName(engineName);
        applicationInfo.setEngineVersion(engineVersion);
        applicationInfo.setApiVersion(apiVersion);

        VkInstanceCreateInfo instanceCreateInfo = new VkInstanceCreateInfo();
        instanceCreateInfo.setPApplicationInfo(applicationInfo);
        instanceCreateInfo.setEnabledLayerCount(enabledLayers.length);
        instanceCreateInfo.setPpEnabledLayerNames(new VkString.Array(enabledLayers));
        instanceCreateInfo.setEnabledExtensionCount(enabledExtensions.length);
        instanceCreateInfo.setPpEnabledExtensionNames(new VkString.Array(enabledExtensions));

        v.vkCreateInstanceP(instanceCreateInfo, null, vk);
        v.setInstance(vk);

        addToResourceManager(vk, vulkan);
    }

    public Vulkan getVulkan() {
        return vulkan;
    }

    public VkInstance getVk(){
        return vk;
    }

    public Array<PhysicalDevice> getPhysicalDevices(){
        VkUInt32 count = new VkUInt32();
        v.vkEnumeratePhysicalDevicesP(vk, count, null);
        VkPhysicalDevice.Array physicalDevices = new VkPhysicalDevice.Array(count.getValue());
        v.vkEnumeratePhysicalDevicesP(vk, count, physicalDevices);

        Array<PhysicalDevice> pd = new Array<>(physicalDevices.count());
        for(int i = 0; i < physicalDevices.count(); i++) pd.set(i, new PhysicalDevice(this, physicalDevices.get(i)));
        return pd;
    }
}
