package craftmine.Commands;

import tage.input.action.AbstractInputAction;
import craftmine.MyGame;
import net.java.games.input.Event;

public class Quit extends AbstractInputAction {

    private MyGame game;

    public Quit (MyGame g) { game = g; }

    @Override
    public void performAction(float time, Event e)
    {
        game.killGame();
    }
}