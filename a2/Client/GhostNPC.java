package a2.Client;

import org.joml.*;
import tage.*;

public class GhostNPC extends GameObject{
    private int id;

    public GhostNPC(int id, ObjShape s, TextureImage t, Vector3f p){
        super(GameObject.root(), s, t);
        this.id = id;
        setPosition(p);
    }

    public void setSize(boolean big){
        if (!big)
            this.setLocalScale((new Matrix4f()).scaling(.5f));
        else 
            this.setLocalScale((new Matrix4f()).scaling(1));
    }

    public int getID() { return id; }
	public Vector3f getPosition() { return getWorldLocation(); }
    public void setPosition(Vector3f p) { setLocalLocation(p); }
}