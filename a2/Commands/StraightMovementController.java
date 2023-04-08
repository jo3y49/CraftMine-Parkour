package a2.Commands;

import tage.input.action.AbstractInputAction;
import a2.MyGame;
import a2.Client.ProtocolClient;
import net.java.games.input.Event;

public class StraightMovementController extends AbstractInputAction
{
    private MyGame game;
    private ProtocolClient protClient;

    public StraightMovementController(MyGame g, ProtocolClient p) { game = g; protClient = p; }

    @Override
    public void performAction(float time, Event e)
    {
        float keyValue = e.getValue();
        if (keyValue > -.4 && keyValue < .4) return; // deadzone

        StraightMovement sw = new StraightMovement(game, protClient, keyValue <= -.4 ? true : false);
        sw.performAction(time, e);
    }
}