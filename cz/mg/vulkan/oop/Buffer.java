package cz.mg.vulkan.oop;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkBufferCreateInfo;
import cz.mg.vulkan.VkMemoryRequirements;
import cz.mg.vulkan.oop.resources.VkBufferResource;
import static cz.mg.vulkan.Vk.*;


public class Buffer extends VulkanObject {
    private final Device device;
    private final Vk v;
    private final VkBufferResource vk;
    private final long size;

    public Buffer(Device device, long size, int usage) {
        this.device = device;
        this.v = device.getPhysicalDevice().getInstance().getVulkan().getVk();
        this.vk = new VkBufferResource(v, device.getVk());
        this.size = size;

        VkBufferCreateInfo bufferCreateInfo = new VkBufferCreateInfo();
        bufferCreateInfo.setSize(size);
        bufferCreateInfo.setUsage(usage);
        bufferCreateInfo.setSharingMode(VK_SHARING_MODE_EXCLUSIVE);
        v.vkCreateBufferP(device.getVk(), bufferCreateInfo, null, vk);

        addToResourceManager(vk, device);
    }

    public Device getDevice() {
        return device;
    }

    public VkBufferResource getVk() {
        return vk;
    }

    public long getSize() {
        return size;
    }

    public void bindMemory(DeviceMemory deviceMemory){
        v.vkBindBufferMemoryP(device.getVk(), vk, deviceMemory.getVk(), 0);
    }

    public VkMemoryRequirements getMemoryRequirements(){
        VkMemoryRequirements memoryRequirements = new VkMemoryRequirements();
        v.vkGetBufferMemoryRequirements(device.getVk(), vk, memoryRequirements);
        return memoryRequirements;
    }
}
