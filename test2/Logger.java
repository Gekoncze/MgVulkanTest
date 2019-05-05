package test2;

import cz.mg.collections.array.Array;
import cz.mg.vulkan.oop.Vulkan;


public class Logger {
    private boolean logging = true;

    public void logLibraryCreate(){
        if(!logging) return;
        System.out.println("Library loaded successfully!\n");
    }

    public void logInstanceCreate(){
        if(!logging) return;
        System.out.println("Instance created successfully!\n");
    }

    public void logExtensions(Vulkan vulkan){
        if(!logging) return;
        Array<String> extensions = vulkan.getAvailableExtensions();
        System.out.println("Found " + extensions.count() + " extensions:");
        for(String extension : extensions) System.out.println("    " + extension);
        System.out.println();
    }

    public void logLayers(Vulkan vulkan){
        if(!logging) return;
        Array<String> layers = vulkan.getAvailableLayers();
        System.out.println("Found " + layers.count() + " layers:");
        for(String layer : layers) System.out.println("    " + layer);
        System.out.println();
    }
}
