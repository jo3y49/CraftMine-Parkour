package a2;

import tage.*;
import tage.shapes.*;
import tage.input.*;
import tage.input.action.*;
import tage.networking.IGameConnection.ProtocolType;
import tage.nodeControllers.*;

import net.java.games.input.Component.Identifier.*;
import java.io.IOException;
import java.lang.Math;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import org.joml.*;
import com.jogamp.opengl.util.gl2.GLUT;

import a2.Client.*;
import a2.Commands.*;
import a2.Shapes.*;

public class MyGame extends VariableFrameRateGame
{
	private static Engine engine;
	private InputManager im;
	private GhostManager gm;
	private double lastFrameTime, elapsTime;

	private CameraOrbit3D orbitController;
	private Light light1;

	private GameObject avatar, ground, candle, x, y, z;
	private ObjShape dolS, groundS, linxS, linyS, linzS, ghostS, candS;
	private TextureImage doltx, ghostT, candT;

	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private ProtocolClient protClient;
	private boolean isClientConnected = false;

	public MyGame(String serverAddress, int serverPort, String protocol) { 
		super(); 
		gm = new GhostManager(this);
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		if (protocol.toUpperCase().compareTo("TCP") == 0)
			this.serverProtocol = ProtocolType.TCP;
		else 
			this.serverProtocol = ProtocolType.UDP;
	}

	public static void main(String[] args)
	{	MyGame game = new MyGame(args[0], Integer.parseInt(args[1]), args[2]);
		engine = new Engine(game);
		game.initializeSystem();
		game.game_loop();
	}

