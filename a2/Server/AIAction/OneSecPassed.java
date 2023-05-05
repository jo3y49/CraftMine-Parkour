package a2.Server.AIAction;

import a2.Server.NPC;
import a2.Server.NPCcontroller;
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
