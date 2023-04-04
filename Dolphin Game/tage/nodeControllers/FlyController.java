package tage.nodeControllers;
import tage.*;
import org.joml.*;

public class FlyController extends NodeController
{
	private float flySpeed = 1.0f;
	private Vector4f locationMatrix;
    private Vector3f curLocation, newLocation;
	private Engine engine;

	public FlyController(Engine e, float speed)
	{	super();

        flySpeed = speed;
		engine = e;
	}

	public void apply(GameObject go)
	{	float elapsedTime = super.getElapsedTime();
        locationMatrix = new Vector4f(0f,1f,0f,0f);
		curLocation = go.getLocalLocation();

        locationMatrix.mul(elapsedTime * flySpeed);
        newLocation = curLocation.add(locationMatrix.x(),
			locationMatrix.y(), locationMatrix.z());
		go.setLocalLocation(newLocation);

	}
}