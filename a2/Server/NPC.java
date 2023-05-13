package a2.Server;

import org.joml.Vector3f;

public class NPC {
    Vector3f location;
    float dir = .1f;
    double size = 1;
    float speed = .08f;
    boolean seePlayer = false;
    Vector3f targetLocation;
    int id;

    public NPC(int id, Vector3f location){
        this.id = id;
        this.location = new Vector3f(location.x(), location.y() + 2, location.z());
    }

    public void randomizeLocation(int seedX, int seedZ){
        
    }

    public double getX() {return location.x;}
    public double getY() {return location.y;}
    public double getZ() {return location.z;}

    public void getBig() {size=2;}
    public void getSmall() {size=1;}
    public double getSize() {return size;}
    public Vector3f getLocation() {return location;}

    public void setSeePlayer(boolean s) {seePlayer=s;}
    public void setTargetLocation(Vector3f l) {targetLocation=l;}

    public void updateLocation(){
        if (seePlayer) {
            Vector3f direction = new Vector3f();
            targetLocation.sub(location, direction).normalize();
            direction.mul(speed);
            location.add(new Vector3f(direction.x(), 0, direction.z()));
        }
        else {
            if (location.x > 10) dir=-.1f;
            if (location.x < -10) dir=.1f;
            location.add(dir, 0, 0);
        }
    }
}
