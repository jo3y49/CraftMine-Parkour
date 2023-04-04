package a2;
import tage.input.action.AbstractInputAction; 
import net.java.games.input.Event; 

public class TurnInPrizes extends AbstractInputAction {


    boolean toggle = false;


    public TurnInPrizes(MyGame g) { 


    } 

    @Override 
    public void performAction(float time, Event e){ 
        toggle = !toggle;

    
    }
    
    public boolean getToggle(){return toggle;}
}
