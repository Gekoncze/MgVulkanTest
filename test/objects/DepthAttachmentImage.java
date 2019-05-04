package test.objects;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkDevice;
import cz.mg.vulkan.VkPhysicalDevice;

import static cz.mg.vulkan.Vk.*;


public class DepthAttachmentImage extends Image {
    public DepthAttachmentImage(Vk vk, VkPhysicalDevice physicalDevice, VkDevice device, int width, int height) {
        this(vk, physicalDevice, device,  width, height, VK_FORMAT_D32_SFLOAT);
    }

    public DepthAttachmentImage(Vk vk, VkPhysicalDevice physicalDevice, VkDevice device, int width, int height, int format) {
        this(vk, physicalDevice, device,  width, height, format, VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT);
    }

    public DepthAttachmentImage(Vk vk, VkPhysicalDevice physicalDevice, VkDevice device, int width, int height, int format, int usage) {
        super(vk, physicalDevice, device, width, height, 1, format, VK_IMAGE_ASPECT_DEPTH_BIT, VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT | usage, VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
    }
}
