package a1.Commands;

import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import tage.*;
import org.joml.*;

import a1.MyGame;
public class Mount extends AbstractInputAction {
    private MyGame game;
    
    public Mount(MyGame g) {game = g;}

    @Override
    public void performAction(float time, Event e)
    {
        Camera c = (game.getEngine().getRenderSystem()).getViewport("MAIN").getCamera();
        GameObject av = game.getAvatar();

        if (game.getRide())
        {
            game.setRide(false);

            Vector3f loc = av.getWorldLocation();
            Vector3f fwd = av.getWorldForwardVector();
            Vector3f up = av.getWorldUpVector();
            Vector3f right = av.getWorldRightVector();
            
            c.setU(right);
            c.setV(up);
            c.setN(fwd);
            c.setLocation(loc.add(up.mul(-.02f)).add(right.mul(1.2f).add(fwd.mul(.3f))));
        } else
        {
            if (av.getLocalLocation().distance(c.getLocation()) <= 3f && av.getRenderStates().renderingEnabled())
                game.setRide(true);
        }
    }
}
