package a2.Server;
import java.io.IOException;

public class NetworkingServer 
{
	private GameServer server;
	private NPCcontroller npcCtrl;

	public NetworkingServer(int serverPort) 
	{	
		npcCtrl = new NPCcontroller();

		try{
			server = new GameServer(serverPort, npcCtrl);
		} catch (IOException e){
			System.out.println("server didn't start");
			e.printStackTrace();
		}
		System.out.println("server starting");
		npcCtrl.start(server);
	}

	public static void main(String[] args) {
		if(args.length > 0) {
			NetworkingServer app = new NetworkingServer(Integer.parseInt(args[0]));
		}
	}
}
