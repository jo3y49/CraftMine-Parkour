package a1.Commands;

import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;

import a1.MyGame;

public class Pitch extends AbstractInputAction
{
    private MyGame game;
    private boolean up;
    private float pitchSpeed;

    public Pitch (MyGame g, boolean u) { game = g; up = u; }

    @Override
    public void performAction(float time, Event e)
    {
        pitchSpeed = game.getFrameTime()*.002f;

        if (game.getRide())
        {
            game.getAvatar().pitch(up ? -pitchSpeed : pitchSpeed);
        } else 
        {
            (game.getEngine().getRenderSystem()).getViewport("MAIN").getCamera().pitch(up ? pitchSpeed : -pitchSpeed);
        }
    }
}