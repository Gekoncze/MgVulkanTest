package test.objects;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkDevice;
import cz.mg.vulkan.VkPhysicalDevice;

import static cz.mg.vulkan.Vk.*;


public class TextureImage extends Image {
    public TextureImage(Vk vk, VkPhysicalDevice physicalDevice, VkDevice device, int width, int height, int mipLevelCount) {
        this(vk, physicalDevice, device, width, height, mipLevelCount, VK_FORMAT_R8G8B8A8_UNORM);
    }

    public TextureImage(Vk vk, VkPhysicalDevice physicalDevice, VkDevice device, int width, int height, int mipLevelCount, int format) {
        super(vk, physicalDevice, device, width, height, mipLevelCount, format, VK_IMAGE_ASPECT_COLOR_BIT, VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_SAMPLED_BIT, VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
    }
}
