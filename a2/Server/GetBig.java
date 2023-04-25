package a2.Server;

import tage.ai.behaviortrees.BTCondition;

public class GetBig{
    NPC npc;

    public GetBig(NPC n){
        npc = n;
    }

    protected double check(){
        return npc.getBig();
    }
}