	@Override
	public void loadShapes()
	{	dolS = new ImportedModel("dolphinHighPoly.obj");
		ghostS = new Sphere();
		groundS = new Plane();
		candS = new ImportedModel("Candle.obj");
		linxS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(5f, 0f, 0f));
		linyS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 5f, 0f));
		linzS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, -5f));
	}

	@Override
	public void loadTextures()
	{	
		doltx = new TextureImage("Dolphin_HighPolyUV.png");
		ghostT = new TextureImage("redDolphin.jpg");
		candT = new TextureImage("Candle.png");
	}	 

	@Override
	public void buildObjects()
	{	Matrix4f initialTranslation, initialScale, initialRotation;

		// build dolphin in the center of the window
		avatar = new GameObject(GameObject.root(), dolS, doltx);
		initialTranslation = (new Matrix4f()).translation(0,1,-5);
		initialScale = (new Matrix4f()).scaling(3.0f);
		initialRotation = (new Matrix4f()).rotationY((float)java.lang.Math.toRadians(135f));
		avatar.setLocalTranslation(initialTranslation);
		avatar.setLocalScale(initialScale);
		avatar.setLocalRotation(initialRotation);

		ground = new GameObject(GameObject.root(), groundS);
		initialTranslation = (new Matrix4f()).translation(0,0,0);
		ground.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(50f);
		ground.setLocalScale(initialScale);

		candle = new GameObject(GameObject.root(), candS, candT);
		initialTranslation = (new Matrix4f()).translation(0,.4f,0);
		initialScale = (new Matrix4f()).scaling(.5f);
		candle.setLocalTranslation(initialTranslation);
		candle.setLocalScale(initialScale);

		// add X, Y, -Z axes
		x = new GameObject(GameObject.root(), linxS);
		y = new GameObject(GameObject.root(), linyS);
		z = new GameObject(GameObject.root(), linzS);
		(x.getRenderStates()).setColor(new Vector3f(1f,1f,0f));
		(y.getRenderStates()).setColor(new Vector3f(0f,1f,0f));
		(z.getRenderStates()).setColor(new Vector3f(0f,0f,1f));
		(x.getRenderStates()).disableRendering();
		(y.getRenderStates()).disableRendering();
		(z.getRenderStates()).disableRendering();
	}

	@Override
	public void initializeLights()
	{	Light.setGlobalAmbient(0.5f, 0.5f, 0.5f);
		light1 = new Light();
		light1.setLocation(new Vector3f(5.0f, 4.0f, 2.0f));
		(engine.getSceneGraph()).addLight(light1);
	}

	@Override
	public void createViewports()
	{
		(engine.getRenderSystem()).addViewport("MAIN", 0, 0, 1f, 1f);
		(engine.getRenderSystem()).addViewport("SMALL", .75f, 0, .25f, .25f);

		Viewport leftVP = (engine.getRenderSystem()).getViewport("MAIN");
		Viewport rightVP = (engine.getRenderSystem()).getViewport("SMALL");
		Camera leftCamera = leftVP.getCamera();
		Camera rightCamera = rightVP.getCamera();

		rightVP.setHasBorder(true);
		rightVP.setBorderWidth(2);
		rightVP.setBorderColor(0, 0, 1);

		leftCamera.setLocation(new Vector3f(0,0,0));
		leftCamera.setU(new Vector3f(1,0,0));
		leftCamera.setV(new Vector3f(0,1,0));
		leftCamera.setN(new Vector3f(0,0,1));

		rightCamera.setLocation(new Vector3f(0,5,0));
		rightCamera.setU(new Vector3f(1,0,0));
		rightCamera.setV(new Vector3f(0,0,-1));
		rightCamera.setN(new Vector3f(0,-1,0));
	}
	
	@Override
	public void initializeGame()
	{	lastFrameTime = System.currentTimeMillis();

		(engine.getRenderSystem()).setWindowDimensions(1900,1000);

		im = engine.getInputManager();

		Camera cM = (engine.getRenderSystem()).getViewport("MAIN").getCamera();
		Camera cS = (engine.getRenderSystem()).getViewport("SMALL").getCamera();

		orbitController = new CameraOrbit3D(cM, avatar, ground, engine);

		StraightMovementController moveController = new StraightMovementController(this);
		StraightMovement moveForward = new StraightMovement(this, true);
		StraightMovement moveBackward = new StraightMovement(this, false);

		YawController YawController = new YawController(this);
		Yaw yawLeft = new Yaw(this, true);
		Yaw yawRight = new Yaw(this, false);

		CameraMovement moveCamIn = new CameraMovement(cS, this, "in");
		CameraMovement moveCamOut = new CameraMovement(cS, this, "out");
		CameraMovement moveCamUp = new CameraMovement(cS, this, "up");
		CameraMovement moveCamDown = new CameraMovement(cS, this, "down");
		CameraMovement moveCamLeft = new CameraMovement(cS, this, "left");
		CameraMovement moveCamRight = new CameraMovement(cS, this, "right");

		ArrowToggle toggle = new ArrowToggle(x, y, z);

		setHeldButtonToGamepad(Axis.Y, moveController);
		setHeldButtonToGamepad(Axis.X, YawController);

		setHeldActionToKeyboard(Key.W, moveForward);
		setHeldActionToKeyboard(Key.S, moveBackward);
		setHeldActionToKeyboard(Key.A, yawLeft);
		setHeldActionToKeyboard(Key.D, yawRight);
		setHeldActionToKeyboard(Key.T, moveCamIn);
		setHeldActionToKeyboard(Key.G, moveCamOut);
		setHeldActionToKeyboard(Key.UP, moveCamUp);
		setHeldActionToKeyboard(Key.DOWN, moveCamDown);
		setHeldActionToKeyboard(Key.LEFT, moveCamLeft);
		setHeldActionToKeyboard(Key.RIGHT, moveCamRight);
		setPressedActionToKeyboard(Key.SPACE, toggle);

		setupNetworking();
	}
	
	@Override
	public void update()
	{	
		elapsTime = System.currentTimeMillis() - lastFrameTime;
		lastFrameTime = System.currentTimeMillis();

		// build and set HUD
		
		String dispStr1 = "Dolphins are EPIC!";
		String dispStr2 = avatar.getWorldLocation().toString();
		Vector3f hudColor = new Vector3f(1,1,1);

		(engine.getHUDmanager()).setHUD1(dispStr1, hudColor, 15, 15);
		(engine.getHUDmanager()).setHUD2(dispStr2, hudColor, 1500, 15);

		(engine.getHUDmanager()).setHUD1font(GLUT.BITMAP_TIMES_ROMAN_24);
		(engine.getHUDmanager()).setHUD2font(GLUT.BITMAP_HELVETICA_18);

		// update inputs and camera
		im.update((float)elapsTime);

		orbitController.updateCameraPosition();

		processNetworking((float)elapsTime);
		
	}

	private void setHeldActionToKeyboard(net.java.games.input.Component.Identifier.Key key, IAction action)
	{
		im.associateActionWithAllKeyboards(
			key, action, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	}
	private void setPressedActionToKeyboard(net.java.games.input.Component.Identifier.Key key, IAction action)
	{
		im.associateActionWithAllKeyboards(
			key, action, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
	}
	private void setHeldButtonToGamepad(net.java.games.input.Component.Identifier button, IAction action)
	{
		im.associateActionWithAllGamepads(
			button, action, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	}
	private void setPressedButtonToGamepad(net.java.games.input.Component.Identifier button, IAction action)
	{
		im.associateActionWithAllGamepads(
			button, action, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
	}

	public GameObject getAvatar() { return avatar; }
	public float getElapsTime() { return (float)elapsTime; }
	public Engine getEngine() { return engine; }

	// ------------Networking-----------------------

	public ObjShape getGhostShape() { return ghostS; }
	public TextureImage getGhostTexture() { return ghostT; }
	public GhostManager getGhostManager() { return gm; }
	public ProtocolClient getProtClient() { return protClient; } 

	private void setupNetworking(){
		isClientConnected = false;
		try{
			protClient = new ProtocolClient(InetAddress.getByName(serverAddress), serverPort, serverProtocol, this);
		} catch (UnknownHostException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		if (protClient == null){
			System.out.println("missing protocol host");
		} else {
			System.out.println("sending join message to protocol host");
			protClient.sendJoinMessage();
		}
	}

	protected void processNetworking(float elapsTime){
		if (protClient != null){
			protClient.processPackets();
		}
	}

	public Vector3f getPlayerPosition() { return avatar.getWorldLocation(); }
	public void setIsConnected(boolean value) { this.isClientConnected = value; }

	private class SendCloseConnectionPacketAction extends AbstractInputAction{
		@Override
		public void performAction(float time, net.java.games.input.Event evt){
			if (protClient != null && isClientConnected == true){
				protClient.sendByeMessage();
			}
		}
	}
}