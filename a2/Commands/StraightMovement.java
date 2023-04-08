package a2.Commands;

import tage.*;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;

import a2.MyGame;
import a2.ProtocolClient;

public class StraightMovement extends AbstractInputAction
{
    private MyGame game;
    private boolean forward;
    private float moveSpeed;
    private ProtocolClient protClient;

    public StraightMovement(MyGame g, ProtocolClient p, boolean f) { game = g; protClient = p; forward = f;  }

    @Override
    public void performAction(float time, Event e)
    {   
        GameObject av = game.getAvatar();

        if (forward)
            moveSpeed = game.getElapsTime()*.006f;
        else    
            moveSpeed = game.getElapsTime()*-.006f;

        av.straightMovement(moveSpeed); 
        protClient.sendMoveMessage(av.getWorldLocation());
    }
}