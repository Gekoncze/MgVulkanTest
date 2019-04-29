package test;

import cz.mg.vulkan.*;
import static cz.mg.vulkan.Vk.*;
import cz.mg.vulkan.utilities.math.Matrix4f;
import cz.mg.vulkan.utilities.math.MatrixGenerator;
import java.awt.image.BufferedImage;


public class Test {
    private static final int IMAGE_WIDTH = 640;
    private static final int IMAGE_HEIGHT = 480;

    public static void main(String[] args) {
        test();
        cleanup();
    }

    public static void cleanup(){
        VkResourceManager m = VkResourceManager.getInstance();
        while(m.count() > 0){
            m.free(false);
            try {Thread.sleep(0);} catch(InterruptedException e){}
        }
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
        System.out.println("Instance created successfully!");
        System.out.println();

        ////////////////////
        /// DEBUG REPORT ///
        ////////////////////
        VkDebugReportCallbackCreateInfoEXT reportCallbackCreateInfo = new VkDebugReportCallbackCreateInfoEXT();
        reportCallbackCreateInfo.setPfnCallback(VkDebug.getDefaultPFNvkDebugReportCallbackEXT());
        reportCallbackCreateInfo.setFlags(VK_DEBUG_REPORT_ERROR_BIT_EXT | VK_DEBUG_REPORT_WARNING_BIT_EXT | VK_DEBUG_REPORT_PERFORMANCE_WARNING_BIT_EXT);

        VkDebugReportCallbackEXT debugReport = new VkDebugReportCallbackEXT();
        vk.vkCreateDebugReportCallbackEXTP(instance, reportCallbackCreateInfo, null, debugReport);
        System.out.println("Debug report created successfully!");
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
            VkMemoryType.Array memoryTypes = new VkMemoryType.Array(memoryProperties.getMemoryTypes(), memoryProperties.getMemoryTypeCountQ());
            System.out.println("        Found " + memoryTypes.count() + " memory types:");
            for(VkMemoryType memoryType : memoryTypes){
                System.out.println("            " + new VkMemoryPropertyFlagBits(memoryType.getPropertyFlags()) + ", " + memoryType.getHeapIndexQ());
            }
            VkMemoryHeap.Array memoryHeaps = new VkMemoryHeap.Array(memoryProperties.getMemoryHeaps(), memoryProperties.getMemoryHeapCountQ());
            System.out.println("        Fund " + memoryHeaps.count() + " memory heaps:");
            for(VkMemoryHeap memoryHeap : memoryHeaps){
                System.out.println("            " + new VkMemoryHeapFlagBits(memoryHeap.getFlags()) + ", " + memoryHeap.getSizeQ());
            }
        }
        System.out.println();
        VkPhysicalDevice selectedPhysicalDevice = physicalDevices.get(0);

        //////////////////////
        /// QUEUE FAMILIES ///
        //////////////////////
        vk.vkGetPhysicalDeviceQueueFamilyProperties(selectedPhysicalDevice, count, null);
        VkQueueFamilyProperties.Array queueFamilyProperties = new VkQueueFamilyProperties.Array(count.getValue());
        vk.vkGetPhysicalDeviceQueueFamilyProperties(selectedPhysicalDevice, count, queueFamilyProperties);

        System.out.println("Found " + queueFamilyProperties.count() + " queue family properties");
        for(VkQueueFamilyProperties properties : queueFamilyProperties){
            System.out.println("    number of queues: " + properties.getQueueCount());
            System.out.println("    flags: " + new VkQueueFlagBits(properties.getQueueFlags()));
        }
        System.out.println();

        //////////////////////
        /// LOGICAL DEVICE ///
        //////////////////////
        VkDeviceQueueCreateInfo queueCreateInfo = new VkDeviceQueueCreateInfo();
        queueCreateInfo.setQueueFamilyIndex(0);
        queueCreateInfo.setQueueCount(1);
        queueCreateInfo.setPQueuePriorities(new VkFloat.Array(1.0f));

        VkDeviceCreateInfo deviceCreateInfo = new VkDeviceCreateInfo();
        deviceCreateInfo.setPEnabledFeatures(new VkPhysicalDeviceFeatures());
        deviceCreateInfo.setQueueCreateInfoCount(1);
        deviceCreateInfo.setPQueueCreateInfos(queueCreateInfo);

        VkDevice device = new VkDevice();
        vk.vkCreateDeviceP(selectedPhysicalDevice, deviceCreateInfo, null, device);
        System.out.println("Logical device created successfully!");
        System.out.println();

