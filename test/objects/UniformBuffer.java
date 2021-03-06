package test.objects;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkDevice;
import cz.mg.vulkan.VkPhysicalDevice;

import static cz.mg.vulkan.Vk.*;


public class UniformBuffer extends Buffer {
    public UniformBuffer(Vk vk, VkPhysicalDevice physicalDevice, VkDevice device, int size) {
        super(vk, physicalDevice, device, size, VK_BUFFER_USAGE_TRANSFER_DST_BIT | VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT, VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
    }
}