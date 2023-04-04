package a2;
import tage.input.action.AbstractInputAction; 
import net.java.games.input.Event; 
import tage.*;


public class Nmove extends AbstractInputAction{
    private MyGame game; 
    private GameObject av; 


    public Nmove(MyGame g) 
    { game = g; } 


    @Override 
    public void performAction(float time, Event e) { 
        float keyValue = e.getValue(); 
        if (keyValue > -.2 && keyValue < .2) return;  // deadzone 

        av = game.getAvatar(); 
        if (keyValue > 0){av.zMove(-game.getLastFrameTime());}
        else {av.zMove(game.getLastFrameTime());}
    }
}
