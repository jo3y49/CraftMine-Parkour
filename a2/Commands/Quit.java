package a2.Commands;

import tage.input.action.AbstractInputAction;

import a2.MyGame;
import a2.Client.ProtocolClient;
import net.java.games.input.Event;

public class Quit extends AbstractInputAction {

    private MyGame game;
    private ProtocolClient protClient;

    public Quit (MyGame g) { game = g; }

    @Override
    public void performAction(float time, Event e)
    {
        protClient = game.getProtClient();
        if (protClient != null && game.getIsClientConnected() == true){
            protClient.sendByeMessage();
        }
        game.killGame();
    }
}