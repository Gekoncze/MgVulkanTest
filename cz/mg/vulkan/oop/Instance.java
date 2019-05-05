package cz.mg.vulkan.oop;

import cz.mg.vulkan.*;


public class Instance extends VulkanObject<VkInstance, Vulkan> {
    private final Vk vk;

    public Instance(Vulkan parent, String applicationName, Version applicationVersion, String engineName, Version engineVersion, String[] enabledExtensions, String[] enabledLayers, Version apiVersion) {
        this.parent = parent;
        this.vk = parent.getVk();
        this.self = new VkInstanceHandle(vk);

        VkApplicationInfo applicationInfo = new VkApplicationInfo();
        applicationInfo.setPApplicationName(applicationName);
        applicationInfo.setApplicationVersion(applicationVersion.getVk());
        applicationInfo.setPEngineName(engineName);
        applicationInfo.setEngineVersion(engineVersion.getVk());
        applicationInfo.setApiVersion(apiVersion.getVk());

        VkInstanceCreateInfo instanceCreateInfo = new VkInstanceCreateInfo();
        instanceCreateInfo.setPApplicationInfo(applicationInfo);
        instanceCreateInfo.setEnabledLayerCount(enabledLayers.length);
        instanceCreateInfo.setPpEnabledLayerNames(new VkString.Array(enabledLayers));
        instanceCreateInfo.setEnabledExtensionCount(enabledExtensions.length);
        instanceCreateInfo.setPpEnabledExtensionNames(new VkString.Array(enabledExtensions));

        VkInstance instance = new VkInstance();
        vk.vkCreateInstanceP(instanceCreateInfo, null, instance);
        vk.setInstance(instance);

        addToResourceManager();
    }

    public Vulkan getParent() {
        return parent;
    }

    private static class VkInstanceHandle extends VkInstance implements Handle {
        private final Vk vk;

        public VkInstanceHandle(Vk vk) {
            this.vk = vk;
        }

        @Override
        public void close() {
            vk.vkDestroyInstance(this, null);
        }
    }
}
