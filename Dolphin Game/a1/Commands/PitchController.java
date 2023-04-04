package a1.Commands;

import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;

import a1.MyGame;

public class PitchController extends AbstractInputAction {

    private MyGame game;

    public PitchController (MyGame g) { game = g; }

    @Override
    public void performAction(float time, Event e)
    {
        float keyValue = e.getValue();
        if (keyValue > -.2 && keyValue < .2) return; // deadzone

        Pitch p = new Pitch(game, keyValue >= .2 ? true : false);
        p.performAction(time, e);
    }
}
