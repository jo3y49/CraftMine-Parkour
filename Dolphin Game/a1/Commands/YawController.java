package a1.Commands;

import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;

import a1.MyGame;

public class YawController extends AbstractInputAction
{
    private MyGame game;

    public YawController (MyGame g) { game = g; }

    @Override
    public void performAction(float time, Event e)
    {
        float keyValue = e.getValue();
        if (keyValue > -.4 && keyValue < .4) return; // deadzone

        Yaw y = new Yaw(game, keyValue <= -.4 ? true : false);
        y.performAction(time, e);
    }
}