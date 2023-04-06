package tage.nodeControllers;
import tage.*; 
import org.joml.*; 

public class ShrinkController extends NodeController {
    private float shrinkAmount;
    private Engine engine;
    private Matrix4f curScale, newScale;

    public ShrinkController(Engine e, float shrinkAmount) {
        engine = e;
        this.shrinkAmount = shrinkAmount;
        newScale = new Matrix4f();

    }


    public void apply(GameObject go){
        curScale = go.getLocalScale();
        newScale.scaling(curScale.m00() * shrinkAmount, curScale.m11() * shrinkAmount, curScale.m22() * shrinkAmount);
        go.setLocalScale(newScale);
    }
}