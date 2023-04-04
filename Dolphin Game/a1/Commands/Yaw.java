package a1.Commands;

import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;

import a1.MyGame;

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
        
        if (game.getRide())
        {
            game.getAvatar().yaw(left ? rotationSpeed : -rotationSpeed);
        } else 
        {
            (game.getEngine().getRenderSystem()).getViewport("MAIN").getCamera().yaw(left ? rotationSpeed : -rotationSpeed); 
        }
    }
}