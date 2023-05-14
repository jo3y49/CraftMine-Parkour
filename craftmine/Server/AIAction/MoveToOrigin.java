package craftmine.Server.AIAction;

import craftmine.Server.NPC;
import tage.ai.behaviortrees.BTAction;
import tage.ai.behaviortrees.BTStatus;

public class MoveToOrigin extends BTAction{
    NPC npc;

    public MoveToOrigin(NPC n){
        super();
        npc = n;
    }

    @Override
    protected BTStatus update(float elapsedTime) {
        npc.setSeePlayer(false);
        return BTStatus.BH_SUCCESS;
    }
}
