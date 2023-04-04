package tage.nodeControllers;

import tage.*;
import org.joml.*;

public class StretchController extends NodeController
{
    private float scaleRate = .0003f;
    private float cycleTime = 2000.0f;
    private float totalTime = 0.0f;
    private float direction = 1.0f;
    private Matrix4f curScale, newScale;
    private Engine engine;

    public StretchController(Engine e, float ctime)
    { 
        super();
        cycleTime = ctime;
        engine = e;
        newScale = new Matrix4f();
    }

    public void apply(GameObject go)
    { 
        float elapsedTime = super.getElapsedTime();
        totalTime += elapsedTime/1000.0f;
        if (totalTime > cycleTime)
        { 
            direction = -direction;
            totalTime = 0.0f;
        }
        curScale = go.getLocalScale();
        float scaleAmt = 1.0f + direction * scaleRate * elapsedTime;
        newScale.scaling(curScale.m00()*scaleAmt, curScale.m11(), curScale.m22());
        go.setLocalScale(newScale);
    }
}
