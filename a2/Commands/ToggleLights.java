package a2.Commands;

import tage.input.action.AbstractInputAction;

import a2.MyGame;
import net.java.games.input.Event;

public class ToggleLights extends AbstractInputAction {

    private MyGame game;

    public ToggleLights (MyGame g) { game = g; }

    @Override
    public void performAction(float time, Event e)
    {
        game.toggleLights();
    }
}