        /////////////
        /// QUEUE ///
        /////////////
        VkQueue queue = new VkQueue();
        vk.vkGetDeviceQueue(device, 0, 0, queue);

        /////////////
        /// IMAGE ///
        /////////////
        VkImageCreateInfo imageCreateInfo = new VkImageCreateInfo();
        imageCreateInfo.setImageType(VK_IMAGE_TYPE_2D);
        imageCreateInfo.setFormat(VK_FORMAT_R8G8B8A8_UNORM);
        imageCreateInfo.setExtent(new VkExtent3D(IMAGE_WIDTH, IMAGE_HEIGHT, 1));
        imageCreateInfo.setArrayLayers(1);
        imageCreateInfo.setMipLevels(1);
        imageCreateInfo.setSamples(VK_SAMPLE_COUNT_1_BIT);
        imageCreateInfo.setTiling(VK_IMAGE_TILING_LINEAR);
        imageCreateInfo.setUsage(VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT);
        imageCreateInfo.setSharingMode(VK_SHARING_MODE_EXCLUSIVE);
        imageCreateInfo.setInitialLayout(VK_IMAGE_LAYOUT_UNDEFINED);

        VkImage image = new VkImage();
        vk.vkCreateImageP(device, imageCreateInfo, null, image);
        System.out.println("Image created successfully!");
        System.out.println();

        /////////////////////
        /// DEVICE MEMORY ///
        /////////////////////
        VkMemoryRequirements imageMemoryRequirements = new VkMemoryRequirements();
        vk.vkGetImageMemoryRequirements(device, image, imageMemoryRequirements);

        VkMemoryAllocateInfo imageMemoryAllocateInfo = new VkMemoryAllocateInfo();
        imageMemoryAllocateInfo.setMemoryTypeIndex(0);
        imageMemoryAllocateInfo.setAllocationSize(imageMemoryRequirements.getSize());
        VkDeviceMemory imageMemory = new VkDeviceMemory();
        vk.vkAllocateMemoryP(device, imageMemoryAllocateInfo, null, imageMemory);
        System.out.println("Image memory allocated successfully!");
        System.out.println();

        vk.vkBindImageMemoryP(device, image, imageMemory, 0);
        System.out.println("Image memory bind successfully!");
        System.out.println();

        //////////////////
        /// IMAGE VIEW ///
        //////////////////
        VkImageViewCreateInfo imageViewCreateInfo = new VkImageViewCreateInfo();
        imageViewCreateInfo.setImage(image);
        imageViewCreateInfo.setViewType(VK_IMAGE_VIEW_TYPE_2D);
        imageViewCreateInfo.setFormat(VK_FORMAT_R8G8B8A8_UNORM);
        imageViewCreateInfo.getSubresourceRange().setAspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
        imageViewCreateInfo.getSubresourceRange().setBaseMipLevel(0);
        imageViewCreateInfo.getSubresourceRange().setLevelCount(1);
        imageViewCreateInfo.getSubresourceRange().setBaseArrayLayer(0);
        imageViewCreateInfo.getSubresourceRange().setLayerCount(1);

        VkImageView imageView = new VkImageView();
        vk.vkCreateImageViewP(device, imageViewCreateInfo, null, imageView);
        System.out.println("Image view created successfully!");
        System.out.println();

        ////////////////////
        /// COMMAND POOL ///
        ////////////////////
        VkCommandPoolCreateInfo commandPoolCreateInfo = new VkCommandPoolCreateInfo();
        commandPoolCreateInfo.setQueueFamilyIndex(0);

        VkCommandPool commandPool = new VkCommandPool();
        vk.vkCreateCommandPoolP(device, commandPoolCreateInfo, null, commandPool);
        System.out.println("Command pool created successfully!");
        System.out.println();

        ///////////////
        /// TEXTURE ///
        ///////////////
        BufferedImage textureBufferedImage = Utilities.loadImage(Test.class, "images/spyro.png");
        int textureDataSize = textureBufferedImage.getWidth() * textureBufferedImage.getHeight() * 4;

