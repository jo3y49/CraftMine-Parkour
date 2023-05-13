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
    private float moveSpeedWeight;

    public StraightMovement(MyGame g, boolean f, float msw) { game = g; forward = f; moveSpeedWeight = msw;}

    @Override
    public void performAction(float time, Event e)
    {   
        av = game.getAvatar();

        if (forward)
            moveSpeed = game.getFrameTime()* moveSpeedWeight;
        else    
            moveSpeed = game.getFrameTime()*-moveSpeedWeight;
    


            game.avatarPhysics(moveSpeed);
            game.handleAvatarAnimation("walk");


        game.getProtClient().sendMoveMessage(av.getWorldLocation());
    }
}