/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.entities;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.ChaseCamera;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import game.test.DMGArt;

/**
 *
 * @author Robbo13
 */
public class CarAppState extends CharacterAppState {
    
    private static final float MAX_SPEED = 300f;
    private static final float SPIKE_DMG_GAIN = 2f;
    
    private int steer;
    
    private VehicleControl carControl;
    private Node vehicleNode;
    private float steeringValue;
    private float accelerationValue;

    public CarAppState(BulletAppState bulletAppState, int maxHP, Vector3f spawnPoint, Quaternion spawnRotation, Node terrainNode) {
        super(bulletAppState, maxHP, spawnPoint, spawnRotation, terrainNode);
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
    }
    
    @Override
    public void initCamera() {
        super.initCamera();
//        camNode = new CameraNode("CameraNode", cam);
//        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
//        vehicleNode.attachChild(camNode);
//        camNode.setLocalTranslation(0, 5, -15);

        //Work in Progresser als af
        chaseCam = new ChaseCamera(cam, geometry, inputManager);
        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setSmoothMotion(true);
        chaseCam.setTrailingEnabled(true);
        chaseCam.setMaxVerticalRotation(FastMath.PI / 16);
        chaseCam.setDefaultVerticalRotation(FastMath.PI / 16);
        chaseCam.setLookAtOffset(new Vector3f(0, 2, 0));
        chaseCam.setDefaultDistance(7);
        chaseCam.setTrailingSensitivity(10);
        chaseCam.setZoomSensitivity(1000);
        chaseCam.setEnabled(true);
    }
    
