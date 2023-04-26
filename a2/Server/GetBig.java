package a2.Server;

import tage.ai.behaviortrees.BTAction;
import tage.ai.behaviortrees.BTCondition;
import tage.ai.behaviortrees.BTStatus;

public class GetBig extends BTAction{
    NPC npc;

    public GetBig(NPC n){
        super();
        npc = n;
    }

    @Override
    protected BTStatus update(float elapsedTime) {
        
    }
}
