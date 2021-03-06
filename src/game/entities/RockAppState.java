/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.entities;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.ChaseCamera;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import game.gui.GUIAppState;
import game.main.appstates.GameAppState;
import beans.DMGArt;
import beans.PlayerInfo;
import com.jme3.collision.CollisionResults;
import com.jme3.scene.Spatial;
import game.main.appstates.TrapPlaceAppState;
import game.utils.AudioPlayer;
import java.util.List;

/**
 *
 * @author Andreas Fruhwirt
 */
public class RockAppState extends CharacterAppState {

    private static final int UPS_LIMITATION = 100;
    private static final float SPIKE_DMG_REDUCE = 3f;
    private static final float RADIUS = 3;
    
    private RigidBodyControl rockControl;
    
    private float deltaU = 0.0f;

    public RockAppState(BulletAppState bulletAppState, GameAppState gameAppState,
            int maxHP, Vector3f spawnPoint, Quaternion spawnRotation,
            Node terrainNode, PlayerInfo playerInfo, AudioPlayer audioPlayer) {
        this(null, gameAppState, bulletAppState, maxHP, spawnPoint, spawnRotation, terrainNode, playerInfo, audioPlayer);
    }
    
    public RockAppState(GUIAppState guiAppState, GameAppState gameAppState,
            BulletAppState bulletAppState, int maxHP, Vector3f spawnPoint,
            Quaternion spawnRotation, Node terrainNode, PlayerInfo playerInfo,
            AudioPlayer audioPlayer) {
        super(guiAppState, gameAppState, bulletAppState, maxHP, spawnPoint, spawnRotation, terrainNode, playerInfo, audioPlayer);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
    }
    
    @Override
    public void initCamera() {
        cam.setLocation(getLocation());
        cam.setRotation(getRotation());
//        camNode = new CameraNode("CameraNode", cam);
//        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
//        vehicleNode.attachChild(camNode);
//        camNode.setLocalTranslation(0, 5, -15);

        //Work in Progresser als af
        chaseCam = new ChaseCamera(cam, geometry, inputManager);
        //chaseCam.setSmoothMotion(true);
        //chaseCam.setTrailingEnabled(true);
        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setLookAtOffset(new Vector3f(0, 2, 0));
        chaseCam.setDefaultDistance(14);
        chaseCam.setEnabled(true);
        chaseCam.setMaxVerticalRotation(FastMath.PI / 16);
        chaseCam.setDefaultVerticalRotation(FastMath.PI / 16);
        //chaseCam.setTrailingSensitivity(10);
        chaseCam.setZoomSensitivity(1000);
    }

