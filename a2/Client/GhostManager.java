package a2.Client;

import java.awt.Color;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;
import org.joml.*;

import a2.MyGame;
import tage.*;

public class GhostManager
{
	private MyGame game;
	private Vector<GhostAvatar> ghostAvatars = new Vector<GhostAvatar>();
	private Vector<GhostNPC> ghostNPCs = new Vector<GhostNPC>();

	public GhostManager(VariableFrameRateGame vfrg)
	{	game = (MyGame)vfrg;
	}
	
	public void createGhostAvatar(UUID id, Vector3f position) throws IOException
	{	System.out.println("adding ghost with ID --> " + id);
		ObjShape s = game.getGhostShape();
		TextureImage t = game.getGhostTexture();
		GhostAvatar newAvatar = new GhostAvatar(id, s, t, position);
		Matrix4f initialScale = (new Matrix4f()).scaling(0.25f);
		newAvatar.setLocalScale(initialScale);
		ghostAvatars.add(newAvatar);
	}

	public void createGhostNPC(int id, Vector3f position) throws IOException{
		System.out.println("adding npc with ID --> " + id);
		ObjShape s = game.getNPCShape();
		TextureImage t = game.getNPCTexture();
		GhostNPC newNPC = new GhostNPC(id, s, t, position);
		ghostNPCs.add(newNPC);
	}
	
	public void removeGhostAvatar(UUID id)
	{	GhostAvatar ghostAvatar = findAvatar(id);
		if(ghostAvatar != null)
		{	game.getEngine().getSceneGraph().removeGameObject(ghostAvatar);
			ghostAvatars.remove(ghostAvatar);
		}
		else
		{	System.out.println("tried to remove, but unable to find ghost in list");
		}
	}

	private GhostAvatar findAvatar(UUID id)
	{	GhostAvatar ghostAvatar;
		Iterator<GhostAvatar> it = ghostAvatars.iterator();
		while(it.hasNext())
		{	ghostAvatar = it.next();
			if(ghostAvatar.getID().compareTo(id) == 0)
			{	return ghostAvatar;
			}
		}		
		return null;
	}
	
	public void updateGhostAvatar(UUID id, Vector3f position)
	{	GhostAvatar ghostAvatar = findAvatar(id);
		if (ghostAvatar != null)
		{	ghostAvatar.setPosition(position);
		}
		else
		{	System.out.println("tried to update ghost avatar position, but unable to find ghost in list");
		}
	}

	public void updateGhostNPC(int id, Vector3f position){
		if (ghostNPCs.get(id) != null){
			ghostNPCs.get(id).setPosition(position);
		}
	}

	public boolean checkNear(Vector3f p, double criteria) {
        return game.getPlayerPosition().distance(p) < criteria;
    }
}
