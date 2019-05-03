package test.objects;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkDevice;
import static cz.mg.vulkan.Vk.*;


public class ColorAttachmentImage extends Image {
    public ColorAttachmentImage(Vk vk, VkDevice device, int width, int height) {
        this(vk, device, width, height, VK_FORMAT_R8G8B8A8_UNORM);
    }

    public ColorAttachmentImage(Vk vk, VkDevice device, int width, int height, int format) {
        super(vk, device, width, height, 1, format, VK_IMAGE_ASPECT_COLOR_BIT, VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT);
    }
}
