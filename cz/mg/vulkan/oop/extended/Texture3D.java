package cz.mg.vulkan.oop.extended;

import cz.mg.vulkan.oop.Device;
import static cz.mg.vulkan.Vk.*;


public class Texture3D extends Texture {
    public Texture3D(Device device, int width, int height, int depth, int mipLevelCount, int arrayLayerCount) {
        super(device, VK_IMAGE_TYPE_3D, width, height, depth, mipLevelCount, arrayLayerCount);
    }
}
