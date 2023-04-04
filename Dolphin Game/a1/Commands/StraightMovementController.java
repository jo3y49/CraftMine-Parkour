package a1.Commands;

import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;

import a1.MyGame;

public class StraightMovementController extends AbstractInputAction
{
    private MyGame game;

    public StraightMovementController(MyGame g) { game = g; }

    @Override
    public void performAction(float time, Event e)
    {
        float keyValue = e.getValue();
        if (keyValue > -.4 && keyValue < .4) return; // deadzone

        StraightMovement sw = new StraightMovement(game, keyValue <= -.4 ? true : false);
        sw.performAction(time, e);
    }
}