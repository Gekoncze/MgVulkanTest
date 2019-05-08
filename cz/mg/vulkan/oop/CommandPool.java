package cz.mg.vulkan.oop;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkCommandPool;
import cz.mg.vulkan.VkCommandPoolCreateInfo;
import cz.mg.vulkan.oop.resources.VkCommandPoolResource;


public class CommandPool extends VulkanObject {
    private final Device device;
    private final Vk v;
    private final VkCommandPoolResource vk;

    public CommandPool(Device device, int queueFamilyIndex, int flags) {
        this.device = device;
        this.v = device.getPhysicalDevice().getInstance().getVulkan().getVk();
        this.vk = new VkCommandPoolResource(v, device.getVk());

        VkCommandPoolCreateInfo commandPoolCreateInfo = new VkCommandPoolCreateInfo();
        commandPoolCreateInfo.setFlags(flags);
        commandPoolCreateInfo.setQueueFamilyIndex(queueFamilyIndex);
        v.vkCreateCommandPoolP(device.getVk(), commandPoolCreateInfo, null, vk);

        addToResourceManager(vk, device);
    }

    public Device getDevice() {
        return device;
    }

    public VkCommandPool getVk() {
        return vk;
    }
}
