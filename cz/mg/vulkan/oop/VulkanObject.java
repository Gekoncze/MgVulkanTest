package cz.mg.vulkan.oop;

import cz.mg.vulkan.VkMemory;
import cz.mg.vulkan.VkObject;
import cz.mg.vulkan.VkResourceManager;


public abstract class VulkanObject {
    protected void addToResourceManager(Object vk, Object parent){
        // note: some of those might seem redundant, but they actually ensure the correct order of close calls

        if(vk instanceof AutoCloseable){
            VkResourceManager.getInstance().add(this, vk);
        }

        if(vk instanceof VkObject){
            VkMemory memory = ((VkObject)vk).getVkMemory();
            if(memory != null){
                VkResourceManager.getInstance().add(this, memory);
            }
        }

        if(parent != null){
            VkResourceManager.getInstance().add(this, parent);
        }
    }
}
