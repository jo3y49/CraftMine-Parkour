package a2.Server;

import org.joml.Random;
import org.joml.Vector3f;

import a2.Server.AIAction.*;
import tage.ai.behaviortrees.*;

public class NPCcontroller{
    private NPC npc;
    private Random rn = new Random();
    private BehaviorTree bt = new BehaviorTree(BTCompositeType.SELECTOR);
    private boolean nearFlag = false;
    private long thinkStartTime, tickStartTime, lastThinkUpdateTime, lastTickUpdateTime;
    private GameServer server;
    private double criteria = 8;

    public void updateNPCs(){
        npc.updateLocation();
    }

    public void start(GameServer s){
        thinkStartTime = System.nanoTime();
        tickStartTime = System.nanoTime();
        lastThinkUpdateTime = thinkStartTime;
        lastTickUpdateTime = tickStartTime;
        server = s;
        setupNPCs();
        setupBehaviorTree();
        npcLoop();
    }

    public void setupNPCs(){
        npc = new NPC();
        npc.randomizeLocation(rn.nextInt(40), rn.nextInt(40));

    }

    public void npcLoop(){
        while(true){
            long currentTime = System.nanoTime();
            float elapsedThinkMillisecs = (currentTime-lastThinkUpdateTime)/1000000f;
            float elapsedTickMillisecs = (currentTime-lastTickUpdateTime)/1000000f;

            if(elapsedTickMillisecs >= 25f){
                lastTickUpdateTime = currentTime;
                npc.updateLocation();
                server.sendNPCinfo();
            }
            if (elapsedThinkMillisecs >= 250f){
                lastThinkUpdateTime = currentTime;
                bt.update(elapsedThinkMillisecs);
            }
            Thread.yield();
        }
    }

    public void setupBehaviorTree(){
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
        nearFlag = true;
        npc.setTargetLocation(playerLocation);
    }
    public void handleNotNear() {
        nearFlag = false;
        npc.setSeePlayer(false);
    }

    public NPC getNPC() {return npc;}
    public double getCriteria() {return criteria;}
    public boolean getNearFlag() {return nearFlag;}
    public void setNearFlag(boolean f) { nearFlag = f; }
}
