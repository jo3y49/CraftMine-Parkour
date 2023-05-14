package craftmine;

//tage imports
import tage.*;
import tage.Light.LightType;
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

import craftmine.Client.*;
import craftmine.Commands.*;
import craftmine.Shapes.*;

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
	private double lastFrameTime, currFrameTime, timePerFrame;

	private NodeController rc, fc;
	private CameraOrbit3D orbitController;
	private Light lightAmb;
	private boolean lightsOn = true;

	private GameObject avatar, tor, sph, ball1, ball2;
	private AnimatedShape avatarA, shadowS;
	private ObjShape ghostS, candS, torS,  sphS;
	private TextureImage dolT, ghostT, candT, shadowT;
	private TextureImage avatarTexs[] = new TextureImage[4];
	private int avatarIndex;

	private ArrayList<GameObject> prizes = new ArrayList<>();
	private ArrayList<GameObject> collectedPrizes = new ArrayList<>();
	private ArrayList<GameObject> platforms = new ArrayList<>();
	private ArrayList<PhysicsObject> platformsP = new ArrayList<>();
	private ArrayList<GameObject> candles = new ArrayList<>();
	private ArrayList<Light> lights = new ArrayList<>();
	
	private int avatarMoveSpeed = 50;
	
	// terrain/skybox variables
	private GameObject terr;
	private ObjShape terrS;
	private TextureImage hills, grass;
	private int spaceBox; // skyboxes

	//scripting variables
	private File scriptFile1;
	private long fileLastModifiedTime = 0;
	ScriptEngine jsEngine;

	//physics variables
	private PhysicsEngine physicsEngine;
	private PhysicsObject ball1P, ball2P, terrP, avatarP;
	private boolean running = true;
	private float vals[] = new float[16];

	//gameplay variables
	private boolean canAvatarJump = true;
	private boolean canAvatarWin = false;
	private int points = 0;


	//Platforms
	private ObjShape platS;
	private GameObject vicPlat1, vicPlat2, vicPlat3, vicPlat4;
	private PhysicsObject vicPlat1P, vicPlat2P, vicPlat3P, vicPlat4P;


	

	//server variables
	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private ProtocolClient protClient;
	private boolean isClientConnected = false;

	//audio variables
	private IAudioManager audioMgr;
	private Sound birdSound, landSound, jumpSound;


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
		ghostS = new AnimatedShape("Player.rkm", "Player.rks");
		candS = new ImportedModel("Candle.obj");
		shadowS = new AnimatedShape("Player.rkm", "Player.rks");
		torS = new Torus(.5f, .2f, 48);
		sphS = new Sphere();
		platS = new Cube();

		//terrain
		terrS = new TerrainPlane(1000); // pixels per axis = 1000x1000
	}

	@Override
	public void loadTextures()
	{	
		dolT = new TextureImage("Dolphin_HighPolyUV.png");
		ghostT = new TextureImage("Candle.png");
		candT = new TextureImage("Candle.png");
		shadowT = new TextureImage("AItexture.png");

		//Need to make hill/grass textures
		hills = new TextureImage("Hills.png");
		grass = new TextureImage("Grass.jpg");

		String[] avatars = {"avatarUVskin1.png", "avatarUVskin2.png", "avatarUVskin3.png", "Dolphin_HighPolyUV.png"};

		for (int i = 0; i < avatarTexs.length; i++){
			avatarTexs[i] = new TextureImage(avatars[i]);
		}
	}	 

	//skybox load
	@Override
	public void loadSkyBoxes()
	{ spaceBox = (engine.getSceneGraph()).loadCubeMap("space");
	
	
	(engine.getSceneGraph()).setActiveSkyBoxTexture(spaceBox);
	(engine.getSceneGraph()).setSkyBoxEnabled(true);
	}


	@Override
	public void buildObjects()
	{	Matrix4f initialTranslation, initialScale, initialRotation;

		// build dolphin in the center of the window
		avatar = new GameObject(GameObject.root(), avatarA, dolT);
		initialTranslation = (new Matrix4f()).translation(0,1,-10);
		initialScale = (new Matrix4f()).scaling(.5f);
		avatar.setLocalTranslation(initialTranslation);
		avatar.setLocalScale(initialScale);

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
		initialScale = (new Matrix4f()).scaling(40.0f, 1.0f, 40.0f);
		terr.setLocalScale(initialScale);
		terr.setHeightMap(hills);

		createPlatform(10, 2, 10);
		createPlatform(-10, 4, 16);
		createPlatform(7, 6, -13);
		createPlatform(18, 8, -9);
		createPlatform(-19, 10, 20);
		createPlatform(3, 12, 11);
		createPlatform(-17, 14, -7);
		createPlatform(-5, 16, 0);
		createPlatform(19, 18, -18);
		createPlatform(10, 20, 10);

		//victory platforms
		vicPlat1 = new GameObject(GameObject.root(), platS, grass);
		vicPlat1.setLocalTranslation((new Matrix4f()).translation(1.5f,24,1.5f));
		vicPlat1.setLocalScale(new Matrix4f().scaling(1.5f, 1.5f, 1.5f));

		vicPlat2 = new GameObject(GameObject.root(), platS, grass);
		vicPlat2.setLocalTranslation((new Matrix4f()).translation(-1.5f,24,1.5f));
		vicPlat2.setLocalScale(new Matrix4f().scaling(1.5f, 1.5f, 1.5f));

		vicPlat3 = new GameObject(GameObject.root(), platS, grass);
		vicPlat3.setLocalTranslation((new Matrix4f()).translation(1.5f,24,-1.5f));
		vicPlat3.setLocalScale(new Matrix4f().scaling(1.5f, 1.5f, 1.5f));

		vicPlat4 = new GameObject(GameObject.root(), platS, grass);
		vicPlat4.setLocalTranslation((new Matrix4f()).translation(-1.5f,24,-1.5f));
		vicPlat4.setLocalScale(new Matrix4f().scaling(1.5f, 1.5f, 1.5f));

	}
	private void createPlatform(float x, float y, float z) {
		GameObject platform = new GameObject(GameObject.root(), platS, grass);
		platform.setLocalTranslation((new Matrix4f()).translation(x,y,z));
		platform.setLocalScale(new Matrix4f().scaling(1.5f, 1.5f, 1.5f));
		GameObject candle = new GameObject(platform, candS, candT);
		platforms.add(platform);
		candles.add(candle);
	}

	@Override
	public void initializeLights()
	{	Light.setGlobalAmbient(0.5f, 0.5f, 0.5f);
		lightAmb = new Light();
		lightAmb.setLocation(new Vector3f(5.0f, 4.0f, 2.0f));
		(engine.getSceneGraph()).addLight(lightAmb);

		for (int i = 0; i < candles.size(); i++){
			Light light = new Light();
			light.setType(LightType.SPOTLIGHT);
			light.setLocation(candles.get(i).getWorldLocation().add(0,2,0));
			lights.add(light);
			(engine.getSceneGraph()).addLight(light);
		}
	}

	public void initAudio() {
		AudioResource resource1, resource2, resource3, resource4;
		audioMgr = AudioManagerFactory.createAudioManager(
		"tage.audio.joal.JOALAudioManager");
		if (!audioMgr.initialize())
		{ System.out.println("Audio Manager failed to initialize!");
		return;
		}
		resource1 = audioMgr.createAudioResource(
		"assets/sounds/birds.wav", AudioResourceType.AUDIO_SAMPLE);
		birdSound = new Sound(resource1,
		SoundType.SOUND_EFFECT, 20, true);
		birdSound.initialize(audioMgr);
		setEarParameters();
		birdSound.setMaxDistance(20.0f);
		birdSound.setMinDistance(0.5f);
		birdSound.setRollOff(10.0f);
		birdSound.play();


		//making jumping and landing sound
		resource3 = audioMgr.createAudioResource(
		"assets/sounds/jump.wav", AudioResourceType.AUDIO_SAMPLE);
		resource4 = audioMgr.createAudioResource(
		"assets/sounds/landing.wav", AudioResourceType.AUDIO_SAMPLE);
		jumpSound = new Sound(resource3,
		SoundType.SOUND_EFFECT, 50, true);
		landSound = new Sound(resource4,
		SoundType.SOUND_EFFECT, 50, true);
		jumpSound.initialize(audioMgr);
		landSound.initialize(audioMgr);
		jumpSound.setMaxDistance(10.0f);
		jumpSound.setMinDistance(0.5f);
		jumpSound.setRollOff(5.0f);
		landSound.setMaxDistance(10.0f);
		landSound.setMinDistance(0.5f);
		landSound.setRollOff(5.0f);
		jumpSound.setLocation(avatar.getWorldLocation());
		landSound.setLocation(avatar.getWorldLocation());
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

		Viewport leftVP = (engine.getRenderSystem()).getViewport("MAIN");
		Camera leftCamera = leftVP.getCamera();

		leftCamera.setLocation(new Vector3f(0,0,0));
		leftCamera.setU(new Vector3f(1,0,0));
		leftCamera.setV(new Vector3f(0,1,0));
		leftCamera.setN(new Vector3f(0,0,1));
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
		timePerFrame = 0;

		(engine.getRenderSystem()).setWindowDimensions(1900,1000);

		rc = new RotationController(engine, new Vector3f(0,1,0),((Double)(jsEngine.get("RotationControllerSpeed"))).floatValue());
		fc = new FlyController(engine, ((Double)(jsEngine.get("FlyControllerSpeed"))).floatValue());

		(engine.getSceneGraph()).addNodeController(rc);
		(engine.getSceneGraph()).addNodeController(fc);

		rc.toggle();
		fc.toggle();

		im = engine.getInputManager();

		Camera cM = (engine.getRenderSystem()).getViewport("MAIN").getCamera();

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

		float[] sizePlat = {3,3,3};

		translation = new Matrix4f(vicPlat1.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		vicPlat1P = physicsEngine.addBoxObject(physicsEngine.nextUID(), 0, tempTransform, sizePlat);
		vicPlat1.setPhysicsObject(vicPlat1P);

		translation = new Matrix4f(vicPlat2.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		vicPlat2P = physicsEngine.addBoxObject(physicsEngine.nextUID(), 0, tempTransform, sizePlat);
		vicPlat2.setPhysicsObject(vicPlat2P);


		translation = new Matrix4f(vicPlat3.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		vicPlat3P = physicsEngine.addBoxObject(physicsEngine.nextUID(), 0, tempTransform, sizePlat);
		vicPlat3.setPhysicsObject(vicPlat3P);


		translation = new Matrix4f(vicPlat4.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		vicPlat4P = physicsEngine.addBoxObject(physicsEngine.nextUID(), 0, tempTransform, sizePlat);
		vicPlat4.setPhysicsObject(vicPlat4P);




		for (int i = 0; i < platforms.size(); i++) {
			translation = new Matrix4f(platforms.get(i).getLocalTranslation());
			tempTransform = toDoubleArray(translation.get(vals));
			platformsP.add(physicsEngine.addBoxObject(physicsEngine.nextUID(), 0, tempTransform, sizePlat));
			platforms.get(i).setPhysicsObject(platformsP.get(i));
		}

		StraightMovementController moveController = new StraightMovementController(this, ((Double) jsEngine.get("straightMoveSpeedWeight")).floatValue());
		StraightMovement moveForward = new StraightMovement(this, true, ((Double) jsEngine.get("straightMoveSpeedWeight")).floatValue());
		StraightMovement moveBackward = new StraightMovement(this, false,  ((Double) jsEngine.get("straightMoveSpeedWeight")).floatValue());

		YawController YawController = new YawController(this, ((Double) jsEngine.get("YawMoveSpeedWeight")).floatValue());
		Yaw yawLeft = new Yaw(this, true, ((Double) jsEngine.get("YawMoveSpeedWeight")).floatValue());
		Yaw yawRight = new Yaw(this, false, ((Double) jsEngine.get("YawMoveSpeedWeight")).floatValue());

		Jump jump = new Jump(this, 1);
		Jump jumpDown = new Jump(this, -1);
		moveSpeed increaseSpeed = new moveSpeed(this, 1);
		moveSpeed decreaseSpeed = new moveSpeed(this, -1);

		ToggleLights toggleLight = new ToggleLights(this);

		Quit quit = new Quit(this);

		setHeldActionToKeyboard(Key.W, moveForward);
		setHeldActionToKeyboard(Key.S, moveBackward);
		setHeldActionToKeyboard(Key.A, yawLeft);
		setHeldActionToKeyboard(Key.D, yawRight);
		setHeldActionToKeyboard(Key.SPACE, jump);
		setHeldActionToKeyboard(Key.X, jumpDown);
		setPressedActionToKeyboard(Key.P, toggleLight);
		setPressedActionToKeyboard(Key.ESCAPE, quit);
		setPressedActionToKeyboard(Key.Q, increaseSpeed);
		setPressedActionToKeyboard(Key.E, decreaseSpeed);



		setHeldButtonToGamepad(Axis.Y, moveController);
		setHeldButtonToGamepad(Axis.X, YawController);
		setHeldButtonToGamepad(Button._0, jump);
		setHeldButtonToGamepad(Button._1, jumpDown);
		setPressedButtonToGamepad(Button._4, increaseSpeed);
		setPressedButtonToGamepad(Button._5, decreaseSpeed);
		setPressedButtonToGamepad(Button._6, toggleLight);




		initAudio();
		setupNetworking();
	}
	
	@Override
	public void update()
	{	
		lastFrameTime = currFrameTime;
		currFrameTime = System.currentTimeMillis();
		timePerFrame += (currFrameTime - lastFrameTime) / 1000.0;

		// avatar follows terrain map
		Vector3f loc = avatar.getWorldLocation();
		float height = terr.getHeight(loc.x() + 1, loc.z());


		avatar.setLocalLocation(loc.x(), height + 1, loc.z());
		



		// candle rotation
		for (int i = 0; i < candles.size(); i++) {
			candles.get(i).setLocalTranslation(candles.get(i).getLocalTranslation().translate((float)Math.sin(currFrameTime/1000) * .1f, 0.0f, (float)Math.cos(currFrameTime/1000) * .1f));
			lights.get(i).setLocation(candles.get(i).getWorldLocation());
			lights.get(i).setDirection(new Vector3f(candles.get(i).getWorldLocation().x(), -10, candles.get(i).getWorldLocation().z()));
		}



		// update physics
		if (running) {
			Matrix4f mat = new Matrix4f();
			Matrix4f mat2 = new Matrix4f().identity();
			checkForCollisions();
			physicsEngine.update((float)timePerFrame);
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
		String collectedStr = Integer.toString(points);
		String dispStr1 = "Total score = " + collectedStr;

		String dispStr2 =  "Current speed: " + String.valueOf(avatarMoveSpeed);

		Vector3f hudColor = new Vector3f(1,1,1);

		(engine.getHUDmanager()).setHUD1(dispStr1, hudColor, 15, 15);
		(engine.getHUDmanager()).setHUD2(dispStr2, hudColor, 250, 15);

		(engine.getHUDmanager()).setHUD1font(GLUT.BITMAP_TIMES_ROMAN_24);
		(engine.getHUDmanager()).setHUD2font(GLUT.BITMAP_HELVETICA_18);

		// update inputs and camera
		im.update((float)timePerFrame);

		orbitController.updateCameraPosition();

		// hereSound.setLocation(avatar.getWorldLocation());
		jumpSound.setLocation(avatar.getWorldLocation());
		landSound.setLocation(avatar.getWorldLocation());
		
		setEarParameters();
		avatarA.updateAnimation();
		processNetworking((float)timePerFrame);
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
					if (avatarP.getUID() == obj1.getUID() || avatarP.getUID() == obj2.getUID())
					{
						if (canAvatarJump == false && jumpSound.getIsPlaying() == false){
							landSound.play(20, false);
						}
						canAvatarJump = true;

						if (terrP.getUID() == obj1.getUID() || terrP.getUID() == obj2.getUID()){
							if (!canAvatarWin){
								canAvatarWin = true;
							}
						}						
						else if (vicPlat1P.getUID() == obj1.getUID() || vicPlat1P.getUID() == obj2.getUID()){
							if (canAvatarWin){
								points++;
								canAvatarWin = false;
							}
						}
						else if (vicPlat2P.getUID() == obj1.getUID() || vicPlat2P.getUID() == obj2.getUID()){
							if (canAvatarWin){
								points++;
								canAvatarWin = false;
							}
						}
						else if (vicPlat3P.getUID() == obj1.getUID() || vicPlat3P.getUID() == obj2.getUID()){
							if (canAvatarWin){
								points++;
								canAvatarWin = false;
							}
						}
						else if (vicPlat4P.getUID() == obj1.getUID() || vicPlat4P.getUID() == obj2.getUID()){
							if (canAvatarWin){
								points++;
								canAvatarWin = false;
							}
						}

					}




					break;
				} 
			} 
		} 
	}


	private void activatePrize(GameObject prize, double speed, float location)
	{
		Matrix4f currentTranslation = prize.getLocalTranslation();
		currentTranslation.translation((float)Math.sin(Math.toRadians(timePerFrame * speed)) * location,
			2f, (float)Math.cos(Math.toRadians(timePerFrame * speed)) * location);
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
	public void increaseAvatarMoveSpeed() { avatarMoveSpeed += 10;}
	public void decreaseAvatarMoveSpeed() { avatarMoveSpeed -= 10;}
	public void stopAvatarJump() {canAvatarJump = false;}
	public void avatarPhysics(float movement) { 


		Vector3f camPos = new Vector3f(orbitController.getCamPosition());//get cam pos
		Vector3f direction = new Vector3f(avatar.getLocalLocation());
		direction.sub(camPos);
		

		//System.out.println("movespeed:" + avatarMoveSpeed);
		avatarP.applyForce(direction.x*movement*avatarMoveSpeed, 0, direction.z*movement*avatarMoveSpeed, 0, 0, 0);
	}
	public void avatarJump(int direction) {
		if (canAvatarJump){
			jumpSound.play(20, false);
			avatarP.applyForce(0, 150 * direction, 0, 0, 0, 0);
		}
	}

	public void toggleLights() {
		if (lightsOn) {
			for (int i = 0; i < lights.size(); i++)
				engine.getSceneGraph().removeLight(lights.get(i));
		} else {
			for (int i = 0; i < lights.size(); i++)
				engine.getSceneGraph().addLight(lights.get(i));
		}
		lightsOn = !lightsOn;
	}

	// ------------Networking-----------------------

	public ObjShape getGhostShape() { return ghostS; }
	public TextureImage getGhostTexture() { return ghostT; }
	public GhostManager getGhostManager() { return gm; }
	public ProtocolClient getProtClient() { return protClient; } 
	public boolean getIsClientConnected() { return isClientConnected; }

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
		selectAvatar();
		if (protClient == null){
			System.out.println("missing protocol host");
		} else {
			System.out.println("sending join message to protocol host");
			protClient.sendJoinMessage();
			protClient.sendNeedNPCMessage();
		}
	}

	private void selectAvatar() {
		AvatarSelectionDialog asd = new AvatarSelectionDialog();
		asd.showIt();
		avatarIndex = asd.getSelectedAvatar();
		avatar.setTextureImage(avatarTexs[avatarIndex]);
	}

	protected void processNetworking(float elapsTime){
		if (protClient != null){
			protClient.processPackets();
		}
	}

	public Vector3f getPlayerPosition() { return avatar.getWorldLocation(); }
	public int getAvatarIndex() {return avatarIndex;}
	public TextureImage[] getAvatarTexts() {return avatarTexs;}
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