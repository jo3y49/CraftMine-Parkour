package a2.Server;

public class NPC {
    double locX, locY, locZ;
    double dir = .1;
    double size = 1;

    public NPC(){
        locX = 0;
        locY = 0;
        locZ = 0;
    }

    public void randomizeLocation(int seedX, int seedZ){
        locX = ((double) seedX)/4 - 5;
        locZ = -2;
    }

    public double getX() {return locX;}
    public double getY() {return locY;}
    public double getZ() {return locZ;}

    public void getBig() {size=2;}
    public void getSmall() {size=1;}
    public double getSize() {return size;}

    public void updateLocation(){
        if (locX > 10) dir=-.1f;
        if (locX < -10) dir=.1f;
        locX += dir;
    }
}