        VkImageCreateInfo textureCreateInfo = new VkImageCreateInfo();
        textureCreateInfo.setImageType(VK_IMAGE_TYPE_2D);
        textureCreateInfo.setFormat(VK_FORMAT_R8G8B8A8_UNORM);
        textureCreateInfo.setExtent(new VkExtent3D(textureBufferedImage.getWidth(), textureBufferedImage.getHeight(), 1));
        textureCreateInfo.setArrayLayers(1);
        textureCreateInfo.setMipLevels(1);
        textureCreateInfo.setSamples(VK_SAMPLE_COUNT_1_BIT);
        textureCreateInfo.setTiling(VK_IMAGE_TILING_LINEAR);
        textureCreateInfo.setUsage(VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_SAMPLED_BIT);
        textureCreateInfo.setSharingMode(VK_SHARING_MODE_EXCLUSIVE);
        textureCreateInfo.setInitialLayout(VK_IMAGE_LAYOUT_UNDEFINED);

        VkImage texture = new VkImage();
        vk.vkCreateImageP(device, textureCreateInfo, null, texture);
        System.out.println("Texture created successfully!");
        System.out.println();

        VkMemoryRequirements textureMemoryRequirements = new VkMemoryRequirements();
        vk.vkGetImageMemoryRequirements(device, texture, textureMemoryRequirements);

        VkMemoryAllocateInfo textureMemoryAllocateInfo = new VkMemoryAllocateInfo();
        textureMemoryAllocateInfo.setMemoryTypeIndex(0);
        textureMemoryAllocateInfo.setAllocationSize(textureMemoryRequirements.getSize());
        VkDeviceMemory textureMemory = new VkDeviceMemory();
        vk.vkAllocateMemoryP(device, textureMemoryAllocateInfo, null, textureMemory);
        System.out.println("Texture memory allocated successfully!");
        System.out.println();

        vk.vkBindImageMemoryP(device, texture, textureMemory, 0);
        System.out.println("Texture memory bind successfully!");
        System.out.println();

        VkImageViewCreateInfo textureViewCreateInfo = new VkImageViewCreateInfo();
        textureViewCreateInfo.setImage(texture);
        textureViewCreateInfo.setViewType(VK_IMAGE_VIEW_TYPE_2D);
        textureViewCreateInfo.setFormat(VK_FORMAT_R8G8B8A8_UNORM);
        textureViewCreateInfo.getSubresourceRange().setAspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
        textureViewCreateInfo.getSubresourceRange().setBaseMipLevel(0);
        textureViewCreateInfo.getSubresourceRange().setLevelCount(1);
        textureViewCreateInfo.getSubresourceRange().setBaseArrayLayer(0);
        textureViewCreateInfo.getSubresourceRange().setLayerCount(1);

        VkImageView textureView = new VkImageView();
        vk.vkCreateImageViewP(device, textureViewCreateInfo, null, textureView);
        System.out.println("Texture view created successfully!");
        System.out.println();

        /////////////////////////////
        /// TEXTURE DATA TRANSFER ///
        /////////////////////////////
        VkCommandBufferAllocateInfo textureCommandBufferAllocateInfo = new VkCommandBufferAllocateInfo();
        textureCommandBufferAllocateInfo.setCommandPool(commandPool);
        textureCommandBufferAllocateInfo.setLevel(VK_COMMAND_BUFFER_LEVEL_PRIMARY);
        textureCommandBufferAllocateInfo.setCommandBufferCount(1);

        VkCommandBuffer textureCommandBuffer = new VkCommandBuffer();
        vk.vkAllocateCommandBuffersP(device, textureCommandBufferAllocateInfo, textureCommandBuffer);
        System.out.println("Texture command buffer allocated successfully!");
        System.out.println();

        VkCommandBufferBeginInfo textureCommandBufferBeginInfo = new VkCommandBufferBeginInfo();
        textureCommandBufferBeginInfo.setFlags(VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT);

        vk.vkBeginCommandBufferP(textureCommandBuffer, textureCommandBufferBeginInfo);
        System.out.println("Texture command buffer begin!");
        System.out.println();
        {
            VkImageMemoryBarrier textureBarrier = new VkImageMemoryBarrier();
            textureBarrier.setOldLayout(VK_IMAGE_LAYOUT_UNDEFINED);
            textureBarrier.setNewLayout(VK_IMAGE_LAYOUT_GENERAL);
            textureBarrier.setSrcQueueFamilyIndex((int) VK_QUEUE_FAMILY_IGNORED);
            textureBarrier.setDstQueueFamilyIndex((int) VK_QUEUE_FAMILY_IGNORED);
            textureBarrier.setImage(texture);
            textureBarrier.getSubresourceRange().setAspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
            textureBarrier.getSubresourceRange().setBaseMipLevel(0);
            textureBarrier.getSubresourceRange().setLevelCount(1);
            textureBarrier.getSubresourceRange().setBaseArrayLayer(0);
            textureBarrier.getSubresourceRange().setLayerCount(1);

            vk.vkCmdPipelineBarrier(
                    textureCommandBuffer,
                    VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT,
                    VK_PIPELINE_STAGE_TRANSFER_BIT,
                    0,
                    0,
                    null,
                    0,
                    null,
                    1,
                    textureBarrier
            );
        }
        vk.vkEndCommandBufferP(textureCommandBuffer);
        System.out.println("Texture command buffer end!");
        System.out.println();

