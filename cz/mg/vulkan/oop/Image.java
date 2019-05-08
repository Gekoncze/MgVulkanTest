package cz.mg.vulkan.oop;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkExtent3D;
import cz.mg.vulkan.VkImageCreateInfo;
import cz.mg.vulkan.VkMemoryRequirements;
import cz.mg.vulkan.oop.resources.VkImageResource;
import static cz.mg.vulkan.Vk.*;


public class Image extends VulkanObject {
    private final Device device;
    private final Vk v;
    private final VkImageResource vk;
    private final int type;
    private final int format;
    private final int width;
    private final int height;
    private final int depth;
    private final int mipLevelCount;
    private final int arrayLayerCount;

    public Image(Device device, int type, int format, int width, int height, int depth, int mipLevelCount, int arrayLayerCount, int usage) {
        this.device = device;
        this.v = device.getPhysicalDevice().getInstance().getVulkan().getVk();
        this.vk = new VkImageResource(v, device.getVk());
        this.type = type;
        this.format = format;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.mipLevelCount = mipLevelCount;
        this.arrayLayerCount = arrayLayerCount;

        VkImageCreateInfo imageCreateInfo = new VkImageCreateInfo();
        imageCreateInfo.setImageType(type);
        imageCreateInfo.setFormat(format);
        imageCreateInfo.setExtent(new VkExtent3D(width, height, depth));
        imageCreateInfo.setMipLevels(mipLevelCount);
        imageCreateInfo.setArrayLayers(arrayLayerCount);
        imageCreateInfo.setSamples(VK_SAMPLE_COUNT_1_BIT);
        imageCreateInfo.setTiling(VK_IMAGE_TILING_OPTIMAL);
        imageCreateInfo.setUsage(usage);
        imageCreateInfo.setSharingMode(VK_SHARING_MODE_EXCLUSIVE);
        imageCreateInfo.setInitialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
        v.vkCreateImageP(device.getVk(), imageCreateInfo, null, vk);

        addToResourceManager(vk, device);
    }

    public Device getDevice() {
        return device;
    }

    public VkImageResource getVk() {
        return vk;
    }

    public int getType() {
        return type;
    }

    public int getFormat() {
        return format;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }

    public int getMipLevelCount() {
        return mipLevelCount;
    }

    public int getArrayLayerCount() {
        return arrayLayerCount;
    }

    public void bindMemory(DeviceMemory deviceMemory){
        v.vkBindImageMemoryP(device.getVk(), vk, deviceMemory.getVk(), 0);
    }

    public VkMemoryRequirements getMemoryRequirements(){
        VkMemoryRequirements memoryRequirements = new VkMemoryRequirements();
        v.vkGetImageMemoryRequirements(device.getVk(), vk, memoryRequirements);
        return memoryRequirements;
    }
}
