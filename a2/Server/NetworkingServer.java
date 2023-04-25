package a2.Server;
import java.io.IOException;

import a2.Server.GameAIServerUDP.NPCcontroller;
import tage.networking.IGameConnection.ProtocolType;

public class NetworkingServer 
{
	private GameServerUDP thisUDPServer;
	private GameServerTCP thisTCPServer;
	private GameAIServerUDP UDPServer;
	private NPCcontroller npcCtrl;

	public NetworkingServer(int serverPort, String protocol) 
	{	
		npcCtrl = new NPCcontroller();

		try{
			UDPServer = new GameAIServerUDP(serverPort, npcCtrl);
		} catch (IOException e){
			System.out.println("server didn't start");
			e.printStackTrace();
		}

		npcCtrl.start(UDPServer);
		try 
		{	if(protocol.toUpperCase().compareTo("TCP") == 0)
			{	thisTCPServer = new GameServerTCP(serverPort);
			}
			else
			{	thisUDPServer = new GameServerUDP(serverPort);
			}
		} 
		catch (IOException e) 
		{	e.printStackTrace();
		}
	}

	public static void main(String[] args) 
	{	if(args.length > 1)
		{	NetworkingServer app = new NetworkingServer(Integer.parseInt(args[0]), args[1]);
		}
	}

}