        VkSubmitInfo textureSubmitInfo = new VkSubmitInfo();
        textureSubmitInfo.setCommandBufferCount(1);
        textureSubmitInfo.setPCommandBuffers(textureCommandBuffer);
        vk.vkQueueSubmitP(queue, 1, textureSubmitInfo, null);
        vk.vkQueueWaitIdleP(queue);
        System.out.println("Texture layout change was successfull!");
        System.out.println();

        VkPointer gpuTextureDataLocation = new VkPointer();
        vk.vkMapMemoryP(device, textureMemory, 0, textureDataSize, 0, gpuTextureDataLocation);
        VkUInt8.Array gpuTextureData = new VkUInt8.Array(gpuTextureDataLocation, textureDataSize);
        Utilities.bufferedImageToData(textureBufferedImage, gpuTextureData);
        vk.vkUnmapMemory(device, textureMemory);
        System.out.println("Texture data transferred successfully!");
        System.out.println();

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
        samplerCreateInfo.setMaxLod(0.0f);

        VkSampler sampler = new VkSampler();
        vk.vkCreateSamplerP(device, samplerCreateInfo, null, sampler);
        System.out.println("Sampler created successfully!");
        System.out.println();

        ////////////////////
        /// VERTEX INPUT ///
        ////////////////////
        VkFloat.Array positionArray = new VkFloat.Array(
                0.0f, -0.5f, 0.0f,
                0.5f, 0.5f, 0.0f,
                -0.5f, 0.5f, 0.0f
        );
        long positionArraySize = positionArray.count() * VkFloat.sizeof();

        VkFloat.Array uvArray = new VkFloat.Array(
                0.5f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f
        );
        long uvArraySize = uvArray.count() * VkFloat.sizeof();

        VkFloat.Array colorArray = new VkFloat.Array(
                1.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f
        );
        long colorArraySize = colorArray.count() * VkFloat.sizeof();

        VkBuffer.Array vertexBuffers = new VkBuffer.Array(3);
        VkBuffer positionBuffer = vertexBuffers.get(0);
        VkBuffer uvBuffer = vertexBuffers.get(1);
        VkBuffer colorBuffer = vertexBuffers.get(2);

        VkBufferCreateInfo positionBufferCreateInfo = new VkBufferCreateInfo();
        positionBufferCreateInfo.setSize(positionArraySize);
        positionBufferCreateInfo.setUsage(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT);
        positionBufferCreateInfo.setSharingMode(VK_SHARING_MODE_EXCLUSIVE);
        vk.vkCreateBufferP(device, positionBufferCreateInfo, null, positionBuffer);

        VkBufferCreateInfo uvBufferCreateInfo = new VkBufferCreateInfo();
        uvBufferCreateInfo.setSize(uvArraySize);
        uvBufferCreateInfo.setUsage(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT);
        uvBufferCreateInfo.setSharingMode(VK_SHARING_MODE_EXCLUSIVE);
        vk.vkCreateBufferP(device, uvBufferCreateInfo, null, uvBuffer);

        VkBufferCreateInfo colorBufferCreateInfo = new VkBufferCreateInfo();
        colorBufferCreateInfo.setSize(colorArraySize);
        colorBufferCreateInfo.setUsage(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT);
        colorBufferCreateInfo.setSharingMode(VK_SHARING_MODE_EXCLUSIVE);
        vk.vkCreateBufferP(device, colorBufferCreateInfo, null, colorBuffer);

        System.out.println("Vertex buffers created successfully!");
        System.out.println();

        VkMemoryRequirements positionMemoryRequirements = new VkMemoryRequirements();
        vk.vkGetBufferMemoryRequirements(device, positionBuffer, positionMemoryRequirements);

