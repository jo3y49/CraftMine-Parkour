package tage;

import org.joml.Vector3f;

import net.java.games.input.Event;
import tage.input.InputManager;
import tage.input.action.AbstractInputAction;

public class CameraOrbit3D{
    private Engine engine;
    private Camera camera;
    private GameObject avatar, barrier;
    private float cameraAzimuth, cameraElevation, newCameraElevation, cameraRadius, newCameraRadius;

    public CameraOrbit3D(Camera cam, GameObject av, GameObject ba, Engine e)
    {
        engine = e;
        camera = cam;
        avatar = av;
        barrier = ba;
        cameraAzimuth = 0f;
        cameraElevation = 20f;
        cameraRadius = 3f;
        newCameraElevation = cameraElevation;
        newCameraRadius = cameraRadius;
        setupInputs();
        updateCameraPosition();
    }

    private void setupInputs()
    {
        OrbitAzimuthAction azmLeft = new OrbitAzimuthAction(true);
        OrbitAzimuthAction azmRight = new OrbitAzimuthAction(false);
        OrbitAzimuthAction azmAction = new OrbitAzimuthAction();

        OrbitRadiusAction orbIn = new OrbitRadiusAction(false);
        OrbitRadiusAction orbOut = new OrbitRadiusAction(true);
        OrbitRadiusAction orbAction = new OrbitRadiusAction();

        OrbitElevationAction eleUp = new OrbitElevationAction(false);
        OrbitElevationAction eleDown = new OrbitElevationAction(true);
        OrbitElevationAction eleAction = new OrbitElevationAction();

        InputManager im = engine.getInputManager();

        im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.RX,
                azmAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.RY,
                eleAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        
        im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.J, azmLeft, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.L, azmRight, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.U, orbIn, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.O, orbOut, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.I, eleUp, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.K, eleDown, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    }

    public void updateCameraPosition()
    {
        Vector3f avatarRot = avatar.getWorldForwardVector();
        double avatarAngle = Math.toDegrees((double) avatarRot.angleSigned(new Vector3f(0,0,-1), new Vector3f(0,1,0)));
        float totalAz = cameraAzimuth - (float) avatarAngle;
        double theta = Math.toRadians(totalAz);
        double phi = Math.toRadians(newCameraElevation);
        float x = newCameraRadius * (float)(Math.cos(phi) * Math.sin(theta));
        float y = newCameraRadius * (float)(Math.sin(phi));
        float z = newCameraRadius * (float)(Math.cos(phi) * Math.cos(theta));

        if (y + avatar.getWorldLocation().y > barrier.getWorldLocation().y)
        {
            camera.setLocation(new Vector3f(x,y,z).add(avatar.getWorldLocation()));
            camera.lookAt(avatar.getWorldLocation());
            cameraElevation = newCameraElevation;
            cameraRadius = newCameraRadius;
        } else {
            newCameraElevation = cameraElevation;
            newCameraRadius = cameraRadius;
        }
    }
    private class OrbitAzimuthAction extends AbstractInputAction {

        private boolean left = false;

        public OrbitAzimuthAction() {}
        
        public OrbitAzimuthAction(boolean l) { left = l; }

        public void performAction(float time, Event event)
        {
            float rotAmount;
            if (left || event.getValue() < -.2)
                rotAmount = -2f;
            else if (event.getValue() > .2)
                rotAmount = 2f;
            else 
                rotAmount = 0f;

            cameraAzimuth += rotAmount;
            cameraAzimuth = cameraAzimuth % 360;
            updateCameraPosition();
        }
    }
    private class OrbitRadiusAction extends AbstractInputAction {

        private boolean out = false;

        public OrbitRadiusAction() {}
        
        public OrbitRadiusAction(boolean o) { out = o; }
        
        public void performAction(float time, Event event)
        {
            float rotAmount;
            if (out || event.getValue() < -.2)
                rotAmount = -.25f;
            else if (event.getValue() > .2)
                rotAmount = .25f;
            else 
                rotAmount = 0f;
            
            
            newCameraRadius = cameraRadius + rotAmount;
            updateCameraPosition();
        }
    }
    private class OrbitElevationAction extends AbstractInputAction {

        private boolean down = false;

        public OrbitElevationAction(){}

        public OrbitElevationAction(boolean d) { down = d; }
        
        public void performAction(float time, Event event)
        {
            float rotAmount;
            if (down || event.getValue() < -.2)
                rotAmount = -2f;
            else if (event.getValue() > .2)
                rotAmount = 2f;
            else 
                rotAmount = 0f;

            newCameraElevation = cameraElevation + rotAmount;
            newCameraElevation = newCameraElevation % 360;
            updateCameraPosition();
        }
    }
}

