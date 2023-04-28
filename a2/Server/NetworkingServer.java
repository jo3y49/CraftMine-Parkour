package a2.Server;
import java.io.IOException;

public class NetworkingServer 
{
	private GameServer UDPServer;
	private NPCcontroller npcCtrl;

	public NetworkingServer(int serverPort, String protocol) 
	{	
		npcCtrl = new NPCcontroller();

		try{
			UDPServer = new GameServer(serverPort, npcCtrl);
		} catch (IOException e){
			System.out.println("server didn't start");
			e.printStackTrace();
		}
		System.out.println("ai server starting");
		npcCtrl.start(UDPServer);

	}

	public static void main(String[] args) 
	{	if(args.length > 0)
		{	NetworkingServer app = new NetworkingServer(Integer.parseInt(args[0]), args[1]);
		}
	}

}
