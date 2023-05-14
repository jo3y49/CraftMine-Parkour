package craftmine.Server.AIAction;

import craftmine.Server.NPC;
import craftmine.Server.NPCcontroller;
import tage.ai.behaviortrees.BTCondition;

public class OneSecPassed extends BTCondition{
    NPC npc;
    NPCcontroller npcc;
    
    public OneSecPassed(NPCcontroller c, NPC n, boolean toNegate){
        super(toNegate);
        npcc = c; npc = n;
    }

    protected boolean check(){
        return false;
    }
}
