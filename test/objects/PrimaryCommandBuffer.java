package test.objects;

import cz.mg.vulkan.*;
import static cz.mg.vulkan.Vk.*;


public class PrimaryCommandBuffer extends CommandBuffer {
    public PrimaryCommandBuffer(Vk vk, VkDevice device, VkCommandPool commandPool) {
        super(vk, device, commandPool, VK_COMMAND_BUFFER_LEVEL_PRIMARY);
    }

    public void begin(){
        begin(false);
    }

    public void begin(boolean once){
        VkCommandBufferBeginInfo commandBufferBeginInfo = new VkCommandBufferBeginInfo();
        commandBufferBeginInfo.setFlags(once ? VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT : 0);
        vk.vkBeginCommandBufferP(commandBuffer, commandBufferBeginInfo);
    }

    public void end(){
        vk.vkEndCommandBufferP(commandBuffer);
    }

    public void submit(VkQueue queue){
        VkSubmitInfo submitInfo = new VkSubmitInfo();
        submitInfo.setCommandBufferCount(1);
        submitInfo.setPCommandBuffers(commandBuffer);
        vk.vkQueueSubmitP(queue, 1, submitInfo, null);
        vk.vkQueueWaitIdleP(queue);
    }
}
