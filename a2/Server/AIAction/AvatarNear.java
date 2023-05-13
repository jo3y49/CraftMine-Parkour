package a2.Server.AIAction;

import a2.Server.GameServer;
import a2.Server.NPC;
import a2.Server.NPCcontroller;
import tage.ai.behaviortrees.BTCondition;

public class AvatarNear extends BTCondition{
    NPC npc;
    NPCcontroller npcc;
    GameServer server;
    
    public AvatarNear(GameServer s, NPCcontroller c, NPC n, boolean toNegate) {
        super(toNegate);
        server = s; npcc = c; npc = n;
    }

    protected boolean check(){
        server.sendLocationMessage();
        return npcc.getNearFlag();
    }
}
