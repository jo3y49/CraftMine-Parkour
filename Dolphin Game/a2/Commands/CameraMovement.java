package a2.Commands;

import tage.Camera;
import tage.input.action.AbstractInputAction;

import java.util.Vector;

import a2.MyGame;
import net.java.games.input.Event;

public class CameraMovement extends AbstractInputAction
{
    private MyGame game;
    private Camera cam;
    private String dir;

    public CameraMovement (Camera c, MyGame g, String d) { cam = c; game = g; dir = d; }

    @Override
    public void performAction(float time, Event e)
    {
        float moveSpeed = game.getFrameTime()*.005f;
        
        switch (dir)
        {
            case "in":
                if (cam.getLocation().y > 1)
                    cam.straightMovement(moveSpeed);
                break;
            case "out":
                cam.straightMovement(-moveSpeed);
                break;
            case "up":
                cam.panVerticalMovement(moveSpeed);
                break;
            case "down":
                cam.panVerticalMovement(-moveSpeed);
                break;
            case "left":
                cam.panHorizontalMovement(-moveSpeed);
                break;
            case "right":
                cam.panHorizontalMovement(moveSpeed);
                break;
        }
    }
}
