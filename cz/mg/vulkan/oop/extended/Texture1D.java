package cz.mg.vulkan.oop.extended;

import cz.mg.vulkan.oop.Device;
import static cz.mg.vulkan.Vk.*;


public class Texture1D extends Texture {
    public Texture1D(Device device, int width, int mipLevelCount, int arrayLayerCount) {
        super(device, VK_IMAGE_TYPE_1D, width, 1, 1, mipLevelCount, arrayLayerCount);
    }
}
