package craftmine.Server.AIAction;

import craftmine.Server.GameServer;
import craftmine.Server.NPC;
import craftmine.Server.NPCcontroller;
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
