package test.objects;

import cz.mg.vulkan.*;


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

    public static int findQueueFamilyIndex(Vk vk, VkPhysicalDevice physicalDevice, int requiredFlags){
        VkUInt32 count = new VkUInt32();

        vk.vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, count, null);
        VkQueueFamilyProperties.Array queueFamilyProperties = new VkQueueFamilyProperties.Array(count.getValue());
        vk.vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, count, queueFamilyProperties);

        for(int i = 0; i < queueFamilyProperties.count(); i++){
            VkQueueFamilyProperties properties = queueFamilyProperties.get(i);
            if((properties.getQueueFlagsQ() & requiredFlags) != requiredFlags) continue;
            return i;
        }
        throw new UnsupportedOperationException("Could not find suitable queue family. (" + Integer.toHexString(requiredFlags) + ")");
    }
}