        VkMemoryAllocateInfo positionBufferMemoryAllocateInfo = new VkMemoryAllocateInfo();
        positionBufferMemoryAllocateInfo.setAllocationSize(positionMemoryRequirements.getSize());
        positionBufferMemoryAllocateInfo.setMemoryTypeIndex(1);
        VkDeviceMemory positionBufferMemory = new VkDeviceMemory();
        vk.vkAllocateMemoryP(device, positionBufferMemoryAllocateInfo, null, positionBufferMemory);

        VkMemoryRequirements uvMemoryRequirements = new VkMemoryRequirements();
        vk.vkGetBufferMemoryRequirements(device, uvBuffer, uvMemoryRequirements);

        VkMemoryAllocateInfo uvBufferMemoryAllocateInfo = new VkMemoryAllocateInfo();
        uvBufferMemoryAllocateInfo.setAllocationSize(uvMemoryRequirements.getSize());
        uvBufferMemoryAllocateInfo.setMemoryTypeIndex(1);
        VkDeviceMemory uvBufferMemory = new VkDeviceMemory();
        vk.vkAllocateMemoryP(device, uvBufferMemoryAllocateInfo, null, uvBufferMemory);

        VkMemoryRequirements colorMemoryRequirements = new VkMemoryRequirements();
        vk.vkGetBufferMemoryRequirements(device, colorBuffer, colorMemoryRequirements);

        VkMemoryAllocateInfo colorBufferMemoryAllocateInfo = new VkMemoryAllocateInfo();
        colorBufferMemoryAllocateInfo.setAllocationSize(colorMemoryRequirements.getSize());
        colorBufferMemoryAllocateInfo.setMemoryTypeIndex(1);
        VkDeviceMemory colorBufferMemory = new VkDeviceMemory();
        vk.vkAllocateMemoryP(device, colorBufferMemoryAllocateInfo, null, colorBufferMemory);

        System.out.println("Vertex buffer memory allocated successfully!");
        System.out.println();

        vk.vkBindBufferMemoryP(device, positionBuffer, positionBufferMemory, 0);
        vk.vkBindBufferMemoryP(device, uvBuffer, uvBufferMemory, 0);
        vk.vkBindBufferMemoryP(device, colorBuffer, colorBufferMemory, 0);

        System.out.println("Vertex buffer memory bind successfully!");
        System.out.println();

        VkPointer positionLocation = new VkPointer();
        vk.vkMapMemoryP(device, positionBufferMemory, 0, positionArraySize, 0, positionLocation);
        VkFloat.Array gpuPositionArray = new VkFloat.Array(positionLocation, positionArray.count());
        for(int i = 0; i < positionArray.count(); i++) gpuPositionArray.get(i).setValue(positionArray.get(i).getValue());
        vk.vkUnmapMemory(device, positionBufferMemory);

        VkPointer uvLocation = new VkPointer();
        vk.vkMapMemoryP(device, uvBufferMemory, 0, uvArraySize, 0, uvLocation);
        VkFloat.Array gpuUvArray = new VkFloat.Array(uvLocation, uvArray.count());
        for(int i = 0; i < uvArray.count(); i++) gpuUvArray.get(i).setValue(uvArray.get(i).getValue());
        vk.vkUnmapMemory(device, uvBufferMemory);

        VkPointer colorLocation = new VkPointer();
        vk.vkMapMemoryP(device, colorBufferMemory, 0, colorArraySize, 0, colorLocation);
        VkFloat.Array gpuColorArray = new VkFloat.Array(colorLocation, colorArray.count());
        for(int i = 0; i < colorArray.count(); i++) gpuColorArray.get(i).setValue(colorArray.get(i).getValue());
        vk.vkUnmapMemory(device, colorBufferMemory);

        System.out.println("Vertex buffer data filled successfully!");
        System.out.println();

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
        long matrixSize = matrix.count()*VkFloat.sizeof();

        VkBufferCreateInfo matrixBufferCreateInfo = new VkBufferCreateInfo();
        matrixBufferCreateInfo.setUsage(VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT);
        matrixBufferCreateInfo.setSize(matrixSize);
        matrixBufferCreateInfo.setSharingMode(VK_SHARING_MODE_EXCLUSIVE);

        VkBuffer matrixBuffer = new VkBuffer();
        vk.vkCreateBufferP(device, matrixBufferCreateInfo, null, matrixBuffer);
        System.out.println("Uniform buffer created successfully!");
        System.out.println();

        VkMemoryRequirements matrixMemoryRequirements = new VkMemoryRequirements();
        vk.vkGetBufferMemoryRequirements(device, matrixBuffer, matrixMemoryRequirements);

