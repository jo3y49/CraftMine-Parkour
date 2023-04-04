package a2.Commands;

import tage.input.action.AbstractInputAction;
import a2.MyGame;
import net.java.games.input.Event;

public class PitchController extends AbstractInputAction {

    private MyGame game;

    public PitchController (MyGame g) { game = g; }

    @Override
    public void performAction(float time, Event e)
    {
        float keyValue = e.getValue();
        if (keyValue > -.2 && keyValue < .2) return; // deadzone

        Pitch p = new Pitch(game, keyValue >= .2 ? false : true);
        p.performAction(time, e);
    }
}
