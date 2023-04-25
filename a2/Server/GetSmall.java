package a2.Server;

import tage.ai.behaviortrees.BTCondition;

public class GetSmall{
    NPC npc;

    public GetSmall(NPC n){
        npc = n;
    }

    protected double check(){
        return npc.getSmall();
    }
}
