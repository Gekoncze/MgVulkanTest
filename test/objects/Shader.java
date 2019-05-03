package test.objects;

import cz.mg.vulkan.*;


public class Shader implements AutoCloseable {
    protected final Vk vk;
    protected final VkDevice device;
    protected final VkShaderModule shader;

    public Shader(Vk vk, VkDevice device, VkUInt32.Array code) {
        this.vk = vk;
        this.device = device;
        this.shader = new VkShaderModule();

        VkShaderModuleCreateInfo vertexShaderCreateInfo = new VkShaderModuleCreateInfo();
        vertexShaderCreateInfo.setCodeSize(code.count() * VkUInt32.sizeof());
        vertexShaderCreateInfo.setPCode(code);

        vk.vkCreateShaderModuleP(device, vertexShaderCreateInfo, null, shader);
    }

    public VkShaderModule getShader() {
        return shader;
    }

    @Override
    public void close() {
        vk.vkDestroyShaderModule(device, shader, null);
    }
}
