package a2.Commands;

import tage.input.action.AbstractInputAction;
import java.util.ArrayList;
import net.java.games.input.Event;
import tage.*;

public class ArrowToggle extends AbstractInputAction{

    private ArrayList<GameObject> arrows = new ArrayList<>();

    public ArrowToggle(GameObject a1, GameObject a2, GameObject a3)
    {
        arrows.add(a1);
        arrows.add(a2);
        arrows.add(a3);
    }

    @Override
    public void performAction(float time, Event e) {
        if (arrows.get(0).getRenderStates().renderingEnabled())
        {
            for (int i = 0; i < 3; i++)
                arrows.get(i).getRenderStates().disableRendering();
        } else {
            for (int i = 0; i < 3; i++)
                arrows.get(i).getRenderStates().enableRendering();
        }
        
    }
}
