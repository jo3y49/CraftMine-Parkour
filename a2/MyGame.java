package a2;

import tage.*;
import tage.shapes.*;
import tage.input.*;
import tage.nodeControllers.RotationController;
import tage.nodeControllers.ShrinkController;
import org.joml.*;

public class MyGame extends VariableFrameRateGame
{
	private static Engine engine;

	private int score = 0, storedPrizes = 0;
	private double lastFrameTime, currFrameTime, elapsTime;

	private ObjShape dolS, cubS;
	private TextureImage doltx, brick, roof, prize, grass;
	private Light light1;

	//code02b
	private InputManager im;
	private GameObject prize1, prize2, prize3, tor, avatar, x, y, z, house, planeObj;
	private ObjShape torS, linxS, linyS, linzS;

	private TurnInPrizes turnInPrizes;
	private Camera leftCamera; 
	private Camera rightCamera;

	//manual object
	private GameObject pyr;
	private ObjShape pyrS;

	//code04b
	private CameraOrbitController orbitController; 
	private CameraOverviewController overviewController;

	//plane
	private Plane plane;
	private ToggleLines toggleLines;

	//node controllers
	private NodeController rc1, rc2, rc3;
	private NodeController sc1, sc2, sc3, scH;


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
		torS = new Torus(0.5f, 0.2f, 48); 
  		linxS = new Line(new Vector3f(0f,0f,0f), new Vector3f(3f,0f,0f)); 
  		linyS = new Line(new Vector3f(0f,0f,0f), new Vector3f(0f,3f,0f)); 
  		linzS = new Line(new Vector3f(0f,0f,0f), new Vector3f(0f,0f,-3f)); 
		pyrS = new ManualPyramid();
		plane = new Plane();
	}

	@Override
	public void loadTextures()
	{	doltx = new TextureImage("Dolphin_HighPolyUV.png");
		brick = new TextureImage("brick1.jpg"); 
		prize = new TextureImage("prize.png");
		roof = new TextureImage("roof.png");
		grass = new TextureImage("terribleGrass.png");
	}

	@Override
	public void buildObjects()
	{	Matrix4f initialTranslation, initialScale;

		// build dolphin in the center of the window
		avatar = new GameObject(GameObject.root(), dolS, doltx);
		initialTranslation = (new Matrix4f()).translation(-1f,1f,1f);
		initialScale = (new Matrix4f()).scaling(3.0f);
		avatar.setLocalTranslation(initialTranslation);
		avatar.setLocalScale(initialScale);

		//build prize1
		prize1 = new GameObject(GameObject.root(), cubS, prize); 
 		initialTranslation = (new Matrix4f()).translation(5f,0.5f,-2.5f); 
  		initialScale = (new Matrix4f()).scaling(0.5f); 
		prize1.setLocalTranslation(initialTranslation); 
		prize1.setLocalScale(initialScale);

		//build prize2
		prize2 = new GameObject(GameObject.root(), cubS, prize); 
		initialTranslation = (new Matrix4f()).translation(-7f,1f,-6f); 
		initialScale = (new Matrix4f()).scaling(1f); 
		prize2.setLocalTranslation(initialTranslation); 
		prize2.setLocalScale(initialScale);

		//build prize3
		prize3 = new GameObject(GameObject.root(), cubS, prize); 
		initialTranslation = (new Matrix4f()).translation(-5f,0.25f,2.5f); 
		initialScale = (new Matrix4f()).scaling(0.25f); 
		prize3.setLocalTranslation(initialTranslation); 
		prize3.setLocalScale(initialScale);

		// build torus along X axis 
		tor = new GameObject(GameObject.root(), torS); 
		initialTranslation = (new Matrix4f()).translation(1,0.25f,0); 
		tor.setLocalTranslation(initialTranslation); 
		initialScale = (new Matrix4f()).scaling(0.25f); 
		tor.setLocalScale(initialScale); 
		// add X,Y,-Z axes 
		x = new GameObject(GameObject.root(), linxS); 
		y = new GameObject(GameObject.root(), linyS); 
		z = new GameObject(GameObject.root(), linzS); 
		(x.getRenderStates()).setColor(new Vector3f(1f,0f,0f)); 
		(y.getRenderStates()).setColor(new Vector3f(0f,1f,0f)); 
		(z.getRenderStates()).setColor(new Vector3f(0f,0f,1f)); 

		//manual objects
		house = new GameObject(GameObject.root(), cubS, brick); 
		initialTranslation = (new Matrix4f()).translation(0f,1f,7f); 
		initialScale = (new Matrix4f()).scaling(1f); 
		house.setLocalTranslation(initialTranslation); 
		house.setLocalScale(initialScale);

		pyr = new GameObject(GameObject.root(), pyrS, roof);
		initialTranslation = (new Matrix4f()).translation(0f,2f,0f);
		pyr.setLocalTranslation(initialTranslation);
		pyr.getRenderStates().hasLighting(true);
		pyr.setParent(house);
		pyr.propagateTranslation(true);
		pyr.propagateRotation(true);
		pyr.propagateScale(true);
		pyr.applyParentRotationToPosition(true);

		//builds plane
		planeObj = new GameObject(GameObject.root(), plane, grass);
		initialTranslation = (new Matrix4f()).translation(0f,0f,0f); 
		initialScale = (new Matrix4f()).scaling(200f); 
		planeObj.setLocalTranslation(initialTranslation); 
		planeObj.setLocalScale(initialScale);
	}

	@Override
	public void initializeLights()
	{	Light.setGlobalAmbient(0.5f, 0.5f, 0.5f);
		light1 = new Light();
		light1.setLocation(new Vector3f(5.0f, 4.0f, 2.0f));
		(engine.getSceneGraph()).addLight(light1);
	}

	
	public void createViewports() 
 	{ 
		(engine.getRenderSystem()).addViewport("LEFT",0,0,1f,1f); 
  		(engine.getRenderSystem()).addViewport("RIGHT",.75f,0,.25f,.25f); 
 
  		Viewport leftVp = (engine.getRenderSystem()).getViewport("LEFT"); 
  		Viewport rightVp = (engine.getRenderSystem()).getViewport("RIGHT"); 
 		leftCamera = leftVp.getCamera(); 
 		rightCamera = rightVp.getCamera(); 
 
  		rightVp.setHasBorder(true); 
  		rightVp.setBorderWidth(4); 
  		rightVp.setBorderColor(0.0f, 1.0f, 0.0f); 
 
  		leftCamera.setLocation(new Vector3f(-2,0,2)); 
  		leftCamera.setU(new Vector3f(1,0,0)); 
 		leftCamera.setV(new Vector3f(0,1,0)); 
 		leftCamera.setN(new Vector3f(0,0,-1)); 
 
 	 	rightCamera.setLocation(new Vector3f(0,2,0)); 
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

		im = engine.getInputManager(); 
		// ------------- positioning the camera -------------
		createViewports();

		String gpName = im.getFirstGamepadName(); 
		Camera c = (engine.getRenderSystem()).getViewport("LEFT").getCamera();
	   	orbitController = new CameraOrbitController(c, avatar, gpName, engine); 
		overviewController = new CameraOverviewController(rightCamera, avatar, gpName, engine); 

		//------------------- node controllers -----------------------
		
		rc1 = new RotationController(engine, new Vector3f(0,1,0), 0.001f);
		rc2 = new RotationController(engine, new Vector3f(0,1,0), 0.001f);
		rc3 = new RotationController(engine, new Vector3f(0,1,0), 0.001f);
		rc1.addTarget(prize1);
		rc2.addTarget(prize2);
		rc3.addTarget(prize3);
		engine.getSceneGraph().addNodeController(rc1);
		engine.getSceneGraph().addNodeController(rc2);
		engine.getSceneGraph().addNodeController(rc3);

		sc1 = new ShrinkController(engine, 0.5f);
		sc2 = new ShrinkController(engine, 0.5f);
		sc3 = new ShrinkController(engine, 0.5f);
		sc1.addTarget(prize1);
		sc2.addTarget(prize2);
		sc3.addTarget(prize3);
		engine.getSceneGraph().addNodeController(sc1);
		engine.getSceneGraph().addNodeController(sc2);
		engine.getSceneGraph().addNodeController(sc3);


		scH = new ShrinkController(engine, 1.3f);
		//scH.addTarget(pyr);
		scH.addTarget(house);
		engine.getSceneGraph().addNodeController(scH);


		// ----------------- INPUTS SECTION ----------------------------- 


		FwdAction fwdAction = new FwdAction(this); 
		BwdAction bwdAction = new BwdAction(this); 
		TurnAction turnAction = new TurnAction(this);
		Nmove nMove = new Nmove(this);
		TurnRightAction turnRightAction = new TurnRightAction(this);
		TurnLeftAction turnLeftAction = new TurnLeftAction(this);
		turnInPrizes = new TurnInPrizes(this);
		toggleLines = new ToggleLines(this);
		


		im.associateActionWithAllGamepads( 
		 net.java.games.input.Component.Identifier.Axis.Y, nMove, 
		 InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
		im.associateActionWithAllGamepads( 
		 net.java.games.input.Component.Identifier.Axis.X, turnAction, 
		 InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
		im.associateActionWithAllGamepads( 
		 net.java.games.input.Component.Identifier.Button._3, turnInPrizes, 
		 InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
		im.associateActionWithAllGamepads( 
		 net.java.games.input.Component.Identifier.Button._0, toggleLines, 
		 InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);


		im.associateActionWithAllKeyboards( 
		 net.java.games.input.Component.Identifier.Key.W, fwdAction, 
		 InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
		 im.associateActionWithAllKeyboards( 
		 net.java.games.input.Component.Identifier.Key.D, turnRightAction, 
		 InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
		im.associateActionWithAllKeyboards( 
		 net.java.games.input.Component.Identifier.Key.A, turnLeftAction, 
		 InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
		im.associateActionWithAllKeyboards( 
		 net.java.games.input.Component.Identifier.Key.S, bwdAction, 
		 InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
		im.associateActionWithAllKeyboards( 
			net.java.games.input.Component.Identifier.Key.C, turnInPrizes, 
			InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE); 
		im.associateActionWithAllKeyboards( 
			net.java.games.input.Component.Identifier.Key.V, toggleLines, 
			InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY); 
	}

	@Override
	public void update()
	{	
		lastFrameTime = currFrameTime; 
		currFrameTime = System.currentTimeMillis(); 
		elapsTime += (currFrameTime - lastFrameTime) / 1000.0; 

		// build and set HUD
		String storedPrizesStr = Integer.toString(storedPrizes);
		String scoreStr = Integer.toString(score);
		String dispStr1 = "X = " + avatar.getLocalLocation().x() + "Y = " + avatar.getLocalLocation().y() + "Z = " + avatar.getLocalLocation().z();
		String dispStr3 = "Stored items = " + storedPrizesStr + "  Score = " + scoreStr;
		Vector3f hud1Color = new Vector3f(1,0,0);
		Vector3f hud2Color = new Vector3f(0,0,1);
		(engine.getHUDmanager()).setHUD1(dispStr3, hud2Color, (int)((engine.getRenderSystem()).getSize().getWidth() - (engine.getRenderSystem()).getViewport("LEFT").getActualWidth()), 15);
		(engine.getHUDmanager()).setHUD2(dispStr1, hud1Color, (int)((engine.getRenderSystem()).getSize().getWidth() - (engine.getRenderSystem()).getViewport("RIGHT").getActualWidth()), 15);

		im.update((float)elapsTime);
		orbitController.updateCameraPosition();
		overviewController.updateCameraPosition();

		if (toggleLines.getToggleLines()){
			x.getRenderStates().disableRendering();
			y.getRenderStates().disableRendering();
			z.getRenderStates().disableRendering();
		}
		else{
			x.getRenderStates().enableRendering();
			y.getRenderStates().enableRendering();
			z.getRenderStates().enableRendering();
		}
		

		//assure objects only shrink once
		if (sc1.isEnabled()){sc1.disable();}
		if (sc2.isEnabled()){sc2.disable();}
		if (sc3.isEnabled()){sc3.disable();}
		if (scH.isEnabled()){scH.disable();}

		if (avatar.getLocalLocation().distance(prize1.getWorldLocation().x(), prize1.getWorldLocation().y(), prize1.getWorldLocation().z()) < 2 && 	!rc1.isEnabled()) {
			storedPrizes++;
			rc1.toggle();
			sc1.toggle();
		}
			

		if (avatar.getLocalLocation().distance(prize2.getWorldLocation().x(), prize2.getWorldLocation().y(), prize2.getWorldLocation().z()) < 2 && 	!rc2.isEnabled()) {
			storedPrizes++;
			rc2.toggle();
			sc2.toggle();

		}

		if (avatar.getLocalLocation().distance(prize3.getWorldLocation().x(), prize3.getWorldLocation().y(), prize3.getWorldLocation().z()) < 2 && 	!rc3.isEnabled()) {
			storedPrizes++;
			rc3.toggle();
			sc3.toggle();
		}

		if (avatar.getLocalLocation().distance(pyr.getWorldLocation().x(), pyr.getWorldLocation().y(), pyr.getWorldLocation().z()) < 2 && turnInPrizes.getToggle() && storedPrizes > 0) {
			storedPrizes--;
			score++;
			scH.toggle();
		}
		if (avatar.getLocalLocation().distance(house.getWorldLocation().x(), house.getWorldLocation().y(), house.getWorldLocation().z()) < 2 && turnInPrizes.getToggle() && storedPrizes > 0) {
			storedPrizes--;
			score++;
			scH.toggle();
		}



	}

	//----- getters -----------

	public GameObject getAvatar() { return avatar; }
	
	public Engine getGameEngine() {return engine;}

	public float getLastFrameTime(){return (float)((currFrameTime - lastFrameTime) / 1000.0);}
}