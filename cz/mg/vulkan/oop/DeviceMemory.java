package cz.mg.vulkan.oop;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkMemoryAllocateInfo;
import cz.mg.vulkan.VkMemoryRequirements;
import cz.mg.vulkan.VkPointer;
import cz.mg.vulkan.oop.resources.VkDeviceMemoryResource;


public class DeviceMemory extends VulkanObject {
    private final Device device;
    private final Vk v;
    private final VkDeviceMemoryResource vk;
    private final long size;

    public DeviceMemory(Device device, long size, int memoryTypeIndex) {
        this.device = device;
        this.v = device.getPhysicalDevice().getInstance().getVulkan().getVk();
        this.vk = new VkDeviceMemoryResource(v, device.getVk());
        this.size = size;

        VkMemoryAllocateInfo memoryAllocateInfo = new VkMemoryAllocateInfo();
        memoryAllocateInfo.setMemoryTypeIndex(memoryTypeIndex);
        memoryAllocateInfo.setAllocationSize(size);
        v.vkAllocateMemoryP(device.getVk(), memoryAllocateInfo, null, vk);

        addToResourceManager(vk, device);
    }

    public DeviceMemory(Device device, VkMemoryRequirements memoryRequirements, int memoryProperties){
        this(device, memoryRequirements.getSizeQ(), device.getPhysicalDevice().findMemoryTypeIndex(memoryRequirements.getMemoryTypeBitsQ(), memoryProperties));
    }

    public Device getDevice() {
        return device;
    }

    public VkDeviceMemoryResource getVk() {
        return vk;
    }

    public long getSize() {
        return size;
    }

    public VkPointer mapMemory(){
        VkPointer location = new VkPointer();
        v.vkMapMemoryP(device.getVk(), vk, 0, size, 0, location);
        return location;
    }

    public void unmapMemory(){
        v.vkUnmapMemory(device.getVk(), vk);
    }
}
