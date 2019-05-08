package cz.mg.vulkan.oop;

import cz.mg.vulkan.*;
import cz.mg.vulkan.oop.resources.VkDeviceResource;


public class Device extends VulkanObject {
    private final PhysicalDevice physicalDevice;
    private final Vk v;
    private final VkDeviceResource vk;

    public Device(PhysicalDevice physicalDevice, int queueFamilyIndex, int queueCount, float... priorities) {
        if(priorities.length != queueCount) throw new IllegalArgumentException("queue count " + queueCount + " vs priorities length " + priorities);

        this.physicalDevice = physicalDevice;
        this.v = physicalDevice.getInstance().getVulkan().getVk();
        this.vk = new VkDeviceResource(v);

        VkDeviceQueueCreateInfo queueCreateInfo = new VkDeviceQueueCreateInfo();
        queueCreateInfo.setQueueFamilyIndex(queueFamilyIndex);
        queueCreateInfo.setQueueCount(queueCount);
        queueCreateInfo.setPQueuePriorities(new VkFloat.Array(priorities));

        VkDeviceCreateInfo deviceCreateInfo = new VkDeviceCreateInfo();
        deviceCreateInfo.setPEnabledFeatures(new VkPhysicalDeviceFeatures());
        deviceCreateInfo.setQueueCreateInfoCount(1);
        deviceCreateInfo.setPQueueCreateInfos(queueCreateInfo);

        v.vkCreateDeviceP(physicalDevice.getVk(), deviceCreateInfo, null, vk);

        addToResourceManager(vk, physicalDevice);
    }

    public PhysicalDevice getPhysicalDevice() {
        return physicalDevice;
    }

    public VkDevice getVk() {
        return vk;
    }

    public Queue getQueue(int familyIndex, int index){
        VkQueue queue = new VkQueue();
        v.vkGetDeviceQueue(vk, familyIndex, index, queue);
        return new Queue(this, queue, familyIndex, index);
    }

    public void waitIdle(){
        v.vkDeviceWaitIdleP(vk);
    }
}
