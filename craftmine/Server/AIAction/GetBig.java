package craftmine.Server.AIAction;

import craftmine.Server.NPC;
import tage.ai.behaviortrees.BTAction;
import tage.ai.behaviortrees.BTStatus;

public class GetBig extends BTAction{
    NPC npc;

    public GetBig(NPC n){
        super();
        npc = n;
    }

    @Override
    protected BTStatus update(float elapsedTime) {
        npc.getBig();
        return BTStatus.BH_SUCCESS;
    }
}
