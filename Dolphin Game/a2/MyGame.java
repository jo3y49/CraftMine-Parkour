package a2;

import tage.*;
import tage.shapes.*;
import tage.input.*;
import tage.input.action.*;
import tage.nodeControllers.*;
import net.java.games.input.Component.Identifier.*;
import java.lang.Math;
import java.util.ArrayList;
import org.joml.*;
import com.jogamp.opengl.util.gl2.GLUT;
import a2.Commands.*;
import a2.Shapes.*;

public class MyGame extends VariableFrameRateGame
{
	private static Engine engine;
	private double lastFrameTime, currFrameTime;
	private double elapsTime;
	private InputManager im;
	private CameraOrbit3D orbitController;
	private NodeController rc, fc;
	private GameObject avatar, cub, cubM, tor, torM, sph, sphM, pyr, ground, x, y, z;
	private ObjShape dolS, cubS, torS, pyrS, sphS, groundS, linxS, linyS, linzS;
	private TextureImage doltx, cubePattern;
	private Light light1;
	private ArrayList<GameObject> prizes = new ArrayList<>();
	private ArrayList<GameObject> collectedPrizes = new ArrayList<>();


	public MyGame() { super(); }

	public static void main(String[] args)
	{	MyGame game = new MyGame();
		engine = new Engine(game);
		game.initializeSystem();
		game.game_loop();
	}

	@Override
	public void loadShapes()
	{	dolS = new ImportedModel("dolphinHighPoly.obj");
		cubS = new Cube();
		torS = new Torus(.5f, .2f, 48);
		pyrS = new ManualPyramid();
		sphS = new Sphere();
		groundS = new Plane();
		linxS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(5f, 0f, 0f));
		linyS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 5f, 0f));
		linzS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, -5f));
	}

	@Override
	public void loadTextures()
	{	
		doltx = new TextureImage("Dolphin_HighPolyUV.png");
		cubePattern = new TextureImage("Cube_Decoration.png");
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

		cub = new GameObject(GameObject.root(), cubS, cubePattern);
		initialTranslation = (new Matrix4f()).translation(20,1,-10);
		initialScale = (new Matrix4f()).scaling(.6f);
		cub.setLocalTranslation(initialTranslation);
		cub.setLocalScale(initialScale);
		prizes.add(cub);

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
		pyr = new GameObject(GameObject.root(), pyrS);
		initialTranslation = (new Matrix4f()).translation(0,2,0);
		pyr.setLocalTranslation(initialRotation);
		initialScale = (new Matrix4f()).scaling(2f);
		pyr.setLocalScale(initialScale);
		pyr.getRenderStates().hasLighting(true);

		cubM = new GameObject(GameObject.root(), cubS, cubePattern);
		initialTranslation = (new Matrix4f()).translation(.3f,2,.6f);
		cubM.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(.06f);
		cubM.setLocalScale(initialScale);
		cubM.getRenderStates().setColor(new Vector3f(1,1,1));
		cubM.setParent(pyr);
		cubM.propagateTranslation(true);
		cubM.propagateRotation(false);
		cubM.getRenderStates().disableRendering();

		sphM = new GameObject(GameObject.root(), sphS);
		initialTranslation = (new Matrix4f()).translation(-.6f,2,0);
		sphM.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(.07f);
		sphM.setLocalScale(initialScale);
		sphM.getRenderStates().setColor(new Vector3f(1,1,1));
		sphM.setParent(pyr);
		sphM.propagateTranslation(true);
		sphM.propagateRotation(false);
		sphM.getRenderStates().disableRendering();

		torM = new GameObject(GameObject.root(), torS);
		initialTranslation = (new Matrix4f()).translation(.3f,2,-.6f);
		torM.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(.1f);
		torM.setLocalScale(initialScale);
		torM.getRenderStates().setColor(new Vector3f(1,1,1));
		torM.setParent(pyr);
		torM.propagateTranslation(true);
		torM.propagateRotation(false);
		torM.getRenderStates().disableRendering();

		ground = new GameObject(GameObject.root(), groundS);
		initialTranslation = (new Matrix4f()).translation(0,0,0);
		ground.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(50f);
		ground.setLocalScale(initialScale);

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
		currFrameTime = System.currentTimeMillis();
		elapsTime = 0.0;
		(engine.getRenderSystem()).setWindowDimensions(1900,1000);

		rc = new RotationController(engine, new Vector3f(0,1,0), .001f);
		fc = new FlyController(engine, .0005f);

		(engine.getSceneGraph()).addNodeController(rc);
		(engine.getSceneGraph()).addNodeController(fc);

		rc.toggle();
		fc.toggle();

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
	}
	
	@Override
	public void update()
	{	
		lastFrameTime = currFrameTime;
		currFrameTime = System.currentTimeMillis();
		elapsTime += (currFrameTime - lastFrameTime) / 1000.0;

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

		checkPrizeCollision();

		double spinSpeed = 30;
		float spinDistance = 1;
		for (int i = 0; i < collectedPrizes.size(); i++)
			{
				activatePrize(collectedPrizes.get(i), spinSpeed, spinDistance);
				spinSpeed += 20;
				spinDistance += .5f;
			}

		orbitController.updateCameraPosition();
		
	}

	private void checkPrizeCollision()
	{
		for (int i = 0; i < prizes.size(); i++)
		{
			if (avatar.getWorldLocation().distance(prizes.get(i).getLocalLocation()) <= 3f)
			{
				rc.addTarget(prizes.get(i));
				fc.addTarget(prizes.get(i));
				GameObject mini = new GameObject(GameObject.root());

				if (prizes.get(i) == cub)
					mini = cubM;
				else if (prizes.get(i) == tor)
					mini = torM;
				else if (prizes.get(i) == sph)
					mini = sphM;

				collectedPrizes.add(mini);
				mini.getRenderStates().enableRendering();
				
				prizes.remove(i);
				if (prizes.size() == 0)
					rc.addTarget(pyr);
			}
		}
	}

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

	public float getFrameTime() { return (float)(currFrameTime - lastFrameTime); }
	
}