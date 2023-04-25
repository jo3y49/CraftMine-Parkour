package a2.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import org.joml.Random;

import tage.ai.behaviortrees.BTCompositeType;
import tage.ai.behaviortrees.BTSequence;
import tage.ai.behaviortrees.BehaviorTree;
import tage.networking.server.GameConnectionServer;

public class GameAIServerUDP extends GameConnectionServer<UUID>{
    NPCcontroller npcCtrl;

    public GameAIServerUDP(int localPort, NPCcontroller npc){
        super(localPort, ProtocolType.UDP);
        npcCtrl = npc;
    }

    public void sendCheckForAvatarNear(){
        try{
            String message = new String("isnr");
            message += "," + (npcCtrl.getNPC()).getX();
            message += "," + (npcCtrl.getNPC()).getY();
            message += "," + (npcCtrl.getNPC()).getZ();
            message += "," + (npcCtrl.getCriteria());
            sendPacketToAll(message);
        } catch(IOException e) {
            System.out.println("couldn't send msg");
            e.printStackTrace();
        }
    }

    public void sendNPCinfo(){

    }
    public void sendNPCstart(UUID clientID){

    }    

    @Override
    public void processPacket(Object o, InetAddress senderIP, int port){
        // Case where server receives request for NPCs
        // Received Message Format: (needNPC,id)
        if(messageTokens[0].compareTo("needNPC") == 0) {
            System.out.println("server got a needNPC message");
            UUID clientID = UUID.fromString(messageTokens[1]);
            sendNPCstart(clientID);
        }
        // Case where server receives notice that an av is close to the npc
        // Received Message Format: (isnear,id)
        if(messageTokens[0].compareTo("isnear") == 0) {
            UUID clientID = UUID.fromString(messageTokens[1]);
            handleNearTiming(clientID);
        }
    }
    public void handleNearTiming(UUID clientID){
        npcCtrl.setNearFlag(true);
    }
// ------------ SENDING NPC MESSAGES -----------------
// Informs clients of the whereabouts of the NPCs.

    public void sendCreateNPCmsg(UUID clientID, String[] position){
        try {
            System.out.println("server telling clients about an NPC");
            String message = new String("create NPC," + clientID.toString());
            message += "," + position[0];
            message += "," + position[1];
            message += "," + position[2];
            forwardPacketToAll(message, clientID);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

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
    }
}
