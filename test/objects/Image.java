package test.objects;

import cz.mg.vulkan.*;
import static cz.mg.vulkan.Vk.*;


public class Image implements AutoCloseable {
    protected final Vk vk;
    protected final VkDevice device;
    protected final VkImage image;
    protected final VkDeviceMemory memory;
    protected final VkImageView view;
    protected final int width;
    protected final int height;
    protected final int mipLevelCount;
    protected final int format;
    protected final int aspect;
    protected int layout;

    public Image(Vk vk, VkPhysicalDevice physicalDevice, VkDevice device, int width, int height, int mipLevelCount, int format, int aspect, int usageFlags, int memoryProperties) {
        this.vk = vk;
        this.device = device;
        this.image = new VkImage();
        this.memory = new VkDeviceMemory();
        this.view = new VkImageView();
        this.width = width;
        this.height = height;
        this.mipLevelCount = mipLevelCount;
        this.format = format;
        this.aspect = aspect;

        VkImageCreateInfo imageCreateInfo = new VkImageCreateInfo();
        imageCreateInfo.setImageType(VK_IMAGE_TYPE_2D);
        imageCreateInfo.setFormat(format);
        imageCreateInfo.setExtent(new VkExtent3D(width, height, 1));
        imageCreateInfo.setArrayLayers(1);
        imageCreateInfo.setMipLevels(mipLevelCount);
        imageCreateInfo.setSamples(VK_SAMPLE_COUNT_1_BIT);
        imageCreateInfo.setTiling(VK_IMAGE_TILING_OPTIMAL);
        imageCreateInfo.setUsage(usageFlags);
        imageCreateInfo.setSharingMode(VK_SHARING_MODE_EXCLUSIVE);
        imageCreateInfo.setInitialLayout(VK_IMAGE_LAYOUT_UNDEFINED);

        vk.vkCreateImageP(device, imageCreateInfo, null, image);

        VkMemoryRequirements imageMemoryRequirements = new VkMemoryRequirements();
        vk.vkGetImageMemoryRequirements(device, image, imageMemoryRequirements);

        int memoryTypeIndex = PhysicalDevice.findMemoryTypeIndex(
                vk, physicalDevice,
                imageMemoryRequirements.getMemoryTypeBitsQ(),
                memoryProperties
        );

        VkMemoryAllocateInfo memoryAllocateInfo = new VkMemoryAllocateInfo();
        memoryAllocateInfo.setMemoryTypeIndex(memoryTypeIndex);
        memoryAllocateInfo.setAllocationSize(imageMemoryRequirements.getSize());

        vk.vkAllocateMemoryP(device, memoryAllocateInfo, null, memory);

        vk.vkBindImageMemoryP(device, image, memory, 0);

        VkImageViewCreateInfo viewCreateInfo = new VkImageViewCreateInfo();
        viewCreateInfo.setImage(image);
        viewCreateInfo.setViewType(VK_IMAGE_VIEW_TYPE_2D);
        viewCreateInfo.setFormat(format);
        viewCreateInfo.getSubresourceRange().setAspectMask(aspect);
        viewCreateInfo.getSubresourceRange().setBaseMipLevel(0);
        viewCreateInfo.getSubresourceRange().setLevelCount(mipLevelCount);
        viewCreateInfo.getSubresourceRange().setBaseArrayLayer(0);
        viewCreateInfo.getSubresourceRange().setLayerCount(1);

        vk.vkCreateImageViewP(device, viewCreateInfo, null, view);
    }

