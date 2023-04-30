package a2.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import org.joml.Random;

import a2.Server.AIAction.*;
import tage.ai.behaviortrees.*;
import tage.networking.server.GameConnectionServer;

public class NPCcontroller{
    private NPC npc;
    private Random rn = new Random();
    private BehaviorTree bt = new BehaviorTree(BTCompositeType.SELECTOR);
    private boolean nearFlag = false;
    private long thinkStartTime, tickStartTime, lastThinkUpdateTime, lastTickUpdateTime;
    private GameServer server;
    private double criteria = 4;

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
        bt.insert(10, new OneSecPassed(this, npc, false));
        bt.insert(10, new GetSmall(npc));
        bt.insert(20, new AvatarNear(server, this, npc, false));
        bt.insert(20, new GetBig(npc));
    }

    public NPC getNPC() {return npc;}
    public double getCriteria() {return criteria;}
    public boolean getNearFlag() {return nearFlag;}
    public void setNearFlag(boolean f) { nearFlag = f; }
}
