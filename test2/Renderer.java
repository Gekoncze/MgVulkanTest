package test2;

import cz.mg.vulkan.oop.Instance;
import cz.mg.vulkan.oop.Version;
import cz.mg.vulkan.oop.Vulkan;
import static cz.mg.vulkan.Vk.*;
import static test2.MainWindow.APPLICATION_NAME;


public class Renderer {
    public static final String ENGINE_NAME = "MgVulkanTestEngine";

    private final Logger logger = new Logger();
    private final Vulkan vulkan;
    private final Instance instance;

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
                APPLICATION_NAME, new Version(0, 1, 0),
                ENGINE_NAME, new Version(0, 1, 0),
                enabledExtensions,
                enabledLayers,
                new Version(1, 0, 0)
        );
        logger.logInstanceCreate();

        // TODO
    }

    public Object getResult(){
        return null; // TODO
    }

    public void render(int width, int height){
        // TODO
    }
}