    @Override
    protected void initPlayer() {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", ColorRGBA.Red);
        
//        CompoundCollisionShape compoundShape = new CompoundCollisionShape();
//        BoxCollisionShape collBox = new BoxCollisionShape(new Vector3f(1.2f, 0.5f, 2.4f));
//        compoundShape.addChildShape(collBox, new Vector3f(0, 1, 0));
//        
//        Box box = new Box(1.2f, 0.5f, 2.4f);
//        Geometry boxGeo = new Geometry("base", box);
//        boxGeo.setLocalTranslation(new Vector3f(0, 1, 0));
//        boxGeo.setMaterial(mat);
//        
//        vehicleNode = new Node("vehicleNode");
//        vehicleNode.attachChild(boxGeo);
        
        vehicleNode = new Node("vehicleNode");
        
        geometry = (Geometry) assetManager.loadModel("Models/Car.obj");
        vehicleNode.attachChild(geometry);
        vehicleNode.setShadowMode(RenderQueue.ShadowMode.Cast);
        
        BoundingBox box = (BoundingBox) geometry.getModelBound();

        //Create a hull collision shape for the chassis
        CollisionShape vehicleHull = CollisionShapeFactory.createDynamicMeshShape(geometry);
        

        //Create a vehicle control
        carControl = new VehicleControl(vehicleHull, 500);
        
        
        //Hier sollte man noch'n paar neue Zahlen draufschreiben, hmmm...
        carControl.setSuspensionCompression(0.1f  * 2.0f * FastMath.sqrt(200.0f));
        carControl.setSuspensionDamping(0.2f  * 2.0f * FastMath.sqrt(200.0f));
        carControl.setSuspensionStiffness(200.0f);
        carControl.setMaxSuspensionForce(5000);
        
        vehicleNode.addControl(carControl);
        
        //Create four wheels and add them at their locations
        float radius = 0.35f;
        float restLength = 0.3f;
        float yOff = 0.5f;
        float xOff = 1f;
        float zOff = 1.7f;
        
        Vector3f wheelDirection = new Vector3f(0, -1, 0);
        Vector3f wheelAxle = new Vector3f(-1, 0, 0);

        Cylinder wheelMesh = new Cylinder(16, 16, radius, radius * 0.6f, true);

        Node node1 = new Node("wheel 1 node");
        Geometry wheels1 = new Geometry("wheel 1", wheelMesh);
        node1.attachChild(wheels1);
        wheels1.rotate(0, FastMath.HALF_PI, 0);
        wheels1.setMaterial(mat);
        carControl.addWheel(node1, new Vector3f(-xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius, true);

        Node node2 = new Node("wheel 2 node");
        Geometry wheels2 = new Geometry("wheel 2", wheelMesh);
        node2.attachChild(wheels2);
        wheels2.rotate(0, FastMath.HALF_PI, 0);
        wheels2.setMaterial(mat);
        carControl.addWheel(node2, new Vector3f(xOff, yOff, zOff),
                wheelDirection, wheelAxle, restLength, radius, true);

        Node node3 = new Node("wheel 3 node");
        Geometry wheels3 = new Geometry("wheel 3", wheelMesh);
        node3.attachChild(wheels3);
        wheels3.rotate(0, FastMath.HALF_PI, 0);
        wheels3.setMaterial(mat);
        carControl.addWheel(node3, new Vector3f(-xOff, yOff, -zOff),
                wheelDirection, wheelAxle, restLength, radius, false);

        Node node4 = new Node("wheel 4 node");
        Geometry wheels4 = new Geometry("wheel 4", wheelMesh);
        node4.attachChild(wheels4);
        wheels4.rotate(0, FastMath.HALF_PI, 0);
        wheels4.setMaterial(mat);
        carControl.addWheel(node4, new Vector3f(xOff, yOff, -zOff),
                wheelDirection, wheelAxle, restLength, radius, false);

        vehicleNode.attachChild(node1);
        vehicleNode.attachChild(node2);
        vehicleNode.attachChild(node3);
        vehicleNode.attachChild(node4);

        rootNode.attachChild(vehicleNode);
        
        bulletAppState.getPhysicsSpace().add(carControl);
        carControl.setPhysicsLocation(spawnPoint);
        carControl.setPhysicsRotation(spawnRotation);
    }
    
    @Override
    protected void cleanupPlayer() {
        bulletAppState.getPhysicsSpace().remove(carControl);
    }
    
    @Override
    public void update(float tpf) {
        if (jump) {
            if (/*onGround &&*/ jumpCooldown <= 0) {
                carControl.applyImpulse(carControl.getGravity().negate().divide(2), Vector3f.ZERO);
                jumpCooldown = DEFAULT_JUMP_COOLDOWN;
            }
        }
        steeringValue = steer * 0.5f * FastMath.pow(((MAX_SPEED - carControl.getCurrentVehicleSpeedKmHour()) / MAX_SPEED) , 2);
        carControl.steer(steeringValue);
    }

    public VehicleControl getControl() {
        return carControl;
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        super.onAction(name, isPressed, tpf);
        switch(name) {
            case MAPPING_LEFT:
                if(isPressed) steer++; else steer--;
                break;
            case MAPPING_RIGHT:
                if(isPressed) steer--; else steer++;
                break;
            case MAPPING_UP:
                if (isPressed) {
                    accelerationValue += 2000;
                }
                else {
                    accelerationValue -= 2000;
                }
                break;
            case MAPPING_DOWN:
                if (isPressed && carControl.getCurrentVehicleSpeedKmHour() >= 0) {
                    carControl.brake(40f);
                }
                break;
            case MAPPING_RESET:
                if (isPressed) {
                    carControl.setPhysicsLocation(spawnPoint);
                    carControl.setPhysicsRotation(spawnRotation);
                    carControl.setLinearVelocity(Vector3f.ZERO);
                    carControl.setAngularVelocity(Vector3f.ZERO);
                    carControl.resetSuspension();
                }
                break;
            default: break;
        }
        carControl.accelerate(accelerationValue * ((MAX_SPEED - carControl.getCurrentVehicleSpeedKmHour()) / MAX_SPEED));
        System.out.println(carControl.getCurrentVehicleSpeedKmHour() + ", " + accelerationValue + ", " + (MAX_SPEED - carControl.getCurrentVehicleSpeedKmHour()));
    }

    @Override
    public void causeDmg(int dmg, DMGArt art) {
        switch (art) {
            case SPIKE:
                super.causeDmg((int)(dmg * SPIKE_DMG_GAIN));
                break;
            default:
                super.causeDmg(dmg);
                break;
        }
    }

    @Override
    public Vector3f getLocation() {
        return carControl.getPhysicsLocation();
    }

    @Override
    public void setLocation(Vector3f location) {
        carControl.setPhysicsLocation(location);
    }

    @Override
    public Quaternion getRotation() {
        return carControl.getPhysicsRotation();
    }

    @Override
    public void setRotation(Quaternion rotation) {
        carControl.setPhysicsRotation(rotation);
    }
    
}
