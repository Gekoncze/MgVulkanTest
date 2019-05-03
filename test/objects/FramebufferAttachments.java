package test.objects;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkDevice;


public class FramebufferAttachments implements AutoCloseable {
    protected final Vk vk;
    protected final VkDevice device;
    protected final int width;
    protected final int height;
    protected final ColorAttachmentImage colorAttachment;
    protected final DepthAttachmentImage depthAttachment;

    public FramebufferAttachments(Vk vk, VkDevice device, int width, int height, boolean color, boolean depth) {
        this.vk = vk;
        this.device = device;
        this.width = width;
        this.height = height;
        this.colorAttachment = color ? new ColorAttachmentImage(vk, device, width, height) : null;
        this.depthAttachment = depth ? new DepthAttachmentImage(vk, device, width, height) : null;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ColorAttachmentImage getColorAttachment() {
        return colorAttachment;
    }

    public DepthAttachmentImage getDepthAttachment() {
        return depthAttachment;
    }

    @Override
    public void close() {
        if(colorAttachment != null) colorAttachment.close();
        if(depthAttachment != null) depthAttachment.close();
    }
}
