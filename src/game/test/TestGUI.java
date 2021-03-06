/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.test;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import game.gui.GUIAppState;
import game.utils.ImageUtils;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

/**
 *
 * @author Andreas Fruhwirt
 */
public class TestGUI extends SimpleApplication {

    private GUIAppState startScreen;
    private AudioNode audioSource;

    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setSettingsDialogImage(ImageUtils.RACETED_TEXT);
        settings.setResolution(1920, 1080);
        settings.setSamples(16);
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        settings.setFullscreen(device.isFullScreenSupported());
        settings.setTitle("Raceted");

        TestGUI app = new TestGUI();
        app.setShowSettings(true);
        app.setSettings(settings);
        app.setDisplayFps(true);
        app.setDisplayStatView(true);
        app.start();
    }

    @Override
    public void simpleInitApp() {

        initAppStates();
    
        flyCam.setDragToRotate(true);
    }

    private void initAppStates() {
        startScreen = new GUIAppState();
        stateManager.attach(startScreen);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}

