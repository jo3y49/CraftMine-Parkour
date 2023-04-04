package a2;
import tage.input.action.AbstractInputAction; 
import net.java.games.input.Event; 
import tage.*;

public class TurnLeftAction extends AbstractInputAction{ 
    private MyGame game; 
    private GameObject av; 

    public TurnLeftAction(MyGame g) 
    { game = g; } 


    @Override 
    public void performAction(float time, Event e) { 
        float keyValue = e.getValue(); 
        if (keyValue > -.2 && keyValue < .2) return;  // deadzone 


            av = game.getAvatar(); 
            av.Yaw(game.getLastFrameTime());
        

 }
}
