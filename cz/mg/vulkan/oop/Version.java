package cz.mg.vulkan.oop;

import cz.mg.vulkan.VkVersion;


public class Version extends VulkanObject<VkVersion, VulkanObject> {
    public Version(int major, int minor, int patch) {
        this.self = new VkVersion(major, minor, patch);
        addToResourceManager();
    }

    public int getMajorVersion(){
        return self.getMajorVersion();
    }

    public int getMinorVersion(){
        return self.getMinorVersion();
    }

    public int getPatchVersion(){
        return self.getPatchVersion();
    }
}
