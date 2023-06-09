package craftmine.Client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import org.joml.*;

import craftmine.MyGame;
import tage.TextureImage;
import tage.networking.client.GameConnectionClient;

public class ProtocolClient extends GameConnectionClient
{
	private MyGame game;
	private GhostManager ghostManager;
	private GhostNPC ghostNPC;
	private UUID id;
	
	public ProtocolClient(InetAddress remoteAddr, int remotePort, ProtocolType protocolType, MyGame game) throws IOException 
	{	super(remoteAddr, remotePort, protocolType);
		this.game = game;
		this.id = UUID.randomUUID();
		ghostManager = game.getGhostManager();
	}
	
	public UUID getID() { return id; }
	
	@Override
	protected void processPacket(Object message)
	{	String strMessage = (String)message;
		System.out.println("message received -->" + strMessage);
		String[] messageTokens = {""};
		try {
			messageTokens = strMessage.split(",");
		} catch (Exception e){}
		
		
		// Game specific protocol to handle the message
		if(messageTokens.length > 0)
		{
			// Handle JOIN message
			// Format: (join,success) or (join,failure)
			if(messageTokens[0].compareTo("join") == 0)
			{	if(messageTokens[1].compareTo("success") == 0)
				{	System.out.println("join success confirmed");
					game.setIsConnected(true);
					sendCreateMessage();
				}
				if(messageTokens[1].compareTo("failure") == 0)
				{	System.out.println("join failure confirmed");
					game.setIsConnected(false);
			}	}
			
			// Handle BYE message
			// Format: (bye,remoteId)
			if(messageTokens[0].compareTo("bye") == 0)
			{	// remove ghost avatar with id = remoteId
				// Parse out the id into a UUID
				UUID ghostID = UUID.fromString(messageTokens[1]);
				ghostManager.removeGhostAvatar(ghostID);
			}
			
			// Handle CREATE message
			// Format: (create,remoteId,x,y,z)
			// AND
			// Handle DETAILS_FOR message
			// Format: (dsfr,remoteId,x,y,z)
			if (messageTokens[0].compareTo("create") == 0 || (messageTokens[0].compareTo("dsfr") == 0))
			{	// create a new ghost avatar
				// Parse out the id into a UUID
				UUID ghostID = UUID.fromString(messageTokens[1]);
				
				// Parse out the position into a Vector3f
				Vector3f ghostPosition = new Vector3f(
					Float.parseFloat(messageTokens[2]),
					Float.parseFloat(messageTokens[3]),
					Float.parseFloat(messageTokens[4]));

				int avatarIndex = Integer.parseInt(messageTokens[5]);

				try
				{	ghostManager.createGhostAvatar(ghostID, ghostPosition, avatarIndex);
				}	catch (IOException e)
				{	System.out.println("error creating ghost avatar");
				}
			}
			
			// Handle WANTS_DETAILS message
			// Format: (wsds,remoteId)
			if (messageTokens[0].compareTo("wsds") == 0)
			{
				// Send the local client's avatar's information
				// Parse out the id into a UUID
				UUID ghostID = UUID.fromString(messageTokens[1]);
				sendDetailsForMessage(ghostID, game.getPlayerPosition());
			}
			
			// Handle MOVE message
			// Format: (move,remoteId,x,y,z)
			if (messageTokens[0].compareTo("move") == 0)
			{
				// move a ghost avatar
				// Parse out the id into a UUID
				UUID ghostID = UUID.fromString(messageTokens[1]);
				
				// Parse out the position into a Vector3f
				Vector3f ghostPosition = new Vector3f(
					Float.parseFloat(messageTokens[2]),
					Float.parseFloat(messageTokens[3]),
					Float.parseFloat(messageTokens[4]));
				
				ghostManager.updateGhostAvatar(ghostID, ghostPosition);
			}	

			//----------NPC--------------

			// Handle createNPC message
			// Format: (createNPC,id,x,y,z)

			if (messageTokens[0].compareTo("createNPC") == 0)
			{ // create a new ghost NPC
			// Parse out the position
			int npcid = Integer.parseInt(messageTokens[1]);
			Vector3f ghostPosition = new Vector3f(
			Float.parseFloat(messageTokens[2]),
			Float.parseFloat(messageTokens[3]),
			Float.parseFloat(messageTokens[4]));
			try
			{ ghostManager.createGhostNPC(npcid, ghostPosition);
			} catch (IOException e) { e.printStackTrace(); } // error creating ghost avatar
			}
			// Handle moneNPC message
			// Format: (monveNPC,id, x,y,z,distance )

			if (messageTokens[0].compareTo("moveNPC") == 0)
			{ // moves npc and checks if near avatar
				// Parse out the position
				int id = Integer.parseInt(messageTokens[1]);
				Vector3f ghostPosition = new Vector3f(
				Float.parseFloat(messageTokens[2]),
				Float.parseFloat(messageTokens[3]),
				Float.parseFloat(messageTokens[4]));

				ghostManager.updateGhostNPC(id, ghostPosition);
			}
			if (messageTokens[0].compareTo("location") == 0){
				sendLocationMessage();
			}
		}
	}	
	
