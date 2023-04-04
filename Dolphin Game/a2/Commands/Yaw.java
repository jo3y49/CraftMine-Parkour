package a2.Commands;

import tage.input.action.AbstractInputAction;
import a2.MyGame;
import net.java.games.input.Event;

public class Yaw extends AbstractInputAction
{
    private MyGame game;
    private boolean left;
    private float rotationSpeed;

    public Yaw (MyGame g, boolean l) { game = g; left = l; }

    @Override
    public void performAction(float time, Event e)
    {
        rotationSpeed = game.getFrameTime() * .002f;
        
        game.getAvatar().yaw(left ? rotationSpeed : -rotationSpeed);
    }
}