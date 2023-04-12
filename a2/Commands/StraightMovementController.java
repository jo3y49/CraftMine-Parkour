package a2.Commands;

import tage.input.action.AbstractInputAction;
import a2.MyGame;
import net.java.games.input.Event;

public class StraightMovementController extends AbstractInputAction
{
    private MyGame game;
    private float moveSpeedWeight; 

    public StraightMovementController(MyGame g, float msw) { game = g; moveSpeedWeight = msw;}

    @Override
    public void performAction(float time, Event e)
    {
        float keyValue = e.getValue();
        if (keyValue > -.4 && keyValue < .4) return; // deadzone

        StraightMovement sw = new StraightMovement(game, keyValue <= -.4 ? true : false, moveSpeedWeight);
        sw.performAction(time, e);
    }
}