    public void setLayout(int layout, VkCommandPool commandPool, VkQueue queue){
        try (PrimaryCommandBuffer commandBuffer = new PrimaryCommandBuffer(vk, device, commandPool)) {
            commandBuffer.begin(true);

            VkImageMemoryBarrier barrier = new VkImageMemoryBarrier();
            barrier.setOldLayout(this.layout);
            barrier.setNewLayout(layout);
            barrier.setSrcQueueFamilyIndex((int) VK_QUEUE_FAMILY_IGNORED);
            barrier.setDstQueueFamilyIndex((int) VK_QUEUE_FAMILY_IGNORED);
            barrier.setImage(image);
            barrier.getSubresourceRange().setAspectMask(aspect);
            barrier.getSubresourceRange().setBaseMipLevel(0);
            barrier.getSubresourceRange().setLevelCount(mipLevelCount);
            barrier.getSubresourceRange().setBaseArrayLayer(0);
            barrier.getSubresourceRange().setLayerCount(1);

            vk.vkCmdPipelineBarrier(
                    commandBuffer.getCommandBuffer(),
                    VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT,
                    VK_PIPELINE_STAGE_TRANSFER_BIT,
                    0,
                    0,
                    null,
                    0,
                    null,
                    1,
                    barrier
            );

            commandBuffer.end();

            commandBuffer.submit(queue);
        }

        this.layout = layout;
    }

    public void getData(StagingBuffer stagingBuffer, int mipLevel, VkCommandPool commandPool, VkQueue queue){
        try(PrimaryCommandBuffer commandBuffer = new PrimaryCommandBuffer(vk, device, commandPool)){
            commandBuffer.begin(true);

            VkBufferImageCopy copy = new VkBufferImageCopy();
            copy.setBufferOffset(0);
            copy.setBufferRowLength(0);
            copy.setBufferImageHeight(0);
            copy.getImageSubresource().setAspectMask(aspect);
            copy.getImageSubresource().setMipLevel(mipLevel);
            copy.getImageSubresource().setBaseArrayLayer(0);
            copy.getImageSubresource().setLayerCount(1);
            copy.getImageOffset().set(0, 0, 0);
            copy.getImageExtent().set(getMipLevelWidth(mipLevel), getMipLevelHeight(mipLevel), 1);

            vk.vkCmdCopyImageToBuffer(
                    commandBuffer.getCommandBuffer(),
                    image,
                    layout,
                    stagingBuffer.getBuffer(),
                    1,
                    copy
            );

            commandBuffer.end();

            commandBuffer.submit(queue);
        }
    }

    public void setData(StagingBuffer stagingBuffer, int mipLevel, VkCommandPool commandPool, VkQueue queue){
        try(PrimaryCommandBuffer commandBuffer = new PrimaryCommandBuffer(vk, device, commandPool)){
            commandBuffer.begin(true);

            VkBufferImageCopy copy = new VkBufferImageCopy();
            copy.setBufferOffset(0);
            copy.setBufferRowLength(0);
            copy.setBufferImageHeight(0);
            copy.getImageSubresource().setAspectMask(aspect);
            copy.getImageSubresource().setMipLevel(mipLevel);
            copy.getImageSubresource().setBaseArrayLayer(0);
            copy.getImageSubresource().setLayerCount(1);
            copy.getImageOffset().set(0, 0, 0);
            copy.getImageExtent().set(getMipLevelWidth(mipLevel), getMipLevelHeight(mipLevel), 1);

            vk.vkCmdCopyBufferToImage(
                    commandBuffer.getCommandBuffer(),
                    stagingBuffer.getBuffer(),
                    image,
                    layout,
                    1,
                    copy
            );

            commandBuffer.end();

            commandBuffer.submit(queue);
        }
    }

    private int getMipLevelWidth(int mipLevel){
        int width = this.width;
        for(int i = 0; i < mipLevel; i++) width /= 2;
        if(width <= 0) width = 1;
        return width;
    }

    private int getMipLevelHeight(int mipLevel){
        int height = this.height;
        for(int i = 0; i < mipLevel; i++) height /= 2;
        if(height <= 0) height = 1;
        return height;
    }

    public Vk getVk() {
        return vk;
    }

    public VkDevice getDevice() {
        return device;
    }

    public VkImage getImage() {
        return image;
    }

    public VkDeviceMemory getMemory() {
        return memory;
    }

    public VkImageView getView() {
        return view;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMipLevelCount() {
        return mipLevelCount;
    }

    public int getFormat() {
        return format;
    }

    public int getLayout() {
        return layout;
    }

    @Override
    public void close() {
        vk.vkDestroyImageView(device, view, null);
        vk.vkFreeMemory(device, memory, null);
        vk.vkDestroyImage(device, image, null);
    }
}