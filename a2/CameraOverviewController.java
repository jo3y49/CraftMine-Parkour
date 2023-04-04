package a2;
import tage.*;
import org.joml.*;
import tage.input.*; 
import tage.input.action.AbstractInputAction; 
import net.java.games.input.Event; 

public class CameraOverviewController {

    private Engine engine; 
    private Camera camera; // the camera being controlled 
    private GameObject avatar; // the target avatar the camera looks at 
    private float cameraPanX; // 
    private float cameraPanZ; // 
    private float cameraZoom; // distance between camera and target 

    public CameraOverviewController(Camera cam, GameObject av, String gpName, Engine e) { 
        engine = e; 
        camera = cam; 
        avatar = av; 
        cameraPanX = 0.0f; // start BEHIND and ABOVE the target 
        cameraPanZ = 0.0f; // elevation is in degrees 
        cameraZoom = 2.0f; // distance from camera to avatar 
        setupInputs(gpName); 
        updateCameraPosition(); 
    } 

    private void setupInputs(String gp) {
        OverviewXAction xAction = new OverviewXAction(); 
        OverviewZoomAction zoomAction = new OverviewZoomAction();
        OverviewZAction zAction = new OverviewZAction();
        OverviewXNegAction xNegAction = new OverviewXNegAction(); 
        OverviewZoomNegAction zoomNegAction = new OverviewZoomNegAction();
        OverviewZNegAction zNegAction = new OverviewZNegAction();
        InputManager im = engine.getInputManager(); 
        if (gp != null){
            im.associateAction(gp,net.java.games.input.Component.Identifier.Button._5, xAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
            im.associateAction(gp,net.java.games.input.Component.Identifier.Button._4, xNegAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            im.associateAction(gp,net.java.games.input.Component.Identifier.Button._7, zAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            im.associateAction(gp,net.java.games.input.Component.Identifier.Button._6, zNegAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 

            im.associateAction(gp,net.java.games.input.Component.Identifier.Button._9, zoomAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
            im.associateAction(gp,net.java.games.input.Component.Identifier.Button._8, zoomNegAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
        }
        im.associateActionWithAllKeyboards( 
            net.java.games.input.Component.Identifier.Key.NUMPAD6, xAction, 
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
        im.associateActionWithAllKeyboards( 
            net.java.games.input.Component.Identifier.Key.NUMPAD4, xNegAction, 
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
        
        im.associateActionWithAllKeyboards( 
            net.java.games.input.Component.Identifier.Key.NUMPAD7, zoomAction, 
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
        im.associateActionWithAllKeyboards( 
            net.java.games.input.Component.Identifier.Key.NUMPAD9, zoomNegAction, 
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
        
        im.associateActionWithAllKeyboards( 
            net.java.games.input.Component.Identifier.Key.NUMPAD2, zAction, 
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
        im.associateActionWithAllKeyboards( 
            net.java.games.input.Component.Identifier.Key.NUMPAD8, zNegAction, 
            InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
    } 


    public void updateCameraPosition() { 
        float x = cameraPanX;
        float y = cameraZoom; 
        float z = cameraPanZ; 
        camera.setLocation(new Vector3f(x,y,z).add(0,0,0)); 
    } 




    private class OverviewXAction extends AbstractInputAction { 
    
        public void performAction(float time, Event event) 
        { 
            float rotAmount; 
            if (event.getValue() < -0.2) 
                { rotAmount=-0.05f;  } 
            else 
            { 
                if (event.getValue() > 0.2) 
                    { rotAmount=0.05f;  } 
                else 
                    { rotAmount=0.0f;  } 
            } 
            cameraPanX += rotAmount; 
            cameraPanX = cameraPanX % 360; 
            updateCameraPosition(); 
        } 
    }

    private class OverviewZoomAction extends AbstractInputAction { 
    
        public void performAction(float time, Event event) 
        { 
            float rotAmount; 
            if (event.getValue() < -0.2) 
                { rotAmount=-0.05f;  } 
            else 
            { 
                if (event.getValue() > 0.2) 
                    { rotAmount=0.05f;  } 
                else 
                    { rotAmount=0.0f;  } 
            } 
            cameraZoom += rotAmount; 
            cameraZoom = cameraZoom % 360; 
            updateCameraPosition(); 
        } 
    }

    private class OverviewZAction extends AbstractInputAction { 
    
        public void performAction(float time, Event event) 
        { 
            float rotAmount; 
            if (event.getValue() < -0.2) 
                { rotAmount=-0.05f;  } 
            else 
            { 
                if (event.getValue() > 0.2) 
                    { rotAmount=0.05f;  } 
                else 
                    { rotAmount=0.0f;  } 
            } 
            cameraPanZ += rotAmount; 
            cameraPanZ = cameraPanZ % 360; 
            updateCameraPosition(); 
        } 
    }




    private class OverviewXNegAction extends AbstractInputAction { 
    
        public void performAction(float time, Event event) 
        { 
            float rotAmount; 
            rotAmount=-0.05f;

            cameraPanX += rotAmount; 
            cameraPanX = cameraPanX % 360; 
            updateCameraPosition(); 
        } 
    }

    private class OverviewZoomNegAction extends AbstractInputAction { 
    
        public void performAction(float time, Event event) 
        { 
            float rotAmount; 
            rotAmount=-0.05f;
            
            cameraZoom += rotAmount; 
            cameraZoom = cameraZoom % 360; 
            updateCameraPosition(); 
        } 
    }

    private class OverviewZNegAction extends AbstractInputAction { 
    
        public void performAction(float time, Event event) 
        { 
            float rotAmount; 
            rotAmount=-0.05f;

            cameraPanZ += rotAmount; 
            cameraPanZ = cameraPanZ % 360; 
            updateCameraPosition(); 
        } 
    }

}