        VkMemoryAllocateInfo matrixMemoryAllocateInfo = new VkMemoryAllocateInfo();
        matrixMemoryAllocateInfo.setMemoryTypeIndex(1);
        matrixMemoryAllocateInfo.setAllocationSize(matrixMemoryRequirements.getSizeQ());

        VkDeviceMemory matrixBufferMemory = new VkDeviceMemory();
        vk.vkAllocateMemoryP(device, matrixMemoryAllocateInfo, null, matrixBufferMemory);
        System.out.println("Uniform buffer memory allocated successfully!");
        System.out.println();

        VkPointer uniformBufferMemoryLocation = new VkPointer();
        vk.vkMapMemoryP(device, matrixBufferMemory, 0, matrixSize, 0, uniformBufferMemoryLocation);
        VkFloat.Array gpuMatrix = new VkFloat.Array(uniformBufferMemoryLocation, matrix.count());
        for(int i = 0; i < matrix.count(); i++) gpuMatrix.setValue(i, matrix.getValue(i));
        vk.vkUnmapMemory(device, matrixBufferMemory);
        System.out.println("Uniform buffer filled successfully!");
        System.out.println();

        vk.vkBindBufferMemoryP(device, matrixBuffer, matrixBufferMemory, 0);
        System.out.println("Uniform buffer memory bind successfully!");
        System.out.println();

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
        System.out.println("Texture descriptor set layout created successfully!");
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
        matrixDescriptorInfo.setBuffer(matrixBuffer);
        matrixDescriptorInfo.setRange(matrixSize);

        matrixDescriptorSetWrite.setDstSet(descriptorSet);
        matrixDescriptorSetWrite.setDstBinding(0);
        matrixDescriptorSetWrite.setDstArrayElement(0);
        matrixDescriptorSetWrite.setDescriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
        matrixDescriptorSetWrite.setDescriptorCount(1);
        matrixDescriptorSetWrite.setPBufferInfo(matrixDescriptorInfo);

        VkDescriptorImageInfo textureDescriptorInfo = new VkDescriptorImageInfo();
        textureDescriptorInfo.setImageLayout(VK_IMAGE_LAYOUT_GENERAL);
        textureDescriptorInfo.setImageView(textureView);
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
        byte[] vertexShaderCode = Utilities.loadBytes(Test.class, "shaders/testVert.spv");
        byte[] fragmentShaderCode = Utilities.loadBytes(Test.class, "shaders/testFrag.spv");
        VkUInt32.Array vertexShaderBuffer = Utilities.createBuffer(vertexShaderCode);
        VkUInt32.Array fragmentShaderBuffer = Utilities.createBuffer(fragmentShaderCode);
        VkShaderModuleCreateInfo vertexShaderCreateInfo = new VkShaderModuleCreateInfo();
        vertexShaderCreateInfo.setCodeSize(vertexShaderCode.length);
        vertexShaderCreateInfo.setPCode(vertexShaderBuffer);
        VkShaderModule vertexShader = new VkShaderModule();
        vk.vkCreateShaderModuleP(device, vertexShaderCreateInfo, null, vertexShader);
        VkShaderModuleCreateInfo fragmentShaderCreateInfo = new VkShaderModuleCreateInfo();
        fragmentShaderCreateInfo.setCodeSize(fragmentShaderCode.length);
        fragmentShaderCreateInfo.setPCode(fragmentShaderBuffer);
        VkShaderModule fragmentShader = new VkShaderModule();
        vk.vkCreateShaderModuleP(device, fragmentShaderCreateInfo, null, fragmentShader);
        System.out.println("Shader modules created successfully!");
        System.out.println();

        //////////////////////////
        /// SHADERS - PIPELINE ///
        //////////////////////////
        VkPipelineShaderStageCreateInfo.Array shaderStages = new VkPipelineShaderStageCreateInfo.Array(2);

        VkPipelineShaderStageCreateInfo vertexShaderStageCreateInfo = shaderStages.get(0);
        vertexShaderStageCreateInfo.setStage(VK_SHADER_STAGE_VERTEX_BIT);
        vertexShaderStageCreateInfo.setModule(vertexShader);
        vertexShaderStageCreateInfo.setPName("main");

        VkPipelineShaderStageCreateInfo fragmentShaderStageCreateInfo = shaderStages.get(1);
        fragmentShaderStageCreateInfo.setStage(VK_SHADER_STAGE_FRAGMENT_BIT);
        fragmentShaderStageCreateInfo.setModule(fragmentShader);
        fragmentShaderStageCreateInfo.setPName("main");

