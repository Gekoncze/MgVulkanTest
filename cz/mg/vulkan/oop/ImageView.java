package cz.mg.vulkan.oop;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkImageViewCreateInfo;
import cz.mg.vulkan.oop.resources.VkImageViewResource;


public class ImageView extends VulkanObject {
    private final Image image;
    private final Vk v;
    private final VkImageViewResource vk;
    private final int aspect;

    public ImageView(Image image, int aspect) {
        this.image = image;
        this.v = image.getDevice().getPhysicalDevice().getInstance().getVulkan().getVk();
        this.vk = new VkImageViewResource(v, image.getDevice().getVk());
        this.aspect = aspect;

        VkImageViewCreateInfo viewCreateInfo = new VkImageViewCreateInfo();
        viewCreateInfo.setImage(image.getVk());
        viewCreateInfo.setViewType(image.getType());
        viewCreateInfo.setFormat(image.getFormat());
        viewCreateInfo.getSubresourceRange().setAspectMask(aspect);
        viewCreateInfo.getSubresourceRange().setBaseMipLevel(0);
        viewCreateInfo.getSubresourceRange().setLevelCount(image.getMipLevelCount());
        viewCreateInfo.getSubresourceRange().setBaseArrayLayer(0);
        viewCreateInfo.getSubresourceRange().setLayerCount(image.getArrayLayerCount());
        v.vkCreateImageViewP(image.getDevice().getVk(), viewCreateInfo, null, vk);

        addToResourceManager(vk, image);
    }

    public Image getImage() {
        return image;
    }

    public VkImageViewResource getVk() {
        return vk;
    }

    public int getAspect() {
        return aspect;
    }
}
