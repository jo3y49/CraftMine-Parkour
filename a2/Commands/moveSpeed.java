package a2.Commands;

import tage.input.action.AbstractInputAction;
import a2.MyGame;
import net.java.games.input.Event;


public class moveSpeed extends AbstractInputAction
{
    private MyGame game;
    private int direction;

    public moveSpeed (MyGame g, int d) { game = g; direction = d;}

    @Override
    public void performAction(float time, Event e)
    {
        if (direction > 0){
            game.increaseAvatarMoveSpeed();
        }
        else{
            game.decreaseAvatarMoveSpeed();
        }

    }
}
