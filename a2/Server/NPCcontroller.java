package a2.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import org.joml.Random;

import tage.ai.behaviortrees.*;
import tage.networking.server.GameConnectionServer;

public class NPCcontroller{
    private NPC npc;
    Random rn = new Random();
    BehaviorTree bt = new BehaviorTree(BTCompositeType.SELECTOR);
    boolean nearFlag = false;
    long thinkStartTime, tickStartTime, lastThinkUpdateTime, lastTickUpdateTime;
    GameAIServerUDP server;
    double criteria = 2;

    public void updateNPCs(){
        npc.updateLocation();
    }

    public void start(GameAIServerUDP s){
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
