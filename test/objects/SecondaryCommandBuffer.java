package test.objects;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkCommandPool;
import cz.mg.vulkan.VkDevice;
import static cz.mg.vulkan.Vk.*;


public class SecondaryCommandBuffer extends CommandBuffer {
    public SecondaryCommandBuffer(Vk vk, VkDevice device, VkCommandPool commandPool) {
        super(vk, device, commandPool, VK_COMMAND_BUFFER_LEVEL_SECONDARY);
    }
}