        ////////////////
        /// PIPELINE ///
        ////////////////
        VkPipelineInputAssemblyStateCreateInfo inputAssemblyStateCreateInfo = new VkPipelineInputAssemblyStateCreateInfo();
        inputAssemblyStateCreateInfo.setTopology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST);
        inputAssemblyStateCreateInfo.setPrimitiveRestartEnable(VK_FALSE);

        VkViewport viewport = new VkViewport(0.0f, 0.0f, IMAGE_WIDTH, IMAGE_HEIGHT, 0.0f, 1.0f);
        VkRect2D scissor = new VkRect2D(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

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

        VkPipelineDepthStencilStateCreateInfo pipelineDepthStencilStateCreateInfo = null;

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
        System.out.println("Pipeline layout created successfully!");
        System.out.println();

        VkAttachmentDescription attachmentDescription = new VkAttachmentDescription();
        attachmentDescription.setFormat(VK_FORMAT_R8G8B8A8_UNORM);
        attachmentDescription.setSamples(VK_SAMPLE_COUNT_1_BIT);
        attachmentDescription.setLoadOp(VK_ATTACHMENT_LOAD_OP_CLEAR);
        attachmentDescription.setStoreOp(VK_ATTACHMENT_STORE_OP_STORE);
        attachmentDescription.setStencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE);
        attachmentDescription.setStencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE);
        attachmentDescription.setInitialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
        attachmentDescription.setFinalLayout(VK_IMAGE_LAYOUT_GENERAL);

        VkAttachmentReference attachmentReference = new VkAttachmentReference();
        attachmentReference.setAttachment(0);
        attachmentReference.setLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);

        VkSubpassDescription subpassDescription = new VkSubpassDescription();
        subpassDescription.setPipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS);
        subpassDescription.setColorAttachmentCount(1);
        subpassDescription.setPColorAttachments(attachmentReference);

        VkRenderPassCreateInfo renderPassCreateInfo = new VkRenderPassCreateInfo();
        renderPassCreateInfo.setAttachmentCount(1);
        renderPassCreateInfo.setPAttachments(attachmentDescription);
        renderPassCreateInfo.setSubpassCount(1);
        renderPassCreateInfo.setPSubpasses(subpassDescription);

        VkRenderPass renderPass = new VkRenderPass();
        vk.vkCreateRenderPassP(device, renderPassCreateInfo, null, renderPass);
        System.out.println("Render pass created successfully!");
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
        VkFramebufferCreateInfo framebufferCreateInfo = new VkFramebufferCreateInfo();
        framebufferCreateInfo.setRenderPass(renderPass);
        framebufferCreateInfo.setAttachmentCount(1);
        framebufferCreateInfo.setPAttachments(imageView);
        framebufferCreateInfo.setWidth(IMAGE_WIDTH);
        framebufferCreateInfo.setHeight(IMAGE_HEIGHT);
        framebufferCreateInfo.setLayers(1);

        VkFramebuffer framebuffer = new VkFramebuffer();
        vk.vkCreateFramebufferP(device, framebufferCreateInfo, null, framebuffer);
        System.out.println("Framebuffer created successfully!");
        System.out.println();

        //////////////////////
        /// COMMAND BUFFER ///
        //////////////////////
        VkCommandBufferAllocateInfo commandBufferAllocateInfo = new VkCommandBufferAllocateInfo();
        commandBufferAllocateInfo.setCommandPool(commandPool);
        commandBufferAllocateInfo.setLevel(VK_COMMAND_BUFFER_LEVEL_PRIMARY);
        commandBufferAllocateInfo.setCommandBufferCount(1);

        VkCommandBuffer commandBuffer = new VkCommandBuffer();
        vk.vkAllocateCommandBuffersP(device, commandBufferAllocateInfo, commandBuffer);
        System.out.println("Command buffer allocated successfully!");
        System.out.println();

        VkCommandBufferBeginInfo commandBufferBeginInfo = new VkCommandBufferBeginInfo();
        commandBufferBeginInfo.setFlags(VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT);

        vk.vkBeginCommandBufferP(commandBuffer, commandBufferBeginInfo);
        System.out.println("Command buffer begin!");
        System.out.println();
        {
            VkRenderPassBeginInfo renderPassBeginInfo = new VkRenderPassBeginInfo();
            renderPassBeginInfo.setRenderPass(renderPass);
            renderPassBeginInfo.setFramebuffer(framebuffer);
            renderPassBeginInfo.getRenderArea().getExtent().setWidth(IMAGE_WIDTH);
            renderPassBeginInfo.getRenderArea().getExtent().setHeight(IMAGE_HEIGHT);
            renderPassBeginInfo.setClearValueCount(1);
            renderPassBeginInfo.setPClearValues(new VkClearValue(0.0f, 0.0f, 0.0f, 1.0f));
            vk.vkCmdBeginRenderPass(commandBuffer, renderPassBeginInfo, VK_SUBPASS_CONTENTS_INLINE);
            vk.vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline);
            vk.vkCmdBindVertexBuffers(commandBuffer, 0, vertexBuffers.count(), vertexBuffers, new VkDeviceSize.Array(3));
            vk.vkCmdBindDescriptorSets(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, pipelineLayout, 0, 1, descriptorSet, 0, null);
            vk.vkCmdDraw(commandBuffer, 3, 1, 0, 0);
            vk.vkCmdEndRenderPass(commandBuffer);
        }
        vk.vkEndCommandBufferP(commandBuffer);
        System.out.println("Command buffer end!");
        System.out.println();

        ////////////////////////
        /// ACTUAL RENDERING ///
        ////////////////////////
        VkSubmitInfo submitInfo = new VkSubmitInfo();
        submitInfo.setCommandBufferCount(1);
        submitInfo.setPCommandBuffers(commandBuffer);
        vk.vkQueueSubmitP(queue, 1, submitInfo, null);
        vk.vkQueueWaitIdleP(queue);
        System.out.println("Rendering was successfull!");
        System.out.println();

        //////////////////////////
        /// READING IMAGE DATA ///
        //////////////////////////
        int bpp = 4;
        int size = IMAGE_WIDTH * IMAGE_HEIGHT * bpp;
        VkPointer gpuDataLocation = new VkPointer();
        vk.vkMapMemoryP(device, imageMemory, 0, size, 0, gpuDataLocation);
        VkUInt8.Array gpuResultData = new VkUInt8.Array(gpuDataLocation, size);
        BufferedImage resultBufferedImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
        Utilities.dataToBufferedImage(gpuResultData, resultBufferedImage);
        vk.vkUnmapMemory(device, imageMemory);

        ////////////////////////
        /// DISPLAYING IMAGE ///
        ////////////////////////
        new ImageWindow(resultBufferedImage).setVisible(true);

        ///////////////
        /// CLEANUP ///
        ///////////////
        vk.vkDeviceWaitIdleP(device);

        vk.vkDestroyFramebuffer(device, framebuffer, null);
        vk.vkDestroyPipeline(device, pipeline, null);
        vk.vkDestroyRenderPass(device, renderPass, null);
        vk.vkDestroyPipelineLayout(device, pipelineLayout, null);
        vk.vkDestroyShaderModule(device, fragmentShader, null);
        vk.vkDestroyShaderModule(device, vertexShader, null);
        vk.vkDestroyDescriptorPool(device, descriptorPool, null);
        vk.vkDestroyDescriptorSetLayout(device, descriptorSetLayout, null);
        vk.vkFreeMemory(device, matrixBufferMemory, null);
        vk.vkDestroyBuffer(device, matrixBuffer, null);
        vk.vkFreeMemory(device, colorBufferMemory, null);
        vk.vkFreeMemory(device, uvBufferMemory, null);
        vk.vkFreeMemory(device, positionBufferMemory, null);
        vk.vkDestroyBuffer(device, colorBuffer, null);
        vk.vkDestroyBuffer(device, uvBuffer, null);
        vk.vkDestroyBuffer(device, positionBuffer, null);
        vk.vkDestroySampler(device, sampler, null);
        vk.vkDestroyImageView(device, textureView, null);
        vk.vkFreeMemory(device, textureMemory, null);
        vk.vkDestroyImage(device, texture, null);
        vk.vkDestroyCommandPool(device, commandPool, null);
        vk.vkDestroyImageView(device, imageView, null);
        vk.vkFreeMemory(device, imageMemory, null);
        vk.vkDestroyImage(device, image, null);
        vk.vkDestroyDevice(device, null);
        vk.vkDestroyDebugReportCallbackEXT(instance, debugReport, null);
        vk.vkDestroyInstance(instance, null);
        System.out.println("Cleanup successfull!");
    }
}
