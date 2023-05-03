package a2.Commands;

import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import tage.*;
import a2.MyGame;

public class Jump  extends AbstractInputAction {

    private MyGame game;

    public Jump(MyGame g){
        game = g;



        
    }

    @Override
    public void performAction(float time, Event e) {

        
            //apply force  0 8 0, upx upy, upz

    }
}
