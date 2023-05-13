package a2;

import a2.Commands.*;
import a2.Shapes.*;
import a2.Client.*;

//tage imports
import tage.*;
import tage.audio.*;
import tage.shapes.*;
import tage.input.*;
import tage.input.action.*;
import tage.nodeControllers.*;
import tage.physics.*;
import tage.physics.JBullet.*;
import tage.networking.IGameConnection.ProtocolType;

import net.java.games.input.Component.Identifier.*;
import java.lang.Math;
import java.util.ArrayList;
import org.joml.*;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.jogamp.opengl.util.gl2.GLUT;

//scripting imports
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;

//networking imports
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MyGame extends VariableFrameRateGame
{
	private static Engine engine;
	private InputManager im;
	private GhostManager gm;
	private double lastFrameTime, currFrameTime, elapsTime;

	private NodeController rc, fc;
	private CameraOrbit3D orbitController;
	private Light lightAmb;

	private GameObject avatar, candle, shadow, cubM, tor, torM, sph, sphM, pyr,  x, y, z, ball1, ball2;
	private AnimatedShape avatarA, shadowS;
	private ObjShape ghostS, candS, torS, pyrS, sphS, linxS, linyS, linzS;
	private TextureImage dolT, ghostT, candT, shadowT;
	private String selectedAvatar;

	private ArrayList<GameObject> prizes = new ArrayList<>();
	private ArrayList<GameObject> collectedPrizes = new ArrayList<>();
	
	// terrain/skybox variables
	private GameObject terr;
	private ObjShape terrS;
	private TextureImage hills, grass;
	private int fluffyClouds, lakeIslands; // skyboxes

	//scripting variables
	private File scriptFile1;
	private long fileLastModifiedTime = 0;
	ScriptEngine jsEngine;

	//physics variables
	private PhysicsEngine physicsEngine;
	private PhysicsObject ball1P, ball2P, terrP, avatarP, terrHeightP;
	private boolean running = true;
	private float vals[] = new float[16];
	private boolean canAvatarJump = true;


	//Platforms
	private GameObject platform1, platform2, platform3, platform4, platform5, platform6, platform7, platform8, platform9, platform10;
	private ObjShape platS;
	private PhysicsObject plat1P, plat2P, plat3P, plat4P, plat5P, plat6P, plat7P, plat8P, plat9P, plat10P;
	//private TextureImage platform;

	

	//server variables
	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private ProtocolClient protClient;
	private boolean isClientConnected = false;

	//audio variables
	private IAudioManager audioMgr;
	private Sound oceanSound, hereSound;


	public MyGame(String serverAddress, int serverPort) { 
		super(); 
		gm = new GhostManager(this);
		this.serverAddress = serverAddress;
		this.serverPort = serverPort; 
		this.serverProtocol = ProtocolType.UDP;
	}

	public static void main(String[] args)
	{	MyGame game = new MyGame(args[0], Integer.parseInt(args[1]));
		engine = new Engine(game);
		game.initializeSystem();
		game.game_loop();
	}

	@Override
	public void loadShapes()
	{	avatarA = new AnimatedShape("Player.rkm", "Player.rks");
		avatarA.loadAnimation("walk", "Player.rka");
		ghostS = new ImportedModel("Candle.obj");
		candS = new ImportedModel("Candle.obj");
		shadowS = new AnimatedShape("Player.rkm", "Player.rks");
		torS = new Torus(.5f, .2f, 48);
		pyrS = new ManualPyramid();
		sphS = new Sphere();
		platS = new Cube();
		linxS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(5f, 0f, 0f));
		linyS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 5f, 0f));
		linzS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, -5f));

		//terrain
		terrS = new TerrainPlane(1000); // pixels per axis = 1000x1000
	}

	@Override
	public void loadTextures()
	{	
		dolT = new TextureImage("Dolphin_HighPolyUV.png");
		ghostT = new TextureImage("Candle.png");
		candT = new TextureImage("Candle.png");
		shadowT = new TextureImage("Cube_Decoration.png");

		//Need to make hill/grass textures
		hills = new TextureImage("Hills.png");
		grass = new TextureImage("Grass.jpg");
	}	 

	//skybox load
	@Override
	public void loadSkyBoxes()
	{ fluffyClouds = (engine.getSceneGraph()).loadCubeMap("fluffyClouds");
	lakeIslands = (engine.getSceneGraph()).loadCubeMap("lakeIslands");
	(engine.getSceneGraph()).setActiveSkyBoxTexture(fluffyClouds);
	(engine.getSceneGraph()).setSkyBoxEnabled(true);
	}


	@Override
	public void buildObjects()
	{	Matrix4f initialTranslation, initialScale, initialRotation;

		// build dolphin in the center of the window
		avatar = new GameObject(GameObject.root(), avatarA, dolT);
		initialTranslation = (new Matrix4f()).translation(0,1,-10);
		initialScale = (new Matrix4f()).scaling(.5f);
		// initialRotation = (new Matrix4f()).rotationY((float)java.lang.Math.toRadians(135f));
		avatar.setLocalTranslation(initialTranslation);
		avatar.setLocalScale(initialScale);
		// avatar.setLocalRotation(initialRotation);

		candle = new GameObject(GameObject.root(), candS, candT);
		initialTranslation = (new Matrix4f()).translation(5,.4f,0);
		initialScale = (new Matrix4f()).scaling(1f);
		candle.setLocalTranslation(initialTranslation);
		candle.setLocalScale(initialScale);
		
		// shadow = new GameObject(GameObject.root(), shadowS, shadowT);
		// initialTranslation = (new Matrix4f()).translation(20,1,-10);
		// initialScale = (new Matrix4f()).scaling(1f);
		// shadow.setLocalTranslation(initialTranslation);
		// shadow.setLocalScale(initialScale);
		// prizes.add(shadow);

		sph = new GameObject(GameObject.root(), sphS);
		initialTranslation = (new Matrix4f()).translation(-25,1,-5);
		initialScale = (new Matrix4f()).scaling(.7f);
		sph.setLocalTranslation(initialTranslation);
		sph.setLocalScale(initialScale);
		prizes.add(sph);

		tor = new GameObject(GameObject.root(), torS);
		initialTranslation = (new Matrix4f()).translation(11, 1, 10);
		tor.setLocalTranslation(initialTranslation);
		prizes.add(tor);

		//build pyramid
		// pyr = new GameObject(GameObject.root(), pyrS);
		// initialTranslation = (new Matrix4f()).translation(0,2,0);
		// pyr.setLocalTranslation(initialRotation);
		// initialScale = (new Matrix4f()).scaling(2f);
		// pyr.setLocalScale(initialScale);
		// pyr.getRenderStates().hasLighting(true);

		// cubM = new GameObject(GameObject.root(), shadowS, shadowT);
		// initialTranslation = (new Matrix4f()).translation(.3f,2,.6f);
		// cubM.setLocalTranslation(initialTranslation);
		// initialScale = (new Matrix4f()).scaling(.06f);
		// cubM.setLocalScale(initialScale);
		// cubM.getRenderStates().setColor(new Vector3f(1,1,1));
		// cubM.setParent(pyr);
		// cubM.propagateTranslation(true);
		// cubM.propagateRotation(false);
		// cubM.getRenderStates().disableRendering();

		// sphM = new GameObject(GameObject.root(), sphS);
		// initialTranslation = (new Matrix4f()).translation(-.6f,2,0);
		// sphM.setLocalTranslation(initialTranslation);
		// initialScale = (new Matrix4f()).scaling(.07f);
		// sphM.setLocalScale(initialScale);
		// sphM.getRenderStates().setColor(new Vector3f(1,1,1));
		// sphM.setParent(pyr);
		// sphM.propagateTranslation(true);
		// sphM.propagateRotation(false);
		// sphM.getRenderStates().disableRendering();

		// torM = new GameObject(GameObject.root(), torS);
		// initialTranslation = (new Matrix4f()).translation(.3f,2,-.6f);
		// torM.setLocalTranslation(initialTranslation);
		// initialScale = (new Matrix4f()).scaling(.1f);
		// torM.setLocalScale(initialScale);
		// torM.getRenderStates().setColor(new Vector3f(1,1,1));
		// torM.setParent(pyr);
		// torM.propagateTranslation(true);
		// torM.propagateRotation(false);
		// torM.getRenderStates().disableRendering();

		// ground = new GameObject(GameObject.root(), groundS);
		// initialTranslation = (new Matrix4f()).translation(0,0,0);
		// ground.setLocalTranslation(initialTranslation);
		// initialScale = (new Matrix4f()).scaling(50f);
		// ground.setLocalScale(initialScale);

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

		// -------------- adding two Spheres -----------------
		ball1 = new GameObject(GameObject.root(), sphS, candT);
		ball1.setLocalTranslation((new Matrix4f()).translation(0, 4, 0));
		ball1.setLocalScale((new Matrix4f()).scaling(0.75f));

		ball2 = new GameObject(GameObject.root(), sphS, candT);
		ball2.setLocalTranslation((new Matrix4f()).translation(-0.5f, 1, 0));
		ball2.setLocalScale((new Matrix4f()).scaling(0.75f));

		// build terrain object
		terr = new GameObject(GameObject.root(), terrS, grass);
		initialTranslation = (new Matrix4f()).translation(0f,0f,0f);
		terr.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(20.0f, 1.0f, 20.0f);
		terr.setLocalScale(initialScale);
		terr.setHeightMap(hills);

		//building platforms
		platform1 = new GameObject(GameObject.root(), platS, grass);
		platform1.setLocalTranslation((new Matrix4f()).translation(10, 2, 10));
		platform1.setLocalScale((new Matrix4f()).scaling(1));

		platform2 = new GameObject(GameObject.root(), platS, grass);
		platform2.setLocalTranslation((new Matrix4f()).translation(-10, 4, 16));
		platform2.setLocalScale((new Matrix4f()).scaling(1));

		platform3 = new GameObject(GameObject.root(), platS, grass);
		platform3.setLocalTranslation((new Matrix4f()).translation(7, 6, -13));
		platform3.setLocalScale((new Matrix4f()).scaling(1));

		platform4 = new GameObject(GameObject.root(), platS, grass);
		platform4.setLocalTranslation((new Matrix4f()).translation(18, 8, -9));
		platform4.setLocalScale((new Matrix4f()).scaling(1));

		platform5 = new GameObject(GameObject.root(), platS, grass);
		platform5.setLocalTranslation((new Matrix4f()).translation(-19, 10, 20));
		platform5.setLocalScale((new Matrix4f()).scaling(1));

		platform6 = new GameObject(GameObject.root(), platS, grass);
		platform6.setLocalTranslation((new Matrix4f()).translation(3, 12, 11));
		platform6.setLocalScale((new Matrix4f()).scaling(1));

		platform7 = new GameObject(GameObject.root(), platS, grass);
		platform7.setLocalTranslation((new Matrix4f()).translation(-17, 14, -7));
		platform7.setLocalScale((new Matrix4f()).scaling(1));

		platform8 = new GameObject(GameObject.root(), platS, grass);
		platform8.setLocalTranslation((new Matrix4f()).translation(-5, 16, 0));
		platform8.setLocalScale((new Matrix4f()).scaling(1));

		platform9 = new GameObject(GameObject.root(), platS, grass);
		platform9.setLocalTranslation((new Matrix4f()).translation(19, 18, -18));
		platform9.setLocalScale((new Matrix4f()).scaling(1));

		platform10 = new GameObject(GameObject.root(), platS, grass);
		platform10.setLocalTranslation((new Matrix4f()).translation(10, 20, 10));
		platform10.setLocalScale((new Matrix4f()).scaling(1));
	}

	@Override
	public void initializeLights()
	{	Light.setGlobalAmbient(0.5f, 0.5f, 0.5f);
		lightAmb = new Light();
		lightAmb.setLocation(new Vector3f(5.0f, 4.0f, 2.0f));
		(engine.getSceneGraph()).addLight(lightAmb);
	}

	public void initAudio() {
		AudioResource resource1, resource2;
		audioMgr = AudioManagerFactory.createAudioManager(
		"tage.audio.joal.JOALAudioManager");
		if (!audioMgr.initialize())
		{ System.out.println("Audio Manager failed to initialize!");
		return;
		}
		resource1 = audioMgr.createAudioResource(
		"assets/sounds/rushing water.wav", AudioResourceType.AUDIO_SAMPLE);
		resource2 = audioMgr.createAudioResource(
		"assets/sounds/rushing water.wav", AudioResourceType.AUDIO_SAMPLE);
		hereSound = new Sound(resource1,
		SoundType.SOUND_EFFECT, 100, true);
		oceanSound = new Sound(resource2,
		SoundType.SOUND_EFFECT, 500, true);
		hereSound.initialize(audioMgr);
		oceanSound.initialize(audioMgr);
		setEarParameters();
		hereSound.setMaxDistance(10.0f);
		hereSound.setMinDistance(0.5f);
		hereSound.setRollOff(5.0f);
		oceanSound.setMaxDistance(20.0f);
		oceanSound.setMinDistance(0.5f);
		oceanSound.setRollOff(10.0f);
		hereSound.setLocation(avatar.getWorldLocation());
		oceanSound.play();
		// hereSound.play();
		
	}

	public void setEarParameters() {
		Camera camera = (engine.getRenderSystem()).getViewport("MAIN").getCamera();
		audioMgr.getEar().setLocation(avatar.getWorldLocation());
		audioMgr.getEar().setOrientation(camera.getN(),
			new Vector3f(0.0f, 1.0f, 0.0f));
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

	//run script method
	private void runScript(File scriptFile)
	{ try
	{ FileReader fileReader = new FileReader(scriptFile);
	jsEngine.eval(fileReader);
	fileReader.close();
	}
	catch (FileNotFoundException e1)
	{ System.out.println(scriptFile + " not found " + e1); }
	catch (IOException e2)
	{ System.out.println("IO problem with " + scriptFile + e2); }
	catch (ScriptException e3)
	{ System.out.println("ScriptException in " + scriptFile + e3); }
	catch (NullPointerException e4)
	{ System.out.println ("Null ptr exception reading " + scriptFile + e4);
	} }

	
	@Override
	public void initializeGame()
	{
		// initialize the scripting engine
		ScriptEngineManager factory = new ScriptEngineManager();
		jsEngine = factory.getEngineByName("js");

		//initilizing parameters using this script
		scriptFile1 = new File("assets/scripts/InitParams.js");
		this.runScript(scriptFile1);

		lastFrameTime = currFrameTime = System.currentTimeMillis();
		elapsTime = 0;

		(engine.getRenderSystem()).setWindowDimensions(1900,1000);

		rc = new RotationController(engine, new Vector3f(0,1,0),((Double)(jsEngine.get("RotationControllerSpeed"))).floatValue());
		fc = new FlyController(engine, ((Double)(jsEngine.get("FlyControllerSpeed"))).floatValue());

		(engine.getSceneGraph()).addNodeController(rc);
		(engine.getSceneGraph()).addNodeController(fc);

		rc.toggle();
		fc.toggle();

		im = engine.getInputManager();

		Camera cM = (engine.getRenderSystem()).getViewport("MAIN").getCamera();
		Camera cS = (engine.getRenderSystem()).getViewport("SMALL").getCamera();

		orbitController = new CameraOrbit3D(cM, avatar, terr, engine);

		// --- initialize physics system ---
		String engine = "tage.physics.JBullet.JBulletPhysicsEngine";
		float[] gravity = {0f, -5f, 0f};
		physicsEngine = PhysicsEngineFactory.createPhysicsEngine(engine);
		physicsEngine.initSystem();
		physicsEngine.setGravity(gravity);

		// --- create physics world ---
		float mass = 1.0f;
		float up[ ] = {0,1,0};
		double[ ] tempTransform;

		Matrix4f translation = new Matrix4f(ball1.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		ball1P = physicsEngine.addSphereObject(physicsEngine.nextUID(),
		mass, tempTransform, 0.75f);
		ball1P.setBounciness(1.0f);
		ball1.setPhysicsObject(ball1P);

		translation = new Matrix4f(ball2.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		ball2P = physicsEngine.addSphereObject(physicsEngine.nextUID(),
		mass, tempTransform, 0.75f);
		ball2P.setBounciness(1.0f);
		ball2.setPhysicsObject(ball2P);

		translation = new Matrix4f(terr.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		terrP = physicsEngine.addStaticPlaneObject(
		physicsEngine.nextUID(), tempTransform, up, 0.0f);
		terrP.setBounciness(0.0f);
		terr.setPhysicsObject(terrP);

		translation = new Matrix4f(avatar.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		float[] size = {1,2,1};
		avatarP = physicsEngine.addBoxObject(physicsEngine.nextUID(), 
		mass, tempTransform, size);
		avatar.setPhysicsObject(avatarP); 
		//makes it so avatar doesn't disable for being idle too long
		avatarP.getRigidBody().setActivationState(CollisionObject.DISABLE_DEACTIVATION);


		//creating platforms
		translation = new Matrix4f(platform1.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		float[] sizePlat = {2,2,2};
		plat1P = physicsEngine.addBoxObject(physicsEngine.nextUID(), 0, tempTransform, sizePlat);
		platform1.setPhysicsObject(plat1P);

		translation = new Matrix4f(platform2.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		plat2P = physicsEngine.addBoxObject(physicsEngine.nextUID(), 0, tempTransform, sizePlat);
		platform2.setPhysicsObject(plat2P);

		translation = new Matrix4f(platform3.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		plat3P = physicsEngine.addBoxObject(physicsEngine.nextUID(), 0, tempTransform, sizePlat);
		platform3.setPhysicsObject(plat3P);

		translation = new Matrix4f(platform4.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		plat4P = physicsEngine.addBoxObject(physicsEngine.nextUID(), 0, tempTransform, sizePlat);
		platform4.setPhysicsObject(plat4P);

		translation = new Matrix4f(platform5.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		plat5P = physicsEngine.addBoxObject(physicsEngine.nextUID(), 0, tempTransform, sizePlat);
		platform5.setPhysicsObject(plat5P);

		translation = new Matrix4f(platform6.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		plat6P = physicsEngine.addBoxObject(physicsEngine.nextUID(), 0, tempTransform, sizePlat);
		platform6.setPhysicsObject(plat6P);

		translation = new Matrix4f(platform7.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		plat7P = physicsEngine.addBoxObject(physicsEngine.nextUID(), 0, tempTransform, sizePlat);
		platform7.setPhysicsObject(plat7P);

		translation = new Matrix4f(platform8.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		plat8P = physicsEngine.addBoxObject(physicsEngine.nextUID(), 0, tempTransform, sizePlat);
		platform8.setPhysicsObject(plat8P);

		translation = new Matrix4f(platform9.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		plat9P = physicsEngine.addBoxObject(physicsEngine.nextUID(), 0, tempTransform, sizePlat);
		platform9.setPhysicsObject(plat9P);

		translation = new Matrix4f(platform10.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		plat10P = physicsEngine.addBoxObject(physicsEngine.nextUID(), 0, tempTransform, sizePlat);
		platform10.setPhysicsObject(plat10P);



		

		StraightMovementController moveController = new StraightMovementController(this, ((Double) jsEngine.get("straightMoveSpeedWeight")).floatValue());
		StraightMovement moveForward = new StraightMovement(this, true, ((Double) jsEngine.get("straightMoveSpeedWeight")).floatValue());
		StraightMovement moveBackward = new StraightMovement(this, false,  ((Double) jsEngine.get("straightMoveSpeedWeight")).floatValue());

		YawController YawController = new YawController(this, ((Double) jsEngine.get("YawMoveSpeedWeight")).floatValue());
		Yaw yawLeft = new Yaw(this, true, ((Double) jsEngine.get("YawMoveSpeedWeight")).floatValue());
		Yaw yawRight = new Yaw(this, false, ((Double) jsEngine.get("YawMoveSpeedWeight")).floatValue());

		CameraMovement moveCamIn = new CameraMovement(cS, this, "in");
		CameraMovement moveCamOut = new CameraMovement(cS, this, "out");
		CameraMovement moveCamUp = new CameraMovement(cS, this, "up");
		CameraMovement moveCamDown = new CameraMovement(cS, this, "down");
		CameraMovement moveCamLeft = new CameraMovement(cS, this, "left");
		CameraMovement moveCamRight = new CameraMovement(cS, this, "right");

		Jump jump = new Jump(this, 1);
		Jump jumpDown = new Jump(this, -1);

		Quit quit = new Quit(this);

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
		setHeldActionToKeyboard(Key.SPACE, jump);
		setHeldActionToKeyboard(Key.X, jumpDown);
		setPressedActionToKeyboard(Key.ESCAPE, quit);

		initAudio();
		setupNetworking();
	}
	
	@Override
	public void update()
	{	
		lastFrameTime = currFrameTime;
		currFrameTime = System.currentTimeMillis();
		elapsTime += (currFrameTime - lastFrameTime) / 1000.0;

		// avatar follows terrain map
		Vector3f loc = avatar.getWorldLocation();
		float height = terr.getHeight(loc.x() + 1, loc.z());


		avatar.setLocalLocation(loc.x(), height + 1, loc.z());
		System.out.println(height);







		// update physics
		if (running) {
			Matrix4f mat = new Matrix4f();
			Matrix4f mat2 = new Matrix4f().identity();
			checkForCollisions();
			physicsEngine.update((float)elapsTime);
			for (GameObject go:engine.getSceneGraph().getGameObjects()) { 
				if (go.getPhysicsObject() != null) {
					mat.set(toFloatArray(go.getPhysicsObject().getTransform()));
					mat2.set(3,0,mat.m30());
					mat2.set(3,1,mat.m31());
					mat2.set(3,2,mat.m32());
						go.setLocalTranslation(mat2);
				}
			} 	
		}

		// build and set HUD
		String collectedStr = Integer.toString(collectedPrizes.size());
		String dispStr1 = "Collected Prizes = " + collectedStr;

		String dispStr2 = avatar.getWorldLocation().toString();

		Vector3f hudColor = new Vector3f(1,1,1);

		(engine.getHUDmanager()).setHUD1(dispStr1, hudColor, 15, 15);
		(engine.getHUDmanager()).setHUD2(dispStr2, hudColor, 1500, 15);

		(engine.getHUDmanager()).setHUD1font(GLUT.BITMAP_TIMES_ROMAN_24);
		(engine.getHUDmanager()).setHUD2font(GLUT.BITMAP_HELVETICA_18);

		// update inputs and camera
		im.update((float)elapsTime);

		//checkPrizeCollision();

		// double spinSpeed = 30;
		// float spinDistance = 1;

		//double spinSpeed = (Double) (jsEngine.get("spinSpeed"));
		//float spinDistance = ((Double) jsEngine.get("spinDistance")).floatValue();

		// for (int i = 0; i < collectedPrizes.size(); i++)
		// 	{
		// 		activatePrize(collectedPrizes.get(i), spinSpeed, spinDistance);
		// 		spinSpeed += 20;
		// 		spinDistance += .5f;
		// 	}

		orbitController.updateCameraPosition();

		// hereSound.setLocation(avatar.getWorldLocation());
		try {
			oceanSound.setLocation(gm.getGhostNPC(0).getWorldLocation());
		} catch (Exception e){}
		
		setEarParameters();

		avatarA.updateAnimation();

		processNetworking((float)elapsTime);
		
	}

	private void checkForCollisions() {
		com.bulletphysics.dynamics.DynamicsWorld dynamicsWorld;
		com.bulletphysics.collision.broadphase.Dispatcher dispatcher;
		com.bulletphysics.collision.narrowphase.PersistentManifold manifold;
		com.bulletphysics.dynamics.RigidBody object1, object2;
		com.bulletphysics.collision.narrowphase.ManifoldPoint contactPoint;

		dynamicsWorld = ((JBulletPhysicsEngine)physicsEngine).getDynamicsWorld();
		dispatcher = dynamicsWorld.getDispatcher();
		int manifoldCount = dispatcher.getNumManifolds();
		for (int i=0; i<manifoldCount; i++) {
			manifold = dispatcher.getManifoldByIndexInternal(i);
			object1 = (com.bulletphysics.dynamics.RigidBody)manifold.getBody0();
			object2 = (com.bulletphysics.dynamics.RigidBody)manifold.getBody1();
			JBulletPhysicsObject obj1 = JBulletPhysicsObject.getJBulletPhysicsObject(object1);
			JBulletPhysicsObject obj2 = JBulletPhysicsObject.getJBulletPhysicsObject(object2);
			for (int j = 0; j < manifold.getNumContacts(); j++) {
				contactPoint = manifold.getContactPoint(j);
				if (contactPoint.getDistance() < 0.0f) {
					// System.out.println("---- hit between " + obj1 + " and " + obj2);

					// if collison between avatar and ground
					if (avatarP.getUID() == obj1.getUID() || avatarP.getUID() == obj2.getUID())
					{
						canAvatarJump = true;
					}
					// isphysicsobject = false
					break;
				} 
			} 
		} 
	}

	// private void checkPrizeCollision()
	// {
	// 	for (int i = 0; i < prizes.size(); i++)
	// 	{
	// 		if (avatar.getWorldLocation().distance(prizes.get(i).getLocalLocation()) <= 3f)
	// 		{
	// 			rc.addTarget(prizes.get(i));
	// 			fc.addTarget(prizes.get(i));
	// 			GameObject mini = new GameObject(GameObject.root());

	// 			if (prizes.get(i) == shadow)
	// 				mini = cubM;
	// 			else if (prizes.get(i) == tor)
	// 				mini = torM;
	// 			else if (prizes.get(i) == sph)
	// 				mini = sphM;

	// 			collectedPrizes.add(mini);
	// 			mini.getRenderStates().enableRendering();
				
	// 			prizes.remove(i);
	// 			if (prizes.size() == 0)
	// 				rc.addTarget(pyr);
	// 		}
	// 	}
	// }

	private void activatePrize(GameObject prize, double speed, float location)
	{
		Matrix4f currentTranslation = prize.getLocalTranslation();
		currentTranslation.translation((float)Math.sin(Math.toRadians(elapsTime * speed)) * location,
			2f, (float)Math.cos(Math.toRadians(elapsTime * speed)) * location);
		prize.setLocalTranslation(currentTranslation);
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
	private void setPressedandReleasedActiontoKeyboard(net.java.games.input.Component.Identifier.Key key, IAction action) {
		im.associateActionWithAllKeyboards(key, action, InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
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

	public Engine getEngine() { return engine; }
	public GameObject getAvatar() { return avatar; }
	public float getFrameTime() { return (float)(currFrameTime - lastFrameTime); }
	public ObjShape getNPCShape() { return shadowS; }
	public TextureImage getNPCTexture() { return shadowT; }
	public void avatarPhysics(float movement) { 


		int forceAmt = 100;
		Vector3f camPos = new Vector3f(orbitController.getCamPosition());//get cam pos
		Vector3f direction = new Vector3f(avatar.getLocalLocation());
		direction.sub(camPos);
		
		avatarP.applyForce(direction.x*movement*forceAmt, 0, direction.z*movement*forceAmt, 0, 0, 0);
	}
	public void avatarJump(int direction) {
		if (canAvatarJump){
			avatarP.applyForce(0, 150 * direction, 0, 0, 0, 0);
		}
	}

	// ------------Networking-----------------------

	public ObjShape getGhostShape() { return ghostS; }
	public TextureImage getGhostTexture() { return ghostT; }
	public GhostManager getGhostManager() { return gm; }
	public ProtocolClient getProtClient() { return protClient; } 
	public boolean getIsClientConnected() { return isClientConnected; }
	public void stopAvatarJump() {canAvatarJump = false;}

	public void handleAvatarAnimation(String a) { 
		if (avatarA.getCurAnimation() == null)
		avatarA.playAnimation(a, .5f, AnimatedShape.EndType.LOOP, 0); 
	}

	private void setupNetworking(){
		isClientConnected = false;
		try{
			protClient = new ProtocolClient(InetAddress.getByName(serverAddress), serverPort, serverProtocol, this);
		} catch (UnknownHostException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		AvatarSelectionDialog asd = new AvatarSelectionDialog();
		asd.showIt();
		selectedAvatar = asd.getSelectedAvatar();
		if (protClient == null){
			System.out.println("missing protocol host");
		} else {
			System.out.println("sending join message to protocol host");
			protClient.sendJoinMessage();
			protClient.sendNeedNPCMessage();
		}
	}

	protected void processNetworking(float elapsTime){
		if (protClient != null){
			protClient.processPackets();
		}
	}

	public Vector3f getPlayerPosition() { return avatar.getWorldLocation(); }
	public void setIsConnected(boolean value) { this.isClientConnected = value; }

	public void killGame(){
		if (protClient != null && isClientConnected){
            protClient.sendByeMessage();
        }
		shutdown();
		System.exit(0);
	}

	// ------------------ UTILITY FUNCTIONS used by physics
	private float[] toFloatArray(double[] arr) {
		if (arr == null) return null;
		int n = arr.length;
		float[] ret = new float[n];
		for (int i = 0; i < n; i++) {
			ret[i] = (float)arr[i];
		}
		return ret;
	}
	private double[] toDoubleArray(float[] arr) {
		if (arr == null) return null;
		int n = arr.length;
		double[] ret = new double[n];
		for (int i = 0; i < n; i++) {
			ret[i] = (double)arr[i];
		}
		return ret;
	}
}