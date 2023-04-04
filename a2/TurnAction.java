package a2;
import tage.input.action.AbstractInputAction; 
import net.java.games.input.Event; 
import tage.*;

public class TurnAction extends AbstractInputAction{ 
    private MyGame game; 
    private GameObject av; 


    public TurnAction(MyGame g) 
    { game = g; } 


    @Override 
    public void performAction(float time, Event e) { 
        float keyValue = e.getValue(); 
        if (keyValue > -.2 && keyValue < .2) return;  // deadzone 

            av = game.getAvatar(); 
            if (keyValue > 0){av.Yaw(-game.getLastFrameTime());}
            else {av.Yaw(game.getLastFrameTime());}
       
 }
}
