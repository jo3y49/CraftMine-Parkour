package a2.Commands;

import tage.input.action.AbstractInputAction;
import a2.MyGame;
import net.java.games.input.Event;

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

        game.getAvatar().pitch(up ? -pitchSpeed : pitchSpeed);
    }
}