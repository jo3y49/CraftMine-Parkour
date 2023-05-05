package a2.Server.AIAction;

import a2.Server.NPC;
import tage.ai.behaviortrees.BTAction;
import tage.ai.behaviortrees.BTStatus;

public class MoveToPlayer extends BTAction{
    NPC npc;

    public MoveToPlayer(NPC n){
        super();
        npc = n;
    }

    @Override
    protected BTStatus update(float elapsedTime) {
        npc.setSeePlayer(true);
        return BTStatus.BH_SUCCESS;
    }
}
