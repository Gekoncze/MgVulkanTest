package test.objects;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkDevice;
import static cz.mg.vulkan.Vk.*;


public class UniformBuffer extends Buffer {
    public UniformBuffer(Vk vk, VkDevice device, int size) {
        super(vk, device, size, VK_BUFFER_USAGE_TRANSFER_DST_BIT | VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT);
    }
}