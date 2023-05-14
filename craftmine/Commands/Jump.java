package craftmine.Commands;

import tage.input.action.AbstractInputAction;
import craftmine.MyGame;
import net.java.games.input.Event;

public class Jump extends AbstractInputAction
{
    private MyGame game;
    private int direction;

    public Jump (MyGame g, int d) { game = g; direction = d;}

    @Override
    public void performAction(float time, Event e)
    {
        game.avatarJump(direction);
        game.stopAvatarJump();
    }
}