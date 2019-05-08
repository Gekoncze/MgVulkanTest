package cz.mg.vulkan.oop.extended;

import cz.mg.vulkan.oop.*;
import static cz.mg.vulkan.Vk.*;


public class Texture {
    private final Image image;
    private final ImageView imageView;
    private final DeviceMemory deviceMemory;

    public Texture(Device device, int type, int width, int height, int depth, int mipLevelCount, int arrayLayerCount) {
        this.image = new Image(device, type, VK_FORMAT_R8G8B8A8_UNORM, width, height, depth, mipLevelCount, arrayLayerCount, VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_SAMPLED_BIT);
        this.deviceMemory = new DeviceMemory(device, image.getMemoryRequirements(), VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
        image.bindMemory(deviceMemory);
        this.imageView = new ImageView(image, VK_IMAGE_ASPECT_COLOR_BIT);
    }

    public Image getImage() {
        return image;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public DeviceMemory getDeviceMemory() {
        return deviceMemory;
    }

    public void setLayout(CommandPool commandPool, Queue queue, int oldLayout, int newLayout){
        ImageTransition transition = new ImageTransition(image, commandPool, oldLayout, newLayout, imageView.getAspect());
        transition.relayout(queue);
    }
}
