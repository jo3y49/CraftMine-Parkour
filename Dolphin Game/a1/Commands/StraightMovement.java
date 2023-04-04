package a1.Commands;

import tage.*;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;

import a1.MyGame;

public class StraightMovement extends AbstractInputAction
{
    private MyGame game;
    private boolean forward;
    private float moveSpeed;
    private GameObject av;

    public StraightMovement(MyGame g, boolean f) { game = g; forward = f; }

    @Override
    public void performAction(float time, Event e)
    {   
        av = game.getAvatar();

        if (game.getRide())
        {
            if (forward)
                moveSpeed = game.getFrameTime()*.006f;
            else    
                moveSpeed = game.getFrameTime()*-.006f;

            av.straightMovement(moveSpeed);
        } else 
        {
            Camera c = (game.getEngine().getRenderSystem()).getViewport("MAIN").getCamera();
            if (forward)
                moveSpeed = game.getFrameTime()*.002f;
            else    
                moveSpeed = game.getFrameTime()*-.002f;

            if (!av.getRenderStates().renderingEnabled())
            {
                c.straightMovement(moveSpeed);
                return;
            }
            Vector3f oldPosition = c.getLocation();
            Vector3f fwdDirection = c.getN();
            fwdDirection.mul(moveSpeed);
            Vector3f newPosition = oldPosition.add(fwdDirection.x(), fwdDirection.y(), fwdDirection.z());
            if (newPosition.distance(av.getLocalLocation()) <= 7f && av.getRenderStates().renderingEnabled())
                c.straightMovement(moveSpeed);
        }
    }
}