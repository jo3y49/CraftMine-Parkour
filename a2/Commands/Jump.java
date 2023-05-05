package a2.Commands;

import tage.input.action.AbstractInputAction;
import tage.physics.PhysicsObject;
import net.java.games.input.Event;
import tage.*;
import a2.MyGame;

public class Jump  extends AbstractInputAction {

    private MyGame game;
    private PhysicsObject physicsObj;

    public Jump(MyGame g, PhysicsObject p){
        game = g;
        physicsObj = p;



        
    }

    @Override
    public void performAction(float time, Event e) {

        GameObject ball = game.getBall();
            //apply force  0 8 0, upx upy, upz
            physicsObj.applyForce(0, 8, 0, ball.getLocalLocation().x(), ball.getLocalLocation().y(), ball.getLocalLocation().z());
            System.out.println("test");
            

    }
}
