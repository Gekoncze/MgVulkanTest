package test;

import cz.mg.vulkan.*;
import static cz.mg.vulkan.Vk.*;
import cz.mg.vulkan.utilities.math.Matrix4f;
import cz.mg.vulkan.utilities.math.MatrixGenerator;
import test.objects.*;
import java.awt.image.BufferedImage;


public class Test {
    private static final int FRAMEBUFFER_WIDTH = 640;
    private static final int FRAMEBUFFER_HEIGHT = 480;

    public static void main(String[] args) {
        test();
        cleanup();
    }

    public static void cleanup(){
        VkResourceManager.getInstance().waitFreeAll();
    }

    public static void test() {
        ///////////////
        /// LIBRARY ///
        ///////////////
        Vk vk = new Vk();
        VkUInt32 count = new VkUInt32();

        ////////////////
        /// INSTANCE ///
        ////////////////
        vk.vkEnumerateInstanceExtensionPropertiesP(null, count, null);
        VkExtensionProperties.Array extensions = new VkExtensionProperties.Array(count.getValue());
        vk.vkEnumerateInstanceExtensionPropertiesP(null, count, extensions);

        System.out.println("Found " + extensions.count() + " extensions: ");
        for(VkExtensionProperties extension : extensions){
            System.out.println("    " + extension.getExtensionNameQ() + " " + extension.getSpecVersion());
        }
        System.out.println();

        vk.vkEnumerateInstanceLayerPropertiesP(count, null);
        VkLayerProperties.Array layers = new VkLayerProperties.Array(count.getValue());
        vk.vkEnumerateInstanceLayerPropertiesP(count, layers);

        System.out.println("Found " + layers.count() + " layers: ");
        for(VkLayerProperties layer : layers){
            System.out.println("    " + layer.getLayerNameQ() + " " + new VkVersion(layer.getSpecVersion()));
        }
        System.out.println();

        VkString.Array enabledExtensions = new VkString.Array(
                "VK_EXT_debug_report"
        );

        VkString.Array enabledLayers = new VkString.Array(
                "VK_LAYER_LUNARG_standard_validation"
        );

        VkApplicationInfo applicationInfo = new VkApplicationInfo();
        applicationInfo.setPApplicationName("MgVulkanTest");
        applicationInfo.setPEngineName("MgVulkanTestEngine");
        applicationInfo.setApiVersion(Vk.VK_API_VERSION_1_0);

        VkInstanceCreateInfo instanceCreateInfo = new VkInstanceCreateInfo();
        instanceCreateInfo.setPApplicationInfo(applicationInfo);
        instanceCreateInfo.setEnabledLayerCount(enabledLayers.count());
        instanceCreateInfo.setPpEnabledLayerNames(enabledLayers);
        instanceCreateInfo.setEnabledExtensionCount(enabledExtensions.count());
        instanceCreateInfo.setPpEnabledExtensionNames(enabledExtensions);

        VkInstance instance = new VkInstance();
        vk.vkCreateInstanceP(instanceCreateInfo, null, instance);
        vk.setInstance(instance);
        System.out.println("Instance created successfully! (" + instance + ")");
        System.out.println();

        ////////////////////
        /// DEBUG REPORT ///
        ////////////////////
        VkDebugReportCallbackCreateInfoEXT reportCallbackCreateInfo = new VkDebugReportCallbackCreateInfoEXT();
        reportCallbackCreateInfo.setPfnCallback(VkDebug.getDefaultPFNvkDebugReportCallbackEXT());
        reportCallbackCreateInfo.setFlags(VK_DEBUG_REPORT_ERROR_BIT_EXT | VK_DEBUG_REPORT_WARNING_BIT_EXT | VK_DEBUG_REPORT_PERFORMANCE_WARNING_BIT_EXT);

        VkDebugReportCallbackEXT debugReport = new VkDebugReportCallbackEXT();
        vk.vkCreateDebugReportCallbackEXTP(instance, reportCallbackCreateInfo, null, debugReport);
        System.out.println("Debug report created successfully! (" + debugReport + ")");
        System.out.println();

        ///////////////////////
        /// PHYSICAL DEVICE ///
        ///////////////////////
        vk.vkEnumeratePhysicalDevicesP(instance, count, null);
        VkPhysicalDevice.Array physicalDevices = new VkPhysicalDevice.Array(count.getValue());
        vk.vkEnumeratePhysicalDevicesP(instance, count, physicalDevices);

        System.out.println("Found " + physicalDevices.count() + " physical devices: ");
        for(VkPhysicalDevice physicalDevice : physicalDevices){
            VkPhysicalDeviceProperties properties = new VkPhysicalDeviceProperties();
            vk.vkGetPhysicalDeviceProperties(physicalDevice, properties);
            System.out.println("    " + properties.getDeviceNameQ());
            System.out.println("        Device type: " + properties.getDeviceType());
            System.out.println("        API version: " + new VkVersion(properties.getApiVersion()));
            System.out.println("        Driver version: " + new VkVersion(properties.getDriverVersion()));
            System.out.println("        Vendor: " + new VkVendor(properties.getVendorID()));
            VkPhysicalDeviceMemoryProperties memoryProperties = new VkPhysicalDeviceMemoryProperties();
            vk.vkGetPhysicalDeviceMemoryProperties(physicalDevice, memoryProperties);
            VkMemoryType.Array memoryTypes = memoryProperties.getMemoryTypesQ();
            System.out.println("        Found " + memoryTypes.count() + " memory types:");
            for(VkMemoryType memoryType : memoryTypes){
                System.out.println("            " + new VkMemoryPropertyFlagBits(memoryType.getPropertyFlags()) + ", " + memoryType.getHeapIndexQ());
            }
            VkMemoryHeap.Array memoryHeaps = memoryProperties.getMemoryHeapsQ();
            System.out.println("        Fund " + memoryHeaps.count() + " memory heaps:");
            for(VkMemoryHeap memoryHeap : memoryHeaps){
                System.out.println("            " + new VkMemoryHeapFlagBits(memoryHeap.getFlags()) + ", " + memoryHeap.getSizeQ());
            }
        }
        System.out.println();

        int selectedPhysicalDeviceIndex = 0;
        VkPhysicalDevice physicalDevice = physicalDevices.get(selectedPhysicalDeviceIndex);
        int selectedQueueFamilyIndex = PhysicalDevice.findQueueFamilyIndex(vk, physicalDevice, VK_QUEUE_GRAPHICS_BIT | VK_QUEUE_TRANSFER_BIT);

        //////////////////////
        /// LOGICAL DEVICE ///
        //////////////////////
        VkDeviceQueueCreateInfo queueCreateInfo = new VkDeviceQueueCreateInfo();
        queueCreateInfo.setQueueFamilyIndex(selectedQueueFamilyIndex);
        queueCreateInfo.setQueueCount(1);
        queueCreateInfo.setPQueuePriorities(new VkFloat.Array(1.0f));

        VkDeviceCreateInfo deviceCreateInfo = new VkDeviceCreateInfo();
        deviceCreateInfo.setPEnabledFeatures(new VkPhysicalDeviceFeatures());
        deviceCreateInfo.setQueueCreateInfoCount(1);
        deviceCreateInfo.setPQueueCreateInfos(queueCreateInfo);

        VkDevice device = new VkDevice();
        vk.vkCreateDeviceP(physicalDevice, deviceCreateInfo, null, device);
        System.out.println("Logical device created successfully! (" + device + ")");
        System.out.println();

        /////////////
        /// QUEUE ///
        /////////////
        VkQueue queue = new VkQueue();
        vk.vkGetDeviceQueue(device, 0, 0, queue);

        ////////////////////
        /// COMMAND POOL ///
        ////////////////////
        VkCommandPoolCreateInfo commandPoolCreateInfo = new VkCommandPoolCreateInfo();
        commandPoolCreateInfo.setQueueFamilyIndex(0);

        VkCommandPool commandPool = new VkCommandPool();
        vk.vkCreateCommandPoolP(device, commandPoolCreateInfo, null, commandPool);
        System.out.println("Command pool created successfully! (" + commandPool + ")");
        System.out.println();

        ///////////////
        /// TEXTURE ///
        ///////////////
        BufferedImage[] textureMipmapBufferedImages = Utilities.generateMipmapImages(Utilities.loadImage(Test.class, "images/spyro.png"));

        TextureImage texture = new TextureImage(
                vk, physicalDevice, device,
                textureMipmapBufferedImages[0].getWidth(),
                textureMipmapBufferedImages[0].getHeight(),
                textureMipmapBufferedImages.length
        );

        /////////////////////////////
        /// TEXTURE DATA TRANSFER ///
        /////////////////////////////
        texture.setLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, commandPool, queue);

