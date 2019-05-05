package cz.mg.vulkan.oop;

import cz.mg.vulkan.VkMemory;
import cz.mg.vulkan.VkObject;
import cz.mg.vulkan.VkResourceManager;


public abstract class VulkanObject<T, P extends VulkanObject> {
    protected T self = null;
    protected P parent = null;

    public T getVk(){
        return self;
    }

    public P getParent() {
        return parent;
    }

    protected void addToResourceManager(){
        if(self instanceof AutoCloseable){
            VkResourceManager.getInstance().add(this, self);
        }

        if(self instanceof VkObject){
            VkMemory memory = ((VkObject)self).getVkMemory();
            if(memory != null){
                VkResourceManager.getInstance().add(this, memory);
            }
        }

        if(parent != null){
            VkResourceManager.getInstance().add(this, parent);
        }

        VkResourceManager.getInstance().free();
    }
}
