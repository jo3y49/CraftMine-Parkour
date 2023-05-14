package craftmine.Commands;

import tage.input.action.AbstractInputAction;
import craftmine.MyGame;
import net.java.games.input.Event;

public class YawController extends AbstractInputAction
{
    private MyGame game;
    private float rotationSpeedWeight;

    public YawController (MyGame g, float rsw) { game = g; rotationSpeedWeight = rsw; }

    @Override
    public void performAction(float time, Event e)
    {
        float keyValue = e.getValue();
        if (keyValue > -.4 && keyValue < .4) return; // deadzone

        Yaw y = new Yaw(game, keyValue <= -.4 ? true : false, rotationSpeedWeight);
        y.performAction(time, e);
    }
}