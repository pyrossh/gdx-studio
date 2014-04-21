GdxStudio 0.7.1
================
GdxStudio is used for creating awesome games using libGdx. It has all the features of libgdx
built-in so you can easily,start creating games with it. Tools like Font Editor, Particle Editor,
Texture Packer, SceneEditor, MapEditor, ActorEditor, ImagingTools are already built into it. 
It also has a powerful Game Framework based on libGDX inbuilt which allows the game coder 
to concentrate on the logic of the game and not bother about setting up assets or configuration. 
It has Automatic Asset Loading including Atlas, TextureRegions, BitmapFonts, Music, Sound.
It has the Latest Stable Version of libGdx inside it so you don't need to download the libGdx at all,
when exporting your game to jar for desktop it automatically loads these libraries into it.

>**Disclaimer**  This is not an official libdgx project so don't ask them for bug fixes  

[See the Wiki for more details](https://github.com/pyros2097/GdxStudio/wiki)  

Features
---------
**1. Automatic Build System**
<p>Uses an Batch Builder system based on eclipse, so on the fly building so you can instantly
run or debug your application. Using the famous Eclipse Java Compiler(ECJ).<p>
**2. Automatic File Saving**
<p>All Files are automatically save when you switch tabs or change views. No more wasting time pressing
CTRL+S or clicking the save button (inspired by Xcode)<p>
**3. Source Editor**
<p>An eclipse like editor which supports code completion, batch compiling and compile errors as you type.<p>
**4. Scene Editor**  
<p>Completely design your game scenes with effects using the scene editor and access these components 
in the source editor and add your logic code. Your scenes are saved in scene.json file is automatically loaded.<p>
**5. Map Editor**  
<p>All your game maps can be designed using it. This supports 3 map layers and 3 object layers. 
You can drag and drop your custom actors/objects onto the map. There is no fixed size for a map. 
Each tile can be anything you want it to be.<p>
**6. Actor Editor**  
<p>You can create custom actors/objects which consist of the basic widgets like images, sprites etc.
And you can drag and drop these into your game scenes and maps. Makes it easier instead of doing it by code.<p>
**7. Export**  
<p>All the libgdx libraries and class files are directly exported to your package.
Your game can be exported to jar for Desktop, apk for android and ipa for iOS<p>
**8. Platform Independent**  
<p>Write Once Deploy Everywhere. You only need to write you game logic for one platform, cross building
for different platforms and exporting is done automatically.(android and ios not done)<p>
**9. Dynamic Compilation**  
<p>You can edit your scenes and add logic to your game and at the same time see the outcome in the studio.
Your source files are automatically compiled and loaded into the class loader and displayed in the studio.
So your don't need to follow the monotonous approach compile->build->run exe. This saves a lot of time.<p>


Using
------
```java
 //This is our first Scene and it shows the libgdx logo until all the assets are loaded 
    //then it automatically switches to the Menu scene
    public class  Splash extends Scene {
		
		public Splash() {
			splashDuration = 5f; // This will make my splash scene to wait 5 seconds after assets are all loaded
			final Texture bg1 = new Texture("splash/libgdx.png");
			final Image imgbg1 = new Image(bg1);
			imgbg1.setFillParent(true);
			addActor(imgbg1);
	    } 
   }
   
    //This is Scene gets called once the assets are loaded
    public class Menu extends Scene {
    
		public Menu() {
			//create some actors
			// if you used sink studio and create a scene like Menu.json then
			// it will automatically call load("Menu") it will populate your scene after parsing the json file
			
			//you can access these objects like this
			TextButton btn = (TextButton) findActor("TextButton1");
			Image img = (Image) findActor("Image5");
			
			// these actors are loaded from the json file and are give names which allows
			// easy access to them
		}
	}
```				  	  					  	  					  	
Todo
-----
1. MapEditor
2. ActorEditor
3. Automatic Updates
4. Make a signals/slots method for connecting actors with events (maybe make an interpreter)

Credits
--------
Thanks to all these awesome frameworks  
[Libgdx](http://libgdx.badlogicgames.com)  
[WebLookAndFeel](http://weblookandfeel.com)  
[RSyntaxTextArea](http://fifesoft.com/rsyntaxtextarea)  
[WebLookAndFeelLite](https://github.com/pyros2097/WebLookAndFeelLite)  
[Scene3d](https://github.com/pyros2097/Scene3d)  
[Sink](https://github.com/pyros2097/Sink)  
[EclipseCompiler](http://download.eclipse.org/eclipse/downloads/)  
[ANTLR](http://www.antlr.org/)  
[ProGuard](http://proguard.sourceforge.net)  

Screenshots
-----------
<img src = "https://github.com/pyros2097/GdxStudio/raw/master/shots/shot2.png">
<img src = "https://github.com/pyros2097/GdxStudio/raw/master/shots/shot3.png">
<img src = "https://github.com/pyros2097/GdxStudio/raw/master/shots/shot4.png">
<img src = "https://github.com/pyros2097/GdxStudio/raw/master/shots/shot5.png">
<img src = "https://github.com/pyros2097/GdxStudio/raw/master/shots/shot6.png">

Documentation
-------------
Scene
-----
<p>
It consists of a single Stage2d, Stage3d, Camera2d, and Camera3d which are all initialized based on the config file.
The root of the stage can be accessed in a statically {@link Scene#getRoot()} and methods related to camera like moveTo, moveBy,
are also accessed the same way.<br>
It has extra things like stateTime, gameUptime, pauseState<br>

It has automatic asset unloading and disposing and you can use {@link #exit()} to quit your game safely

Note: Your TMX maps have to be unloaded manually as they can be huge resources needing to be freed early.

It has static methods which can be used for panning the camera using mouse, keyboard, drag.. etc.
It can also automatically follow a actor by using followActor(Actor actor)<br>

This class will register all your scenes based on your scene.json file and then you can switch you scenes by using {@link #setScene}
method with the sceneClassName.<br>
All your assets are loaded in the background(asynchronously) in the first scene and then automatically 
the next scene in the list is set.
You can stop the stage from switching to the next scene by setting Asset.loadAsynchronous = false in your first scene but then
you have to load all the assets by using the blocking call Asset.loadBlocking()

If you want to display your splash screen for more time after the assets have loaded then you can change
Stage.splashDuration to the amount of time you want you splash screen to show. By default the splash screen's
lifetime is finished once the assets are loaded and then the next scene is set.
</p>

Camera
------
```java
/*
 *  This is to set the offsets of camera position when following the actor
 *  When the camera follows the actor its (x,y) position is set to actor's (x,y) position
 *  based on followSpeed. The offsets are used to position the camera in such a way that the actor
 *  doesn't need to be at the center of the camera always
 */
public static Rectangle followOffset;
public static boolean usePan;
public static boolean useDrag;

/*
 *  This sets the boundary of the camera till what position can it move or pan in the
 *  directions left, right, top, down. This is to prevent is from panning overboard the game area.
 *  Usually the bounds of the camera is like a rectangle. This must be calculated carefully
 *  as the camera's position is based on its center.
*/
public static Rectangle bounds;

/*
 * Moves the camera to x,y over a time duration
 */
public void moveTo(float x, float y, float duration);

/*
 * Moves the camera by amountX, amountY over a time duration
 */
public static void moveBy (float amountX, float amountY, float duration);

/*
 * Moves the camera by amountX, amountY over a time duration and interpolation interp
 */
public static void moveBy (float amountX, float amountY, float dur, Interpolation interp);
/*
 * This makes the camera follow the actor once and only once. Once the camera reaches its
 * target, it stops following the actor.
 */
public static void followActor(Actor actor);
/*
 * This makes the camera follow the actor continuously, even after the camera reaches its
 * target, it keeps following the if the actor changes its position.
 */
public static void followActorContinuously(Actor actor);
/*
 * Sets the speed at which the camera follows the actor. By default it moves 1px for a duration of 1f
 * so its speed is 1px/f. So reduce the duration to increase its speed.
 * ex: setPanSpeed(0.5) will change its speed to 2px/f
 * Here: f can/maybe also indicate seconds
 */
public static void setFollowSpeed(float duration);

/*
 * Sets the speed at which the camera pans. By default it moves 1px for a duration a 1f
 * so its speed is 1px/f. So reduce the duration to increase its speed.
 * ex: setPanSpeed(0.5) will change its speed to 2px/f
 * Here: f can/maybe also indicate seconds 
 */
public void setPanSpeed(float duration);

/* If you want to make any elements/actors to move along with the camera 
 * like HUD's add them using this method */
public static void addHud(Actor actor);

/* If you want to any elements/actors which was a Hud the use this */
public static void removeHud(Actor actor);

/*
 * Clears all hud's registerd with the camera
 */
public static void clearAllHud();

/*
 * Returns the x postion of the camera
 */
public static float getX();
/*
 * Returns the y postion of the camera
 */
public static float getY();		
```
Asset
-----
<b>#Important </b> <br>
All asset files must be lowercase only.. otherwise it causes problems with android <br> 
Automatic Asset Loading the directory Structure should be like this <br>
This class automatically loads all the assets in the prescribed folders into the appropriate 
class types. All This can be accessed using an neat api.

icon.png  --- your game icon to be displayed on the window<br>
atlas/  --- all your Texture Atlas files .atlas and .png go here<br>
font/  --- all your BitmapFont files .fnt and .png go here<br>
music/  --- all your Music files .mp3 go here<br>
sound/  --- all your Music files .mp3 go here<br>
particle/  --- all your Particle files .part go here<br>
map/  --- all your TMX map files along with tilesets go here<br>
pack/  --- all your image files which are to be packed are to be stored here<br>
 					  so that they are automatically packed by the texture packer and stored in
 					  the atlas folder
 					  
All the assets are read from their particular folders and an asset.json file is created with
the filenames:
ex: asset.json 			
	{ 
	  	"font": "arial.fnt, font1",
  		"atlas": "image.atlas",
  		"sound": "click.mp3,gg.mp3",
  		"music": "title.mp3,"bg.mp3",
	}
```java
//All assets are accessed this way, To load TextureRegion
TextureRegion cat = Asset.tex("cat");

//To load Animation
Animation catAnim = Asset.anim("cat");
BitmapFont font1 = Asset.font("font1");

//The music and sound files are automatically cached and can be played by invoking:
Asset.musicPlay("musicname");
Asset.soundPlay("soundname");

//The asset functions will return null for Font, TextureRegion and Animation if the asset cannot be found
```