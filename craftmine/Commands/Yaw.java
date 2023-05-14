package craftmine.Commands;

import tage.input.action.AbstractInputAction;
import craftmine.MyGame;
import net.java.games.input.Event;

public class Yaw extends AbstractInputAction
{
    private MyGame game;
    private boolean left;
    private float rotationSpeed;
    private float rotationSpeedWeight;

    public Yaw (MyGame g, boolean l, float rsw) { game = g; left = l; rotationSpeedWeight = rsw;}

    @Override
    public void performAction(float time, Event e)
    {
        rotationSpeed = game.getFrameTime() * rotationSpeedWeight;
        
        game.getAvatar().yaw(left ? rotationSpeed : -rotationSpeed);
    }
}