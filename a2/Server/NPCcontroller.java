package a2.Server;

import java.util.ArrayList;
import java.util.UUID;
import java.util.Vector;

import org.joml.Random;
import org.joml.Vector3f;

import a2.Server.AIAction.*;
import tage.ai.behaviortrees.*;

public class NPCcontroller{
    private Vector<NPC> npcs = new Vector<NPC>();
    private Random rn = new Random();
    private BehaviorTree bt = new BehaviorTree(BTCompositeType.SELECTOR);
    private boolean nearFlag = false;
    private long thinkStartTime, tickStartTime, lastThinkUpdateTime, lastTickUpdateTime;
    private GameServer server;
    private double criteria = 8;

    public void updateNPCs(){
        for (int i = 0; i < npcs.size(); i++) {
            npcs.get(i).updateLocation();
        }
    }

    public void start(GameServer s){
        thinkStartTime = System.nanoTime();
        tickStartTime = System.nanoTime();
        lastThinkUpdateTime = thinkStartTime;
        lastTickUpdateTime = tickStartTime;
        server = s;
        setupNPCs();
        npcLoop();
    }

    public void setupNPCs(){
        npcs.add(new NPC(0, new Vector3f(0, 0, 0)));
        npcs.add(new NPC(1, new Vector3f(0,0,-10)));
        setupBehaviorTree(npcs.get(0));
        setupBehaviorTree(npcs.get(1));
    }

    public void npcLoop(){
        while(true){
            long currentTime = System.nanoTime();
            float elapsedThinkMillisecs = (currentTime-lastThinkUpdateTime)/1000000f;
            float elapsedTickMillisecs = (currentTime-lastTickUpdateTime)/1000000f;

            if(elapsedTickMillisecs >= 25f){
                lastTickUpdateTime = currentTime;
                updateNPCs();
                server.sendNPCinfo();
            }
            if (elapsedThinkMillisecs >= 250f){
                lastThinkUpdateTime = currentTime;
                bt.update(elapsedThinkMillisecs);
            }
            Thread.yield();
        }
    }

    public void setupBehaviorTree(NPC npc){
        bt.insertAtRoot(new BTSequence(10));
        bt.insertAtRoot(new BTSequence(20));
        bt.insert(10, new AvatarNear(server, this, npc, false));
        bt.insert(10, new MoveToPlayer(npc));
        bt.insert(10, new GetBig(npc));
        bt.insert(20, new AvatarNear(server, this, npc, true));
        // bt.insert(20, new OneSecPassed(this, npc, false));
        // bt.insert(20, new MoveToOrigin(npc));
        bt.insert(20, new GetSmall(npc));
    }

    public void handleNear(Vector3f playerLocation) {
        

        for (int i = 0; i < npcs.size(); i++){
            npcs.get(i).setTargetLocation(playerLocation);
        }
    }

    public Vector<NPC> getNPCs() {return npcs;}
    public double getCriteria() {return criteria;}
    public boolean getNearFlag() {return nearFlag;}
    public void setNearFlag(boolean f) { nearFlag = f; }
}