        for(int i = 0; i < textureMipmapBufferedImages.length; i++){
            BufferedImage textureMipmapBufferedImage = textureMipmapBufferedImages[i];
            int textureDataSize = textureMipmapBufferedImage.getWidth() * textureMipmapBufferedImage.getHeight() * 4;

            try (StagingBuffer textureStagingBuffer = new StagingBuffer(vk, physicalDevice, device, textureDataSize)) {
                VkPointer gpuTextureDataLocation = textureStagingBuffer.mapMemory();
                VkUInt8.Array gpuTextureData = new VkUInt8.Array(gpuTextureDataLocation, textureDataSize);
                Utilities.bufferedImageToData(textureMipmapBufferedImage, gpuTextureData);
                textureStagingBuffer.unmapMemory();

                texture.setData(textureStagingBuffer, i, commandPool, queue);
            }
        }

        texture.setLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL, commandPool, queue);

        ///////////////
        /// SAMPLER ///
        ///////////////
        VkSamplerCreateInfo samplerCreateInfo = new VkSamplerCreateInfo();
        samplerCreateInfo.setMinFilter(VK_FILTER_LINEAR);
        samplerCreateInfo.setMagFilter(VK_FILTER_NEAREST);
        samplerCreateInfo.setAddressModeU(VK_SAMPLER_ADDRESS_MODE_REPEAT);
        samplerCreateInfo.setAddressModeV(VK_SAMPLER_ADDRESS_MODE_REPEAT);
        samplerCreateInfo.setAddressModeW(VK_SAMPLER_ADDRESS_MODE_REPEAT);
        samplerCreateInfo.setAnisotropyEnable(VK_FALSE);
        samplerCreateInfo.setBorderColor(VK_BORDER_COLOR_INT_OPAQUE_BLACK);
        samplerCreateInfo.setUnnormalizedCoordinates(VK_FALSE);
        samplerCreateInfo.setCompareEnable(VK_FALSE);
        samplerCreateInfo.setCompareOp(VK_COMPARE_OP_ALWAYS);
        samplerCreateInfo.setMipmapMode(VK_SAMPLER_MIPMAP_MODE_LINEAR);
        samplerCreateInfo.setMipLodBias(0.0f);
        samplerCreateInfo.setMinLod(0.0f);
        samplerCreateInfo.setMaxLod(texture.getMipLevelCount());

        VkSampler sampler = new VkSampler();
        vk.vkCreateSamplerP(device, samplerCreateInfo, null, sampler);
        System.out.println("Sampler created successfully! (" + sampler + ")");
        System.out.println();

        ////////////////////
        /// VERTEX INPUT ///
        ////////////////////
        int vertexCount = 12;

        VkFloat.Array positionArray = new VkFloat.Array(
                0.0f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, 0.5f,

                0.0f, 0.5f, 0.4f,
                -0.5f, -0.5f, 0.4f,
                0.5f, -0.5f, 0.4f,

                -1.0f, -1.0f, 0.0f,
                -0.9f, -0.9f, 0.0f,
                -1.0f, -0.9f, 0.0f,

                -1.0f, -1.0f, 0.0f,
                -0.9f, -1.0f, 0.0f,
                -0.9f, -0.9f, 0.0f
        );
        int positionArraySize = (int) (positionArray.count() * VkFloat.sizeof());

        VkFloat.Array uvArray = new VkFloat.Array(
                0.5f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,

                0.5f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,

                0.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,

                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        );
        int uvArraySize = (int) (uvArray.count() * VkFloat.sizeof());

        VkFloat.Array colorArray = new VkFloat.Array(
                1.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f,

                1.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f,

                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,

                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f
        );
        int colorArraySize = (int) (colorArray.count() * VkFloat.sizeof());

        VertexBuffer positionBuffer = new VertexBuffer(vk, physicalDevices, device, positionArraySize);
        VertexBuffer uvBuffer = new VertexBuffer(vk, physicalDevices, device, uvArraySize);
        VertexBuffer colorBuffer = new VertexBuffer(vk, physicalDevices, device, colorArraySize);

        try (StagingBuffer positionStagingBuffer = new StagingBuffer(vk, physicalDevice, device, positionArraySize)) {
            VkPointer positionLocation = positionStagingBuffer.mapMemory();
            VkFloat.Array gpuPositionArray = new VkFloat.Array(positionLocation, positionArray.count());
            for(int i = 0; i < positionArray.count(); i++) gpuPositionArray.get(i).setValue(positionArray.get(i).getValue());
            positionStagingBuffer.unmapMemory();

            positionBuffer.setData(positionStagingBuffer, commandPool, queue);
        }

        try (StagingBuffer uvStagingBuffer = new StagingBuffer(vk, physicalDevice, device, uvArraySize)) {
            VkPointer uvLocation = uvStagingBuffer.mapMemory();
            VkFloat.Array gpuUvArray = new VkFloat.Array(uvLocation, uvArray.count());
            for(int i = 0; i < uvArray.count(); i++) gpuUvArray.get(i).setValue(uvArray.get(i).getValue());
            uvStagingBuffer.unmapMemory();

            uvBuffer.setData(uvStagingBuffer, commandPool, queue);
        }

        try (StagingBuffer colorStagingBuffer = new StagingBuffer(vk, physicalDevice, device, colorArraySize)) {
            VkPointer colorLocation = colorStagingBuffer.mapMemory();
            VkFloat.Array gpuColorArray = new VkFloat.Array(colorLocation, colorArray.count());
            for(int i = 0; i < colorArray.count(); i++) gpuColorArray.get(i).setValue(colorArray.get(i).getValue());
            colorStagingBuffer.unmapMemory();

            colorBuffer.setData(colorStagingBuffer, commandPool, queue);
        }

        ///////////////////////////////
        /// VERTEX INPUT - PIPELINE ///
        ///////////////////////////////
        VkVertexInputBindingDescription.Array bindingDescriptions = new VkVertexInputBindingDescription.Array(3);

        VkVertexInputBindingDescription positionBindingDescription = bindingDescriptions.get(0);
        positionBindingDescription.setBinding(0);
        positionBindingDescription.setStride((int) (3*VkFloat.sizeof()));
        positionBindingDescription.setInputRate(VK_VERTEX_INPUT_RATE_VERTEX);

        VkVertexInputBindingDescription uvBindingDescription = bindingDescriptions.get(1);
        uvBindingDescription.setBinding(1);
        uvBindingDescription.setStride((int) (2*VkFloat.sizeof()));
        uvBindingDescription.setInputRate(VK_VERTEX_INPUT_RATE_VERTEX);

        VkVertexInputBindingDescription colorBindingDescription = bindingDescriptions.get(2);
        colorBindingDescription.setBinding(2);
        colorBindingDescription.setStride((int) (4*VkFloat.sizeof()));
        colorBindingDescription.setInputRate(VK_VERTEX_INPUT_RATE_VERTEX);

        VkVertexInputAttributeDescription.Array attributeDescriptions = new VkVertexInputAttributeDescription.Array(3);

        VkVertexInputAttributeDescription positionAttributeDescription = attributeDescriptions.get(0);
        positionAttributeDescription.setLocation(0);
        positionAttributeDescription.setBinding(0);
        positionAttributeDescription.setFormat(VK_FORMAT_R32G32B32_SFLOAT);
        positionAttributeDescription.setOffset(0);

        VkVertexInputAttributeDescription uvAttributeDescription = attributeDescriptions.get(1);
        uvAttributeDescription.setLocation(1);
        uvAttributeDescription.setBinding(1);
        uvAttributeDescription.setFormat(VK_FORMAT_R32G32_SFLOAT);
        uvAttributeDescription.setOffset(0);

        VkVertexInputAttributeDescription colorAttributeDescription = attributeDescriptions.get(2);
        colorAttributeDescription.setLocation(2);
        colorAttributeDescription.setBinding(2);
        colorAttributeDescription.setFormat(VK_FORMAT_R32G32B32A32_SFLOAT);
        colorAttributeDescription.setOffset(0);

        VkPipelineVertexInputStateCreateInfo vertexInputStateCreateInfo = new VkPipelineVertexInputStateCreateInfo();
        vertexInputStateCreateInfo.setVertexBindingDescriptionCount(bindingDescriptions.count());
        vertexInputStateCreateInfo.setPVertexBindingDescriptions(bindingDescriptions);
        vertexInputStateCreateInfo.setVertexAttributeDescriptionCount(attributeDescriptions.count());
        vertexInputStateCreateInfo.setPVertexAttributeDescriptions(attributeDescriptions);

        //////////////
        /// MATRIX ///
        //////////////
        MatrixGenerator generator = new MatrixGenerator(MatrixGenerator.Angle.DEGREES);
        Matrix4f matrix = generator.rotateZ(-90.0f);
        int matrixSize = (int) (matrix.count()*VkFloat.sizeof());

        UniformBuffer matrixBuffer = new UniformBuffer(vk, physicalDevice, device, matrixSize);

        try (StagingBuffer matrixStagingBuffer = new StagingBuffer(vk, physicalDevice, device, matrixSize)) {
            VkPointer uniformBufferMemoryLocation = matrixStagingBuffer.mapMemory();
            VkFloat.Array gpuMatrix = new VkFloat.Array(uniformBufferMemoryLocation, matrix.count());
            for(int i = 0; i < matrix.count(); i++) gpuMatrix.setValue(i, matrix.getValue(i));
            matrixStagingBuffer.unmapMemory();

            matrixBuffer.setData(matrixStagingBuffer, commandPool, queue);
        }

        /////////////////////////////
        /// DESCRIPTOR SET LAYOUT ///
        /////////////////////////////
        VkDescriptorSetLayoutBinding.Array layoutBindings = new VkDescriptorSetLayoutBinding.Array(2);
        VkDescriptorSetLayoutBinding matrixLayoutBinding = layoutBindings.get(0);
        VkDescriptorSetLayoutBinding textureLayoutBinding = layoutBindings.get(1);

        matrixLayoutBinding.setBinding(0);
        matrixLayoutBinding.setDescriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
        matrixLayoutBinding.setDescriptorCount(1);
        matrixLayoutBinding.setStageFlags(VK_SHADER_STAGE_VERTEX_BIT);

        textureLayoutBinding.setBinding(1);
        textureLayoutBinding.setDescriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
        textureLayoutBinding.setDescriptorCount(1);
        textureLayoutBinding.setStageFlags(VK_SHADER_STAGE_FRAGMENT_BIT);

        VkDescriptorSetLayoutCreateInfo layoutCreateInfo = new VkDescriptorSetLayoutCreateInfo();
        layoutCreateInfo.setBindingCount(layoutBindings.count());
        layoutCreateInfo.setPBindings(layoutBindings);

        VkDescriptorSetLayout descriptorSetLayout = new VkDescriptorSetLayout();
        vk.vkCreateDescriptorSetLayoutP(device, layoutCreateInfo, null, descriptorSetLayout);
        System.out.println("Descriptor set layout created successfully!");
        System.out.println();

        ///////////////////////////////
        /// ALLOCATE DESCRIPTOR SET ///
        ///////////////////////////////
        VkDescriptorPoolSize.Array descriptorPoolSizes = new VkDescriptorPoolSize.Array(2);
        VkDescriptorPoolSize matrixDescriptorPoolSize = descriptorPoolSizes.get(0);
        VkDescriptorPoolSize textureDescriptorPoolSize = descriptorPoolSizes.get(1);

        matrixDescriptorPoolSize.setType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
        matrixDescriptorPoolSize.setDescriptorCount(1);

        textureDescriptorPoolSize.setType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
        textureDescriptorPoolSize.setDescriptorCount(1);

        VkDescriptorPoolCreateInfo descriptorPoolCreateInfo = new VkDescriptorPoolCreateInfo();
        descriptorPoolCreateInfo.setPoolSizeCount(descriptorPoolSizes.count());
        descriptorPoolCreateInfo.setPPoolSizes(descriptorPoolSizes);
        descriptorPoolCreateInfo.setMaxSets(1);

        VkDescriptorPool descriptorPool = new VkDescriptorPool();
        vk.vkCreateDescriptorPoolP(device, descriptorPoolCreateInfo, null, descriptorPool);
        System.out.println("Descriptor pool created successfully!");
        System.out.println();

        VkDescriptorSetAllocateInfo descriptorSetAllocateInfo = new VkDescriptorSetAllocateInfo();
        descriptorSetAllocateInfo.setDescriptorPool(descriptorPool);
        descriptorSetAllocateInfo.setDescriptorSetCount(1);
        descriptorSetAllocateInfo.setPSetLayouts(descriptorSetLayout);

        VkDescriptorSet descriptorSet = new VkDescriptorSet();
        vk.vkAllocateDescriptorSetsP(device, descriptorSetAllocateInfo, descriptorSet);
        System.out.println("Matrix descriptor sets allocated successfully!");
        System.out.println();

        /////////////////////////////
        /// UPDATE DESCRIPTOR SET ///
        /////////////////////////////
        VkWriteDescriptorSet.Array descriptorSetWrites = new VkWriteDescriptorSet.Array(2);
        VkWriteDescriptorSet matrixDescriptorSetWrite = descriptorSetWrites.get(0);
        VkWriteDescriptorSet textureDescriptorSetWrite = descriptorSetWrites.get(1);

        VkDescriptorBufferInfo matrixDescriptorInfo = new VkDescriptorBufferInfo();
        matrixDescriptorInfo.setBuffer(matrixBuffer.getBuffer());
        matrixDescriptorInfo.setRange(matrixSize);

        matrixDescriptorSetWrite.setDstSet(descriptorSet);
        matrixDescriptorSetWrite.setDstBinding(0);
        matrixDescriptorSetWrite.setDstArrayElement(0);
        matrixDescriptorSetWrite.setDescriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
        matrixDescriptorSetWrite.setDescriptorCount(1);
        matrixDescriptorSetWrite.setPBufferInfo(matrixDescriptorInfo);

        VkDescriptorImageInfo textureDescriptorInfo = new VkDescriptorImageInfo();
        textureDescriptorInfo.setImageLayout(VK_IMAGE_LAYOUT_GENERAL);
        textureDescriptorInfo.setImageView(texture.getView());
        textureDescriptorInfo.setSampler(sampler);

        textureDescriptorSetWrite.setDstSet(descriptorSet);
        textureDescriptorSetWrite.setDstBinding(1);
        textureDescriptorSetWrite.setDstArrayElement(0);
        textureDescriptorSetWrite.setDescriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
        textureDescriptorSetWrite.setDescriptorCount(1);
        textureDescriptorSetWrite.setPImageInfo(textureDescriptorInfo);

        vk.vkUpdateDescriptorSets(device, descriptorSetWrites.count(), descriptorSetWrites, 0, null);
        System.out.println("Descriptor sets updated successfully!");
        System.out.println();

        ///////////////
        /// SHADERS ///
        ///////////////
        VkUInt32.Array vertexShaderCode = Utilities.createBuffer(Utilities.loadBytes(Test.class, "shaders/testVert.spv"));
        VkUInt32.Array fragmentShaderCode = Utilities.createBuffer(Utilities.loadBytes(Test.class, "shaders/testFrag.spv"));
        Shader vertexShader = new Shader(vk, device, vertexShaderCode);
        Shader fragmentShader = new Shader(vk, device, fragmentShaderCode);

        //////////////////////////
        /// SHADERS - PIPELINE ///
        //////////////////////////
        VkPipelineShaderStageCreateInfo.Array shaderStages = new VkPipelineShaderStageCreateInfo.Array(2);

        VkPipelineShaderStageCreateInfo vertexShaderStageCreateInfo = shaderStages.get(0);
        vertexShaderStageCreateInfo.setStage(VK_SHADER_STAGE_VERTEX_BIT);
        vertexShaderStageCreateInfo.setModule(vertexShader.getShader());
        vertexShaderStageCreateInfo.setPName("main");

        VkPipelineShaderStageCreateInfo fragmentShaderStageCreateInfo = shaderStages.get(1);
        fragmentShaderStageCreateInfo.setStage(VK_SHADER_STAGE_FRAGMENT_BIT);
        fragmentShaderStageCreateInfo.setModule(fragmentShader.getShader());
        fragmentShaderStageCreateInfo.setPName("main");

        ////////////////
        /// PIPELINE ///
        ////////////////
        VkPipelineInputAssemblyStateCreateInfo inputAssemblyStateCreateInfo = new VkPipelineInputAssemblyStateCreateInfo();
        inputAssemblyStateCreateInfo.setTopology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST);
        inputAssemblyStateCreateInfo.setPrimitiveRestartEnable(VK_FALSE);

        VkViewport viewport = new VkViewport(0.0f, 0.0f, FRAMEBUFFER_WIDTH, FRAMEBUFFER_HEIGHT, 0.0f, 1.0f);
        VkRect2D scissor = new VkRect2D(0, 0, FRAMEBUFFER_WIDTH, FRAMEBUFFER_HEIGHT);

        VkPipelineViewportStateCreateInfo viewportStateCreateInfo = new VkPipelineViewportStateCreateInfo();
        viewportStateCreateInfo.setViewportCount(1);
        viewportStateCreateInfo.setPViewports(viewport);
        viewportStateCreateInfo.setScissorCount(1);
        viewportStateCreateInfo.setPScissors(scissor);

        VkPipelineRasterizationStateCreateInfo rasterizationStateCreateInfo = new VkPipelineRasterizationStateCreateInfo();
        rasterizationStateCreateInfo.setDepthBiasEnable(VK_FALSE);
        rasterizationStateCreateInfo.setDepthClampEnable(VK_FALSE);
        rasterizationStateCreateInfo.setRasterizerDiscardEnable(VK_FALSE);
        rasterizationStateCreateInfo.setPolygonMode(VK_POLYGON_MODE_FILL);
        rasterizationStateCreateInfo.setCullMode(VK_CULL_MODE_BACK_BIT);
        rasterizationStateCreateInfo.setFrontFace(VK_FRONT_FACE_CLOCKWISE);
        rasterizationStateCreateInfo.setLineWidth(1.0f);

        VkPipelineMultisampleStateCreateInfo multisampleStateCreateInfo = new VkPipelineMultisampleStateCreateInfo();
        multisampleStateCreateInfo.setSampleShadingEnable(VK_FALSE);
        multisampleStateCreateInfo.setRasterizationSamples(VK_SAMPLE_COUNT_1_BIT);
        multisampleStateCreateInfo.setMinSampleShading(1.0f);

        VkPipelineDepthStencilStateCreateInfo pipelineDepthStencilStateCreateInfo = new VkPipelineDepthStencilStateCreateInfo();
        pipelineDepthStencilStateCreateInfo.setDepthTestEnable(VK_TRUE);
        pipelineDepthStencilStateCreateInfo.setDepthWriteEnable(VK_TRUE);
        pipelineDepthStencilStateCreateInfo.setDepthCompareOp(VK_COMPARE_OP_LESS);
        pipelineDepthStencilStateCreateInfo.setDepthBoundsTestEnable(VK_FALSE);

        VkPipelineColorBlendAttachmentState pipelineColorBlendAttachmentState = new VkPipelineColorBlendAttachmentState();
        pipelineColorBlendAttachmentState.setBlendEnable(VK_FALSE);
        pipelineColorBlendAttachmentState.setSrcColorBlendFactor(VK_BLEND_FACTOR_ONE);
        pipelineColorBlendAttachmentState.setDstColorBlendFactor(VK_BLEND_FACTOR_ZERO);
        pipelineColorBlendAttachmentState.setColorBlendOp(VK_BLEND_OP_ADD);
        pipelineColorBlendAttachmentState.setSrcAlphaBlendFactor(VK_BLEND_FACTOR_ONE);
        pipelineColorBlendAttachmentState.setDstAlphaBlendFactor(VK_BLEND_FACTOR_ZERO);
        pipelineColorBlendAttachmentState.setAlphaBlendOp(VK_BLEND_OP_ADD);
        pipelineColorBlendAttachmentState.setColorWriteMask(VK_COLOR_COMPONENT_R_BIT | VK_COLOR_COMPONENT_G_BIT | VK_COLOR_COMPONENT_B_BIT | VK_COLOR_COMPONENT_A_BIT);

        VkPipelineColorBlendStateCreateInfo colorBlendStateCreateInfo = new VkPipelineColorBlendStateCreateInfo();
        colorBlendStateCreateInfo.setLogicOpEnable(VK_FALSE);
        colorBlendStateCreateInfo.setLogicOp(VK_LOGIC_OP_COPY);
        colorBlendStateCreateInfo.setAttachmentCount(1);
        colorBlendStateCreateInfo.setPAttachments(pipelineColorBlendAttachmentState);
        colorBlendStateCreateInfo.setBlendConstants(new VkFloat.Array(0.0f, 0.0f, 0.0f, 0.0f));

        VkDynamicState.Array dynamicStates = new VkDynamicState.Array(
                VK_DYNAMIC_STATE_VIEWPORT,
                VK_DYNAMIC_STATE_LINE_WIDTH
        );

        VkPipelineDynamicStateCreateInfo pipelineDynamicStateCreateInfo = new VkPipelineDynamicStateCreateInfo();
        pipelineDynamicStateCreateInfo.setDynamicStateCount(dynamicStates.count());
        pipelineDynamicStateCreateInfo.setPDynamicStates(dynamicStates);

        VkPipelineLayoutCreateInfo pipelineLayoutCreateInfo = new VkPipelineLayoutCreateInfo();
        pipelineLayoutCreateInfo.setSetLayoutCount(1);
        pipelineLayoutCreateInfo.setPSetLayouts(descriptorSetLayout);

        VkPipelineLayout pipelineLayout = new VkPipelineLayout();
        vk.vkCreatePipelineLayoutP(device, pipelineLayoutCreateInfo, null, pipelineLayout);
        System.out.println("Pipeline layout created successfully! (" + pipelineLayout + ")");
        System.out.println();

        ///////////////////////////////
        /// FRAMEBUFFER ATTACHMENTS ///
        ///////////////////////////////
        FramebufferAttachments framebufferAttachments = new FramebufferAttachments(vk, physicalDevice, device, FRAMEBUFFER_WIDTH, FRAMEBUFFER_HEIGHT, true, true);
        framebufferAttachments.getColorAttachment().setLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL, commandPool, queue);
        framebufferAttachments.getDepthAttachment().setLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL, commandPool, queue);

        ////////////////////////////////////
        /// PIPELINE - COLOR ATTACHMENTS ///
        ////////////////////////////////////
        VkAttachmentDescription.Array attachmentDescriptions = new VkAttachmentDescription.Array(2);
        VkAttachmentDescription colorAttachmentDescription = attachmentDescriptions.get(0);
        VkAttachmentDescription depthAttachmentDescription = attachmentDescriptions.get(1);

        colorAttachmentDescription.setFormat(framebufferAttachments.getColorAttachment().getFormat());
        colorAttachmentDescription.setSamples(VK_SAMPLE_COUNT_1_BIT);
        colorAttachmentDescription.setLoadOp(VK_ATTACHMENT_LOAD_OP_CLEAR);
        colorAttachmentDescription.setStoreOp(VK_ATTACHMENT_STORE_OP_STORE);
        colorAttachmentDescription.setStencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE);
        colorAttachmentDescription.setStencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE);
        colorAttachmentDescription.setInitialLayout(framebufferAttachments.getColorAttachment().getLayout());
        colorAttachmentDescription.setFinalLayout(framebufferAttachments.getColorAttachment().getLayout());

        depthAttachmentDescription.setFormat(framebufferAttachments.getDepthAttachment().getFormat());
        depthAttachmentDescription.setSamples(VK_SAMPLE_COUNT_1_BIT);
        depthAttachmentDescription.setLoadOp(VK_ATTACHMENT_LOAD_OP_CLEAR);
        depthAttachmentDescription.setStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE);
        depthAttachmentDescription.setStencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE);
        depthAttachmentDescription.setStencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE);
        depthAttachmentDescription.setInitialLayout(framebufferAttachments.getDepthAttachment().getLayout());
        depthAttachmentDescription.setFinalLayout(framebufferAttachments.getDepthAttachment().getLayout());

        VkAttachmentReference colorAttachmentReference = new VkAttachmentReference();
        colorAttachmentReference.setAttachment(0);
        colorAttachmentReference.setLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);

        VkAttachmentReference depthAttachmentReference = new VkAttachmentReference();
        depthAttachmentReference.setAttachment(1);
        depthAttachmentReference.setLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL);

        VkSubpassDescription subpassDescription = new VkSubpassDescription();
        subpassDescription.setPipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS);
        subpassDescription.setColorAttachmentCount(1);
        subpassDescription.setPColorAttachments(colorAttachmentReference);
        subpassDescription.setPDepthStencilAttachment(depthAttachmentReference);

        VkRenderPassCreateInfo renderPassCreateInfo = new VkRenderPassCreateInfo();
        renderPassCreateInfo.setAttachmentCount(attachmentDescriptions.count());
        renderPassCreateInfo.setPAttachments(attachmentDescriptions);
        renderPassCreateInfo.setSubpassCount(1);
        renderPassCreateInfo.setPSubpasses(subpassDescription);

        VkRenderPass renderPass = new VkRenderPass();
        vk.vkCreateRenderPassP(device, renderPassCreateInfo, null, renderPass);
        System.out.println("Render pass created successfully! (" + renderPass + ")");
        System.out.println();

        VkGraphicsPipelineCreateInfo graphicsPipelineCreateInfo = new VkGraphicsPipelineCreateInfo();
        graphicsPipelineCreateInfo.setStageCount(shaderStages.count());
        graphicsPipelineCreateInfo.setPStages(shaderStages);
        graphicsPipelineCreateInfo.setPVertexInputState(vertexInputStateCreateInfo);
        graphicsPipelineCreateInfo.setPInputAssemblyState(inputAssemblyStateCreateInfo);
        graphicsPipelineCreateInfo.setPViewportState(viewportStateCreateInfo);
        graphicsPipelineCreateInfo.setPRasterizationState(rasterizationStateCreateInfo);
        graphicsPipelineCreateInfo.setPMultisampleState(multisampleStateCreateInfo);
        graphicsPipelineCreateInfo.setPColorBlendState(colorBlendStateCreateInfo);
        graphicsPipelineCreateInfo.setPDepthStencilState(pipelineDepthStencilStateCreateInfo);
        graphicsPipelineCreateInfo.setLayout(pipelineLayout);
        graphicsPipelineCreateInfo.setRenderPass(renderPass);
        graphicsPipelineCreateInfo.setSubpass(0);

        VkPipeline pipeline = new VkPipeline();
        vk.vkCreateGraphicsPipelinesP(device, null, 1, graphicsPipelineCreateInfo, null, pipeline);
        System.out.println("Graphics pipeline created successfully!");
        System.out.println();

        ///////////////////
        /// FRAMEBUFFER ///
        ///////////////////
        Framebuffer framebuffer = new Framebuffer(vk, device, framebufferAttachments, renderPass);

        /////////////////
        /// RENDERING ///
        /////////////////
        PrimaryCommandBuffer commandBuffer = new PrimaryCommandBuffer(vk, device, commandPool);
        VkCommandBuffer cmd = commandBuffer.getCommandBuffer();
        commandBuffer.begin();

        VkClearValue.Array clearValues = new VkClearValue.Array(2);
        clearValues.get(0).set(0.0f, 0.0f, 0.0f, 1.0f);
        clearValues.get(1).set(1.0f, 0);

        VkBuffer.Array vertexBuffers = new VkBuffer.Array(
                positionBuffer.getBuffer(),
                uvBuffer.getBuffer(),
                colorBuffer.getBuffer()
        );

        VkRenderPassBeginInfo renderPassBeginInfo = new VkRenderPassBeginInfo();
        renderPassBeginInfo.setRenderPass(renderPass);
        renderPassBeginInfo.setFramebuffer(framebuffer.getFramebuffer());
        renderPassBeginInfo.getRenderArea().getExtent().setWidth(framebuffer.getWidth());
        renderPassBeginInfo.getRenderArea().getExtent().setHeight(framebuffer.getHeight());
        renderPassBeginInfo.setClearValueCount(clearValues.count());
        renderPassBeginInfo.setPClearValues(clearValues);

        vk.vkCmdBeginRenderPass(cmd, renderPassBeginInfo, VK_SUBPASS_CONTENTS_INLINE);
        vk.vkCmdBindPipeline(cmd, VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline);
        vk.vkCmdBindVertexBuffers(cmd, 0, vertexBuffers.count(), vertexBuffers, new VkDeviceSize.Array(3));
        vk.vkCmdBindDescriptorSets(cmd, VK_PIPELINE_BIND_POINT_GRAPHICS, pipelineLayout, 0, 1, descriptorSet, 0, null);
        vk.vkCmdDraw(cmd, vertexCount, 1, 0, 0);
        vk.vkCmdEndRenderPass(cmd);

        commandBuffer.end();
        commandBuffer.submit(queue);

        //////////////////////////
        /// READING IMAGE DATA ///
        //////////////////////////
        framebufferAttachments.getColorAttachment().setLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, commandPool, queue);

        BufferedImage resultBufferedImage = new BufferedImage(FRAMEBUFFER_WIDTH, FRAMEBUFFER_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
        int colorAttachmentDataSize = FRAMEBUFFER_WIDTH * FRAMEBUFFER_HEIGHT * 4;
        try (StagingBuffer resultStagingBuffer = new StagingBuffer(vk, physicalDevice, device, colorAttachmentDataSize)) {
            framebufferAttachments.getColorAttachment().getData(resultStagingBuffer, 0, commandPool, queue);

            VkPointer gpuDataLocation = resultStagingBuffer.mapMemory();
            VkUInt8.Array gpuResultData = new VkUInt8.Array(gpuDataLocation, colorAttachmentDataSize);
            Utilities.dataToBufferedImage(gpuResultData, resultBufferedImage);
            resultStagingBuffer.unmapMemory();
        }

        ////////////////////////
        /// DISPLAYING IMAGE ///
        ////////////////////////
        new ImageWindow(resultBufferedImage).setVisible(true);

        ///////////////
        /// CLEANUP ///
        ///////////////
        vk.vkDeviceWaitIdleP(device);

        commandBuffer.close();
        framebuffer.close();
        vk.vkDestroyPipeline(device, pipeline, null);
        vk.vkDestroyRenderPass(device, renderPass, null);
        vk.vkDestroyPipelineLayout(device, pipelineLayout, null);
        fragmentShader.close();
        vertexShader.close();
        vk.vkDestroyDescriptorPool(device, descriptorPool, null);
        vk.vkDestroyDescriptorSetLayout(device, descriptorSetLayout, null);
        matrixBuffer.close();
        positionBuffer.close();
        uvBuffer.close();
        colorBuffer.close();
        vk.vkDestroySampler(device, sampler, null);
        texture.close();
        vk.vkDestroyCommandPool(device, commandPool, null);
        framebufferAttachments.close();
        vk.vkDestroyDevice(device, null);
        vk.vkDestroyDebugReportCallbackEXT(instance, debugReport, null);
        vk.vkDestroyInstance(instance, null);
        System.out.println("Cleanup successfull!");
    }
}
