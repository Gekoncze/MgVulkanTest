package cz.mg.vulkan.oop;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkQueue;
import cz.mg.vulkan.VkSubmitInfo;


public class Queue extends VulkanObject {
    private final Device device;
    private final VkQueue vk;
    private final int familyIndex;
    private final int index;
    private final Vk v;
    private final VkSubmitInfo submitInfo = new VkSubmitInfo();

    Queue(Device device, VkQueue vk, int familyIndex, int index) {
        this.device = device;
        this.vk = vk;
        this.familyIndex = familyIndex;
        this.index = index;
        this.v = device.getPhysicalDevice().getInstance().getVulkan().getVk();
        addToResourceManager(vk, device);
    }

    public Device getDevice() {
        return device;
    }

    public VkQueue getVk() {
        return vk;
    }

    public int getFamilyIndex() {
        return familyIndex;
    }

    public int getIndex() {
        return index;
    }

    public void submit(CommandBuffer commandBuffer){
        submitInfo.setCommandBufferCount(1);
        submitInfo.setPCommandBuffers(commandBuffer.getVk());
        v.vkQueueSubmitP(vk, 1, submitInfo, null);
    }

    public void waitIdle(){
        v.vkQueueWaitIdleP(vk);
    }
}
