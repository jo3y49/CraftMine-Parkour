package a2;
import tage.*;
import tage.input.action.AbstractInputAction; 
import net.java.games.input.Event; 

public class ToggleLines extends AbstractInputAction {
    private MyGame game; 
    private Boolean toggleLines = false;

    
    public ToggleLines(MyGame g) { 
        game = g; 
    } 


    @Override 
    public void performAction(float time, Event e){ 

        toggleLines = !toggleLines;
        
    } 

    public Boolean getToggleLines (){
        return toggleLines;
    }
    
}
