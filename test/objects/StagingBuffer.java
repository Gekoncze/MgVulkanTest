package test.objects;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkDevice;
import cz.mg.vulkan.VkPhysicalDevice;
import cz.mg.vulkan.VkPointer;
import static cz.mg.vulkan.Vk.*;


public class StagingBuffer extends Buffer {
    public StagingBuffer(Vk vk, VkPhysicalDevice physicalDevice, VkDevice device, int size) {
        super(vk, physicalDevice, device, size, VK_BUFFER_USAGE_TRANSFER_SRC_BIT | VK_BUFFER_USAGE_TRANSFER_DST_BIT, VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
    }

    public VkPointer mapMemory(){
        VkPointer location = new VkPointer();
        vk.vkMapMemoryP(device, memory, 0, size, 0, location);
        return location;
    }

    public void unmapMemory(){
        vk.vkUnmapMemory(device, memory);
    }
}