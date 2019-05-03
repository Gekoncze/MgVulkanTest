package test.objects;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkDevice;
import static cz.mg.vulkan.Vk.*;


public class TextureImage extends Image {
    public TextureImage(Vk vk, VkDevice device, int width, int height, int mipLevelCount) {
        this(vk, device, width, height, mipLevelCount, VK_FORMAT_R8G8B8A8_UNORM);
    }

    public TextureImage(Vk vk, VkDevice device, int width, int height, int mipLevelCount, int format) {
        super(vk, device, width, height, mipLevelCount, format, VK_IMAGE_ASPECT_COLOR_BIT, VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_SAMPLED_BIT);
    }
}
