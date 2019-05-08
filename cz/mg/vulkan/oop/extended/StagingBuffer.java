package cz.mg.vulkan.oop.extended;

import static cz.mg.vulkan.Vk.*;
import cz.mg.vulkan.oop.Buffer;
import cz.mg.vulkan.oop.Device;
import cz.mg.vulkan.oop.DeviceMemory;


public class StagingBuffer extends Buffer {
    private final DeviceMemory deviceMemory;

    public StagingBuffer(Device device, long size) {
        super(device, size, VK_BUFFER_USAGE_TRANSFER_SRC_BIT);
        this.deviceMemory = new DeviceMemory(device, getMemoryRequirements(), VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
        bindMemory(deviceMemory);
    }

    public DeviceMemory getDeviceMemory() {
        return deviceMemory;
    }
}
