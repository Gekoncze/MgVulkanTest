package test.objects;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkDevice;
import static cz.mg.vulkan.Vk.*;


public class DepthAttachmentImage extends Image {
    public DepthAttachmentImage(Vk vk, VkDevice device, int width, int height) {
        this(vk, device,  width, height, VK_FORMAT_D32_SFLOAT);
    }

    public DepthAttachmentImage(Vk vk, VkDevice device, int width, int height, int format) {
        this(vk, device,  width, height, format, VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT);
    }

    public DepthAttachmentImage(Vk vk, VkDevice device, int width, int height, int format, int usage) {
        super(vk, device, width, height, 1, format, VK_IMAGE_ASPECT_DEPTH_BIT, VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT | usage);
    }
}
