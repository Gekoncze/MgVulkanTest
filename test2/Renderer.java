package test2;

import cz.mg.vulkan.VkVersion;
import cz.mg.vulkan.oop.*;
import cz.mg.vulkan.oop.extended.Texture2D;
import test2.images.ImagesLocation;
import test2.utilities.IOUtilities;
import test2.utilities.ImageUtilities;
import java.awt.image.BufferedImage;
import static cz.mg.vulkan.Vk.*;
import static test2.MainWindow.APPLICATION_NAME;


public class Renderer {
    public static final String ENGINE_NAME = "MgVulkanTestEngine";

    private final Logger logger = new Logger();
    private final Vulkan vulkan;
    private final Instance instance;
    private final DebugReportCallback debugReportCallback;
    private final PhysicalDevice physicalDevice;
    private final Device device;
    private final Queue queue;
    private final CommandPool commandPool;
    private final Texture2D texture;

    public Renderer() {
        this.vulkan = new Vulkan();
        logger.logLibraryCreate();
        logger.logExtensions(vulkan);
        logger.logLayers(vulkan);

        String[] enabledExtensions = new String[]{
                "VK_EXT_debug_report"
        };

        String[] enabledLayers = new String[]{
                "VK_LAYER_LUNARG_standard_validation"
        };

        this.instance = new Instance(
                vulkan,
                APPLICATION_NAME, new VkVersion(0, 1, 0),
                ENGINE_NAME, new VkVersion(0, 1, 0),
                enabledExtensions,
                enabledLayers,
                new VkVersion(1, 0, 0)
        );
        logger.logInstanceCreate();

        debugReportCallback = new DebugReportCallback(instance, VK_DEBUG_REPORT_ERROR_BIT_EXT | VK_DEBUG_REPORT_WARNING_BIT_EXT | VK_DEBUG_REPORT_PERFORMANCE_WARNING_BIT_EXT);
        physicalDevice = instance.getPhysicalDevices().get(0);
        int queueFamilyIndex = physicalDevice.findQueueFamilyIndex(VK_QUEUE_GRAPHICS_BIT);
        device = new Device(physicalDevice, queueFamilyIndex, 1, 1.0f);
        queue = device.getQueue(queueFamilyIndex, 0);
        commandPool = new CommandPool(device, queueFamilyIndex, 0);

        BufferedImage textureBufferedImage = IOUtilities.loadImage(ImagesLocation.class, "spyro.png");
        texture = ImageUtilities.bufferedImageToTexture(textureBufferedImage, device, commandPool, queue);


    }

    public Object getResult(){
        return null; // TODO
    }

    public void render(int width, int height){
        // TODO
    }
}
