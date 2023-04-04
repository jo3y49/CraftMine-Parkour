package a2;
import java.lang.Math; 
import tage.*;
import org.joml.*;
import tage.input.*; 
import tage.input.action.AbstractInputAction; 
import net.java.games.input.Event; 



public class CameraOrbitController {

    private Engine engine; 
    private Camera camera; // the camera being controlled 
    private GameObject avatar; // the target avatar the camera looks at 
    private float cameraAzimuth; // rotation around target Y axis 
    private float cameraElevation; // elevation of camera above target 
    private float cameraRadius; // distance between camera and target 

    private float aziSpeed = 0.8f;
    private float radSpeed = 0.1f;
    private float eleSpeed = 0.6f;

    public CameraOrbitController(Camera cam, GameObject av, String gpName, Engine e) { 
        engine = e; 
        camera = cam; 
        avatar = av; 
        cameraAzimuth = 0.0f; // start BEHIND and ABOVE the target 
        cameraElevation = 20.0f; // elevation is in degrees 
        cameraRadius = 2.0f; // distance from camera to avatar 
        setupInputs(gpName); 
        updateCameraPosition(); 
    } 


    private void setupInputs(String gp) {
        OrbitAzimuthAction azmAction = new OrbitAzimuthAction(); 
        OrbitRadiusAction radAction = new OrbitRadiusAction();
        OrbitElevationAction eleAction = new OrbitElevationAction();
        OrbitAzimuthNegAction azmNegAction = new OrbitAzimuthNegAction(); 
        OrbitRadiusNegAction radNegAction = new OrbitRadiusNegAction();
        OrbitElevationNegAction eleNegAction = new OrbitElevationNegAction();
        InputManager im = engine.getInputManager(); 
        if (gp != null){
            im.associateAction(gp,net.java.games.input.Component.Identifier.Axis.Z, azmAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
            im.associateAction(gp,net.java.games.input.Component.Identifier.Axis.RZ, eleAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
            im.associateAction(gp,net.java.games.input.Component.Identifier.Button._1, radAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
            im.associateAction(gp,net.java.games.input.Component.Identifier.Button._2, radNegAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);       
        }
        im.associateActionWithAllKeyboards( 
            net.java.games.input.Component.Identifier.Key.J, azmAction, 
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
        im.associateActionWithAllKeyboards( 
            net.java.games.input.Component.Identifier.Key.L, azmNegAction, 
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
        
        im.associateActionWithAllKeyboards( 
            net.java.games.input.Component.Identifier.Key.O, radAction, 
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
        im.associateActionWithAllKeyboards( 
            net.java.games.input.Component.Identifier.Key.U, radNegAction, 
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
        
        im.associateActionWithAllKeyboards( 
            net.java.games.input.Component.Identifier.Key.I, eleAction, 
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
        im.associateActionWithAllKeyboards( 
            net.java.games.input.Component.Identifier.Key.K, eleNegAction, 
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
    } 

    public void updateCameraPosition() { 
        Vector3f avatarRot = avatar.getWorldForwardVector(); 
        double avatarAngle = Math.toDegrees((double)avatarRot.angleSigned(new Vector3f(0,0,-1), new Vector3f(0,1,0))); 
        float totalAz = cameraAzimuth - (float)avatarAngle;
        double theta = Math.toRadians(cameraAzimuth); 
        double phi = Math.toRadians(cameraElevation); 
        float x = cameraRadius * (float)(Math.cos(phi) * Math.sin(theta)); 
        float y = cameraRadius * (float)(Math.sin(phi)); 
        float z = cameraRadius * (float)(Math.cos(phi) * Math.cos(theta)); 
        camera.setLocation(new Vector3f(x,y,z).add(avatar.getWorldLocation())); 
        camera.lookAt(avatar); 
    } 


    private class OrbitAzimuthAction extends AbstractInputAction { 
    
        public void performAction(float time, Event event) 
        { 
            float rotAmount; 
            if (event.getValue() < -0.2) 
                { rotAmount=-aziSpeed;  } 
            else 
            { 
                if (event.getValue() > 0.2) 
                    { rotAmount=aziSpeed;  } 
                else 
                    { rotAmount=0.0f;  } 
            } 
            cameraAzimuth += rotAmount; 
            cameraAzimuth = cameraAzimuth % 360; 
            updateCameraPosition(); 
        } 
    }

    private class OrbitRadiusAction extends AbstractInputAction { 
    
        public void performAction(float time, Event event) 
        { 
            float rotAmount; 
            if (event.getValue() < -0.2) 
                { rotAmount=-radSpeed;  } 
            else 
            { 
                if (event.getValue() > 0.2) 
                    { rotAmount=radSpeed;  } 
                else 
                    { rotAmount=0.0f;  } 
            } 
            cameraRadius += rotAmount; 
            cameraRadius = cameraRadius % 360;
            if (cameraRadius < 1)
                cameraRadius = 1;
            if (cameraRadius > 180)
                cameraRadius = 180;
            updateCameraPosition(); 
        } 
    }

    private class OrbitElevationAction extends AbstractInputAction { 
    
        public void performAction(float time, Event event) 
        { 
            float rotAmount; 
            if (event.getValue() < -0.2) 
                { rotAmount=-eleSpeed;  } 
            else 
            { 
                if (event.getValue() > 0.2) 
                    { rotAmount=eleSpeed;  } 
                else 
                    { rotAmount=0.0f;  } 
            } 

            
            cameraElevation += rotAmount; 
            cameraElevation = cameraElevation % 360; 
            if (cameraElevation < 0)
                cameraElevation = 0;
            if (cameraElevation > 180)
                cameraElevation = 180;
            updateCameraPosition();

        } 
    }




    private class OrbitAzimuthNegAction extends AbstractInputAction { 
    
        public void performAction(float time, Event event) 
        { 
            float rotAmount; 
            rotAmount=-aziSpeed;

            cameraAzimuth += rotAmount; 
            cameraAzimuth = cameraAzimuth % 360; 
            updateCameraPosition(); 
        } 
    }

    private class OrbitRadiusNegAction extends AbstractInputAction { 
    
        public void performAction(float time, Event event) 
        { 
            float rotAmount; 
            rotAmount=-radSpeed;
            
            cameraRadius += rotAmount; 
            cameraRadius = cameraRadius % 360; 
            if (cameraRadius < 1)
                cameraRadius = 1;
            if (cameraRadius > 180)
                cameraRadius = 180;
            updateCameraPosition(); 
        } 
    }

    private class OrbitElevationNegAction extends AbstractInputAction { 
    
        public void performAction(float time, Event event) 
        { 
            float rotAmount; 
            rotAmount=-eleSpeed;

            cameraElevation += rotAmount; 
            cameraElevation = cameraElevation % 360; 
            if (cameraElevation < 0)
                cameraElevation = 0;
            if (cameraElevation > 180)
                cameraElevation = 180;
            updateCameraPosition(); 
        } 
    }

} 



