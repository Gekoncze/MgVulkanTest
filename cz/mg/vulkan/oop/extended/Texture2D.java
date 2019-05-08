package cz.mg.vulkan.oop.extended;

import cz.mg.vulkan.oop.Device;
import static cz.mg.vulkan.Vk.*;


public class Texture2D extends Texture {
    public Texture2D(Device device, int width, int height, int mipLevelCount, int arrayLayerCount) {
        super(device, VK_IMAGE_TYPE_2D, width, height, 1, mipLevelCount, arrayLayerCount);
    }
}
