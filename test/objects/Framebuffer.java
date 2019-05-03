package test.objects;

import cz.mg.vulkan.*;


public class Framebuffer implements AutoCloseable {
    protected final Vk vk;
    protected final VkDevice device;
    protected final VkFramebuffer framebuffer;
    protected final int width;
    protected final int height;

    public Framebuffer(Vk vk, VkDevice device, FramebufferAttachments framebufferAttachments, VkRenderPass renderPass) {
        this.vk = vk;
        this.device = device;
        this.framebuffer = new VkFramebuffer();
        this.width = framebufferAttachments.getWidth();
        this.height = framebufferAttachments.getHeight();

        VkImageView.Array framebufferImageViews;
        if(framebufferAttachments.getColorAttachment() != null && framebufferAttachments.getDepthAttachment() != null){
            framebufferImageViews = new VkImageView.Array(
                    framebufferAttachments.getColorAttachment().getView(),
                    framebufferAttachments.getDepthAttachment().getView()
            );
        } else if(framebufferAttachments.getColorAttachment() != null){
            framebufferImageViews = new VkImageView.Array(
                    framebufferAttachments.getColorAttachment().getView()
            );
        } else if(framebufferAttachments.getDepthAttachment() != null){
            framebufferImageViews = new VkImageView.Array(
                    framebufferAttachments.getDepthAttachment().getView()
            );
        } else {
            throw new IllegalArgumentException();
        }

        VkFramebufferCreateInfo framebufferCreateInfo = new VkFramebufferCreateInfo();
        framebufferCreateInfo.setRenderPass(renderPass);
        framebufferCreateInfo.setAttachmentCount(framebufferImageViews.count());
        framebufferCreateInfo.setPAttachments(framebufferImageViews);
        framebufferCreateInfo.setWidth(framebufferAttachments.getWidth());
        framebufferCreateInfo.setHeight(framebufferAttachments.getHeight());
        framebufferCreateInfo.setLayers(1);

        vk.vkCreateFramebufferP(device, framebufferCreateInfo, null, framebuffer);
    }

    public VkFramebuffer getFramebuffer() {
        return framebuffer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void close() {
        vk.vkDestroyFramebuffer(device, framebuffer, null);
    }
}
