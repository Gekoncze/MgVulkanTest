package test.objects;

import cz.mg.vulkan.Vk;
import cz.mg.vulkan.VkMemoryType;
import cz.mg.vulkan.VkPhysicalDevice;
import cz.mg.vulkan.VkPhysicalDeviceMemoryProperties;


public class PhysicalDevice implements AutoCloseable {
    protected final Vk vk;
    protected final VkPhysicalDevice physicalDevice;

    public PhysicalDevice(Vk vk, VkPhysicalDevice physicalDevice) {
        this.vk = vk;
        this.physicalDevice = physicalDevice;
    }

    public VkPhysicalDevice getPhysicalDevice() {
        return physicalDevice;
    }

    public int findMemoryTypeIndex(int requiredTypes, int requiredProperties){
        return findMemoryTypeIndex(vk, physicalDevice, requiredTypes, requiredProperties);
    }

    @Override
    public void close() {
    }

    public static int findMemoryTypeIndex(Vk vk, VkPhysicalDevice physicalDevice, int requiredTypes, int requiredProperties){
        VkPhysicalDeviceMemoryProperties memoryProperties = new VkPhysicalDeviceMemoryProperties();
        vk.vkGetPhysicalDeviceMemoryProperties(physicalDevice, memoryProperties);
        VkMemoryType.Array memoryTypes = new VkMemoryType.Array(memoryProperties.getMemoryTypes(), memoryProperties.getMemoryTypeCount().getValue());
        for(int i = 0; i < memoryTypes.count(); i++){
            if((requiredTypes & (1 << i)) == 0) continue;
            if((memoryTypes.get(i).getPropertyFlagsQ() & requiredProperties) != requiredProperties) continue;
            return i;
        }
        throw new UnsupportedOperationException("Could not find suitable memory type. (0x" + Integer.toHexString(requiredTypes) + ", 0x" + Integer.toHexString(requiredProperties) + ")");
    }
}
