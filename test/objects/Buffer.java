package test.objects;

import cz.mg.vulkan.*;
import static cz.mg.vulkan.Vk.*;


public class Buffer implements AutoCloseable {
    protected final Vk vk;
    protected final VkDevice device;
    protected final VkBuffer buffer;
    protected final VkDeviceMemory memory;
    protected final int size;

    public Buffer(Vk vk, VkPhysicalDevice physicalDevice, VkDevice device, int size, int usage, int memoryProperties) {
        this.vk = vk;
        this.device = device;
        this.buffer = new VkBuffer();
        this.memory = new VkDeviceMemory();
        this.size = size;

        VkBufferCreateInfo bufferCreateInfo = new VkBufferCreateInfo();
        bufferCreateInfo.setSize(size);
        bufferCreateInfo.setUsage(usage);
        bufferCreateInfo.setSharingMode(VK_SHARING_MODE_EXCLUSIVE);
        vk.vkCreateBufferP(device, bufferCreateInfo, null, buffer);

        VkMemoryRequirements bufferMemoryRequirements = new VkMemoryRequirements();
        vk.vkGetBufferMemoryRequirements(device, buffer, bufferMemoryRequirements);

        int memoryTypeIndex = PhysicalDevice.findMemoryTypeIndex(
                vk, physicalDevice,
                bufferMemoryRequirements.getMemoryTypeBitsQ(),
                memoryProperties
        );

        VkMemoryAllocateInfo memoryAllocateInfo = new VkMemoryAllocateInfo();
        memoryAllocateInfo.setAllocationSize(bufferMemoryRequirements.getSize());
        memoryAllocateInfo.setMemoryTypeIndex(memoryTypeIndex);

        vk.vkAllocateMemoryP(device, memoryAllocateInfo, null, memory);

        vk.vkBindBufferMemoryP(device, buffer, memory, 0);
    }

    public VkBuffer getBuffer() {
        return buffer;
    }

    public VkDeviceMemory getMemory() {
        return memory;
    }

    public int getSize() {
        return size;
    }

    public void getData(StagingBuffer stagingBuffer, VkCommandPool commandPool, VkQueue queue){
        try(PrimaryCommandBuffer commandBuffer = new PrimaryCommandBuffer(vk, device, commandPool)){
            commandBuffer.begin(true);

            VkBufferCopy copy = new VkBufferCopy();
            copy.setSrcOffset(0);
            copy.setDstOffset(0);
            copy.setSize(size);

            vk.vkCmdCopyBuffer(
                    commandBuffer.getCommandBuffer(),
                    buffer,
                    stagingBuffer.getBuffer(),
                    1,
                    copy
            );

            commandBuffer.end();

            commandBuffer.submit(queue);
        }
    }

    public void setData(StagingBuffer stagingBuffer, VkCommandPool commandPool, VkQueue queue){
        try(PrimaryCommandBuffer commandBuffer = new PrimaryCommandBuffer(vk, device, commandPool)){
            commandBuffer.begin(true);

            VkBufferCopy copy = new VkBufferCopy();
            copy.setSrcOffset(0);
            copy.setDstOffset(0);
            copy.setSize(size);

            vk.vkCmdCopyBuffer(
                    commandBuffer.getCommandBuffer(),
                    stagingBuffer.getBuffer(),
                    buffer,
                    1,
                    copy
            );

            commandBuffer.end();

            commandBuffer.submit(queue);
        }
    }

    @Override
    public void close() {
        vk.vkFreeMemory(device, memory, null);
        vk.vkDestroyBuffer(device, buffer, null);
    }
}