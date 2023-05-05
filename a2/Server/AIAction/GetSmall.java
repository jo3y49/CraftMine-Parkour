package a2.Server.AIAction;

import a2.Server.NPC;
import tage.ai.behaviortrees.BTAction;
import tage.ai.behaviortrees.BTStatus;

public class GetSmall extends BTAction{
    NPC npc;

    public GetSmall(NPC n){
        super();
        npc = n;
    }

    @Override
    protected BTStatus update(float elapsedTime) {
        npc.getSmall();
        return BTStatus.BH_SUCCESS;
    }
}
