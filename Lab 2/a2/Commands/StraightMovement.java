package a2.Commands;

import tage.*;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;

import a2.MyGame;

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

        if (forward)
            moveSpeed = game.getFrameTime()*.006f;
        else    
            moveSpeed = game.getFrameTime()*-.006f;

        av.straightMovement(moveSpeed); 
    }
}