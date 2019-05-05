package test2;

import cz.mg.vulkan.VkResourceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;


public class MainWindow extends javax.swing.JFrame {
    public static final String APPLICATION_NAME = "MgVulkanTest";

    private final Renderer renderer = new Renderer();
    private BufferedImage image;

    public MainWindow() {
        initComponents();
        setTitle(APPLICATION_NAME);
        jPanelGraphics.setPreferredSize(new Dimension(640, 480));
        pack();
        setLocationRelativeTo(null);
        repaint();

        jPanelGraphics.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                super.componentResized(componentEvent);
                image = null;
            }
        });
    }

    private BufferedImage getImage(){
        if(image == null){
            image = new BufferedImage(jPanelGraphics.getWidth(), jPanelGraphics.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            renderer.render(jPanelGraphics.getWidth(), jPanelGraphics.getHeight());
            // TODO
        }
        return image;
    }

    private void draw(Graphics g){
        g.drawImage(getImage(), 0, 0, rootPane);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jPanelGraphics = new JPanel(){
            @Override
            public void paint(Graphics g) {
                draw(g);
            }
        };

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new GridLayout());

        javax.swing.GroupLayout jPanelGraphicsLayout = new javax.swing.GroupLayout(jPanelGraphics);
        jPanelGraphics.setLayout(jPanelGraphicsLayout);
        jPanelGraphicsLayout.setHorizontalGroup(
                jPanelGraphicsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE)
        );
        jPanelGraphicsLayout.setVerticalGroup(
                jPanelGraphicsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE)
        );

        getContentPane().add(jPanelGraphics);

        pack();
    }// </editor-fold>

    public static void main(String[] args) {
        mainLoop();
        cleanup();
    }

    private static void mainLoop(){
        new MainWindow().setVisible(true);
    }

    private static void cleanup(){
        VkResourceManager.getInstance().waitFreeAll();
    }

    // Variables declaration - do not modify
    private JPanel jPanelGraphics;
    // End of variables declaration
}