    @Override
    protected void initPlayer() {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture texture = assetManager.loadTexture("Textures/Tile/Stone.jpg");
        mat.setTexture("ColorMap", texture);

        Sphere sphere = new Sphere(20, 20, RADIUS);
        sphere.setBound(new BoundingSphere());
        sphere.updateBound();
        geometry = new Geometry("Rocksphere", sphere);
        geometry.setCullHint(Geometry.CullHint.Never);
        geometry.setMaterial(mat);

        CollisionShape collShape = CollisionShapeFactory.createDynamicMeshShape(geometry);

        rockControl = new RigidBodyControl(collShape);
        geometry.addControl(rockControl);

        rootNode.attachChild(geometry);
        bulletAppState.getPhysicsSpace().add(rockControl);
        
        rockControl.setPhysicsLocation(spawnPoint);
        rockControl.setPhysicsRotation(spawnRotation);
        rockControl.setGravity(GRAVITY.subtract(new Vector3f(0, 25f, 0)));
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        deltaU += tpf * UPS_LIMITATION;
        while(deltaU >= 1) {
            float divisor = 4.0f;
            Vector3f brakeChange = new Vector3f(), impulsVector;
            Vector3f speedVector = rockControl.getLinearVelocity();
            Vector3f lookVector = new Vector3f(0, 0, 0);
            if (left) {
                lookVector.addLocal(cam.getLeft());                 /////////// LEFT
            }
            if (right) {
                lookVector.addLocal(cam.getLeft().negate());        /////////// RIGHT
            }
            if (forward) {
                lookVector.addLocal(cam.getDirection());            /////////// UP
            }
            if (backward) {
                lookVector.addLocal(cam.getDirection().negate());   /////////// DOWN 
            }
            impulsVector = lookVector.normalize().divide(divisor);
            if(speedVector.x != 0) {
                if(speedVector.x < 0 && impulsVector.x > 0) {
                    brakeChange.x = -speedVector.x / 600f;
                } else if(speedVector.x > 0 && impulsVector.x < 0) {
                    brakeChange.x = -speedVector.x / 600f;
                }
            }
            if(speedVector.z != 0) {
                if(speedVector.z < 0 && impulsVector.z > 0) {
                    brakeChange.z = -speedVector.z / 600f;
                } else if(speedVector.z > 0 && impulsVector.z < 0) {
                    brakeChange.z = -speedVector.z / 600f;
                }
            }
            rockControl.applyImpulse(impulsVector.add(brakeChange).setY(0), new Vector3f(0, 1, 0));
            
            for (List<Spatial> spatials : gameAppState.getPlacedTraps().values()) {
                for(Spatial spatial : spatials) {
                    if(geometry.collideWith(spatial.getWorldBound(), new CollisionResults()) > 0) {
                        if(spatial.getUserData(TrapPlaceAppState.DMG_ART_KEY) == DMGArt.BUSHES.name()) {
                            Vector3f oldVelocity = rockControl.getLinearVelocity();
                            float vx = rockControl.getLinearVelocity().x;
                            float vy = rockControl.getLinearVelocity().y;
                            float vz = rockControl.getLinearVelocity().z;
                            if(Math.abs(vx) > 10) {
                                oldVelocity.setX(vx > 0 ? 10 : -10);
                                rockControl.setLinearVelocity(oldVelocity);
                            }
                            if(Math.abs(vy) > 10) {
                                oldVelocity.setY(vy > 0 ? 10 : -10);
                                rockControl.setLinearVelocity(oldVelocity);
                            }
                            if(Math.abs(vz) > 10) {
                                oldVelocity.setZ(vz > 0 ? 10 : -10);
                                rockControl.setLinearVelocity(oldVelocity);
                            }
                        }
                    }
                }
            }
            deltaU--;
        }
    }

    @Override
    protected void cleanupPlayer() {
        bulletAppState.getPhysicsSpace().remove(rockControl);
        super.cleanupPlayer();
    }

    @Override
    public void setSpawnPoint(Vector3f spawnPoint) {
        super.setSpawnPoint(spawnPoint);
        this.spawnPoint.setY(this.spawnPoint.getY() + RADIUS);
    }

    public RigidBodyControl getControl() {
        return rockControl;
    }

    @Override
    public void causeDmg(double dmg, DMGArt art) {
        switch (art) {
            case SPIKE:
                super.causeDmg((int)(dmg / SPIKE_DMG_REDUCE));
                break;
            case BOUNCE: break;
            case TRAFFICCONE: break;
            case BUSHES: break;
            default:
                super.causeDmg((int)dmg);
                break;
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        super.onAction(name, isPressed, tpf);
        if(this.isEnabled()) {
            if (name.equals(MAPPING_RESET)) {
                if (isPressed) {
                    rockControl.setPhysicsLocation(spawnPoint);
                    rockControl.setPhysicsRotation(spawnRotation);
                    rockControl.setLinearVelocity(Vector3f.ZERO);
                    rockControl.setAngularVelocity(Vector3f.ZERO);
                }
            }
        }
    }

    @Override
    public Vector3f getLocation() {
        return rockControl.getPhysicsLocation();
    }

    @Override
    public void setLocation(Vector3f location) {
        rockControl.setPhysicsLocation(location);
    }

    @Override
    public Quaternion getRotation() {
        return rockControl.getPhysicsRotation();
    }

    @Override
    public void setRotation(Quaternion rotation) {
        rockControl.setPhysicsRotation(rotation);
    }

}
