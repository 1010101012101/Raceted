/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.test;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.system.JmeContext;
import game.entities.CarAppState;

/**
 * 
 * @author Robbo13
 */
public class TestCar extends SimpleApplication {
    
    private CarAppState carAppState;
    private BulletAppState bulletAppState;
    private Spatial terrain;

    public static void main(String[] args) {
        TestCar testCar = new TestCar();
        testCar.start(JmeContext.Type.Display);
    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.setDebugEnabled(true);
        flyCam.setEnabled(false);
        
        initLight();
        initSky();
        initTerrain();
        
        carAppState = new CarAppState(bulletAppState, new Vector3f(0,30,0));
        stateManager.attach(carAppState);
    }
    
    private void initLight() {
        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.White);
        rootNode.addLight(ambientLight);
    }
    
    private void initSky() {
        Spatial sky = assetManager.loadModel("Scenes/Sky.j3o");
        rootNode.attachChild(sky);
    }
    
    private void initTerrain() {
        terrain = assetManager.loadModel("Scenes/Terrain.j3o");
        terrain.setLocalTranslation(0, -5, 0);
        RigidBodyControl landscapeControl = new RigidBodyControl(0.0f);
        terrain.addControl(landscapeControl);
        rootNode.attachChild(terrain);
        bulletAppState.getPhysicsSpace().add(landscapeControl);
    }

    @Override
    public void simpleUpdate(float tpf) {
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        
    }
}