	// The initial message from the game client requesting to join the 
	// server. localId is a unique identifier for the client. Recommend 
	// a random UUID.
	// Message Format: (join,localId,texture)
	
	public void sendJoinMessage()
	{	try 
		{	sendPacket(new String("join," + id.toString()));
		} catch (IOException e) 
		{	e.printStackTrace();
	}	}
	
	// Informs the server that the client is leaving the server. 
	// Message Format: (bye,localId)

	public void sendByeMessage()
	{	try 
		{	sendPacket(new String("bye," + id.toString()));
		} catch (IOException e) 
		{	e.printStackTrace();
	}	}
	
	// Informs the server of the client�s Avatar�s position. The server 
	// takes this message and forwards it to all other clients registered 
	// with the server.
	// Message Format: (create,localId,x,y,z) where x, y, and z represent the position

	public void sendCreateMessage()
	{	try 
		{	String message = new String("create," + id.toString());

			message += "," + game.getAvatar().getWorldLocation().x();
			message += "," + game.getAvatar().getWorldLocation().y();
			message += "," + game.getAvatar().getWorldLocation().z();
			message += "," + game.getAvatarIndex();
			
			sendPacket(message);
		} catch (IOException e) 
		{	e.printStackTrace();
	}	}

	public void sendNearMessage()
	{ 	try
		{
			System.out.println("avatar near npc");
			String message = "isnear";

			message += "," + game.getAvatar().getWorldLocation().x();
			message += "," + game.getAvatar().getWorldLocation().y();
			message += "," + game.getAvatar().getWorldLocation().z();

			sendPacket(message);
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	// Informs the server of the local avatar's position. The server then 
	// forwards this message to the client with the ID value matching remoteId. 
	// This message is generated in response to receiving a WANTS_DETAILS message 
	// from the server.
	// Message Format: (dsfr,remoteId,localId,x,y,z) where x, y, and z represent the position.

	public void sendDetailsForMessage(UUID remoteId, Vector3f position)
	{	try 
		{	String message = new String("dsfr," + remoteId.toString() + "," + id.toString());
			message += "," + position.x();
			message += "," + position.y();
			message += "," + position.z();
			message += "," + game.getAvatarIndex();
			
			sendPacket(message);
		} catch (IOException e) 
		{	e.printStackTrace();
	}	}
	
	// Informs the server that the local avatar has changed position.  
	// Message Format: (move,localId,x,y,z) where x, y, and z represent the position.

	public void sendMoveMessage(Vector3f position)
	{	try 
		{	String message = new String("move," + id.toString());
			message += "," + position.x();
			message += "," + position.y();
			message += "," + position.z();
			
			sendPacket(message);
		} catch (IOException e) 
		{	e.printStackTrace();
	}	}

	public void sendLocationMessage()
	{
		try{
			String message = "location";
			message += "," + game.getAvatar().getWorldLocation().x();
			message += "," + game.getAvatar().getWorldLocation().y();
			message += "," + game.getAvatar().getWorldLocation().z();

			sendPacket(message);
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	// ------------- GHOST NPC SECTION --------------

	public void sendNeedNPCMessage() {
		{	try 
			{	String message = new String("needNPC," + id.toString());
				
				sendPacket(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
