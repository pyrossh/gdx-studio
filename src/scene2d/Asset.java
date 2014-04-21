package scene2d;
import scene3d.Actor3d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonValue;

/** Automatic Assets Loading for the Game
 * <p>
 *  This class automatically loads all the assets in the prescribed folders into the appropriate 
 *  class types. All This can be accessed using an neat api.
 * 
 * <p>
 *	<b>#Important </b> <br>
 *	All asset files must be lowercase only.. otherwise it causes problems with android <br> 
 *	1. All Assets are to be stored in the assets directory <br>
 *	2. Automatic Asset Loading the directory Structure should be like this <br>
 *
 *  <p>
 *  icon.png  --- your game icon to be displayed on the window<br>
 *	atlas/  --- all your Texture Atlas files .atlas and .png go here<br>
 *	font/  --- all your BitmapFont files .fnt and .png go here<br>
 *	music/  --- all your Music files .mp3 go here<br>
 *	sound/  --- all your Music files .mp3 go here<br>
 *	particle/  --- all your Particle files .part go here<br>
 *	map/  --- all your TMX map files along with tilesets go here<br>
 *	pack/  --- all your image files which are to be packed are to be stored here<br>
 *					  so that they are automatically packed by the texture packer and stored in
 *					  the atlas folder
 *	
 * <p>   
 * All the assets are read from their particular folders and an asset.json file is created with
 * the filenames:
 * ex: asset.json 			
 * 	{ 
 * 	  "font": "arial.fnt, font1",
 *    "atlas": "image.atlas",
 *    "sound": "click.mp3,gg.mp3",
 *    "music": "title.mp3,"bg.mp3",
 *	}
 * All assets are accessed this way,<br>
 * <pre>
 * <code>
 *
 *	//To load TextureRegion
 *	TextureRegion cat = Asset.tex("cat");
 *	
 *	//To load Animation
 *	Animation catAnim = Asset.anim("cat");
 *	
 *	//To load BitmapFont
 *	BitmapFont font1 = Asset.font("font1");
 *
 *	//The music and sound files are automatically cached and can be played by invoking:
 *	Asset.musicPlay("musicname");
 *	Asset.soundPlay("soundname");
 *	
 *  //The asset functions will return null for Font, TextureRegion and Animation if the asset cannot be found
 * </code>
  </pre>
 * </p>
 * @author pyros2097 */

public final class Asset {
	private static AssetManager assetMan = new AssetManager();
	
	public static Skin skin;
	public final static ArrayMap<String, String> assetMap = new ArrayMap<String, String>();
	public final static ArrayMap<String, Music> musicMap = new ArrayMap<String, Music>();
	public final static ArrayMap<String, Sound> soundMap = new ArrayMap<String, Sound>();
	public final static ArrayMap<String, BitmapFont> fontMap = new ArrayMap<String, BitmapFont>();
	public final static ArrayMap<String, TextureRegion> texMap = new ArrayMap<String, TextureRegion>();
	public final static ArrayMap<String, TextureRegion> particleMap = new ArrayMap<String, TextureRegion>();
	public final static Array<String> modelMap = new Array<String>();
	
	private static Music currentMusic = null;
	public static String currentMusicName = ""; //Only file name no prefix or suffix
	private static Sound currentSound = null;
	
	private static boolean readinglock = false;
	private static boolean updatinglock = false;
	
	/* This is to be used by Gdx Studio */
	public static String basePath = "";
	
	public static boolean musicOn = true;
	public static boolean soundOn = false;
	public static boolean loadAsynchronous = true;
	
	private static void clear(){
		assetMap.clear();
		fontMap.clear();
		texMap.clear();
		particleMap.clear();
		musicMap.clear();
		soundMap.clear();
		modelMap.clear();
	}
	
	public static void load(){
		if(loadAsynchronous)
			loadNonBlocking();
		//else
		//	loadBlocking();
	}
	/*
	 * This is a blocking load call blocks the display until assets are all loaded.
	 */
	public static void loadBlocking(){
		clear();
		readinglock = true;
		updatinglock = true;
		loadAssets();
		assetMan.finishLoading();
		assetMan.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		getAssets();
	}
	
	/*
	 * This is a non-blocking load call that allows you to display other things while the load is going
	 * on. This is called in the act method of SplashScene so that it is runs in the background
	 * once the assets are all loaded it will automatically stop and call SplashScene.onAssetsLoaded()
	 */
	private static boolean loadNonBlocking(){
		if(!readinglock){
			clear();
			loadAssets();
			readinglock = true;
		}
		// once update returns true then condition is satisfied and the lock stops update call
		if(!updatinglock)
			if(assetMan.update()){
				assetMan.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
				getAssets();
				updatinglock = true;
				if(Scene.splashDuration != 0)
					Scene.nextSceneWithDelay(Scene.splashDuration);
				else
					Scene.nextScene();
			}
		return updatinglock;
	}
	
	public static void loadAssets(){
		JsonValue sv = Scene.jsonReader.parse(Gdx.files.internal(Asset.basePath+"asset"));
 		for(JsonValue jv: sv.iterator())
 			assetMap.put(jv.name, jv.asString());
 		for(String font: assetMap.get("font").split(",")){
 			if(!font.isEmpty())
 				assetMan.load(basePath+"font/"+font, BitmapFont.class);
 		}
 		for(String atlas: assetMap.get("atlas").split(",")){
 			if(!atlas.isEmpty())
 				assetMan.load(basePath+"atlas/"+atlas, TextureAtlas.class);
 		}
 		for(String sound: assetMap.get("sound").split(",")){
 			if(!sound.isEmpty())
 				assetMan.load(basePath+"sound/"+sound, Sound.class);
 		}
 		for(String music: assetMap.get("music").split(",")){
 			if(!music.isEmpty())
 				assetMan.load(basePath+"music/"+music, Music.class);
 		}
 		assetMan.load(basePath+"skin/uiskin.json", Skin.class);
	}
	
	public static void getAssets(){
		for(String font: assetMap.get("font").split(",")){
			if(!font.isEmpty())
				fontMap.put(ext(font), assetMan.get(basePath+"font/"+font, BitmapFont.class));
		}
 		for(String atlas: assetMap.get("atlas").split(",")){
 			if(!atlas.isEmpty())
 				for(TextureAtlas.AtlasRegion ar: assetMan.get(basePath+"atlas/"+atlas, 
 						TextureAtlas.class).getRegions())
 					texMap.put(ar.name, ar);
 		}
 		for(String sound: assetMap.get("sound").split(",")){
 			if(!sound.isEmpty())
 				soundMap.put(ext(sound), assetMan.get(basePath+"sound/"+sound, Sound.class));
 		}
 		for(String music: assetMap.get("music").split(",")){
 			if(!music.isEmpty())
 				musicMap.put(ext(music), assetMan.get(basePath+"music/"+music, Music.class));
 		}
 		skin = assetMan.get(basePath+"skin/uiskin.json", Skin.class);
 		if(Scene.configJson.getBoolean("showFPS"))
 			Scene.setupFps();
	}
	
	private static String ext(String file){
		return file.substring(0, file.indexOf("."));
	}
	
	public static void save(){
		Gdx.files.local(Asset.basePath+"asset").writeString(Scene.json.toJson(assetMap, ArrayMap.class, String.class), false);
	}
	
/***********************************************************************************************************
* 								Music Related Global Functions											   *
************************************************************************************************************/
	/** Plays the music file which was dynamically loaded if it is present otherwise logs the name
	 *  @param filename The Music File name only
	 *  @ex <code>music("title")</code>
	 *  */
	public static void musicPlay(String filename){
		if(Config.isMusic){
			if(currentMusic != null)
				if(currentMusic.isPlaying())
					if(currentMusicName == filename)
						return;
					else
						musicStop();
			if(musicMap.containsKey(filename)){
				Scene.log("Music: playing "+filename);
				currentMusic = musicMap.get(filename);//Gdx.audio.newMusic(Gdx.files.internal("music/"+filename));
				currentMusic.setVolume(Config.volMusic);
				currentMusic.setLooping(true);
				currentMusic.play();
				currentMusicName = filename;
			}
			else{
				Scene.log("Music File Not Found: "+filename);
			}
		}
	}
	
	/** Pauses the current music file being played */
	public static void musicPause(){
		if(currentMusic != null)
			if(currentMusic.isPlaying()){
				Scene.log("Music: pausing "+currentMusicName);
				currentMusic.pause();
			}
	}
	
	/** Resumes the current music file being played */
	public static void musicResume(){
		if(currentMusic != null)
			if(!currentMusic.isPlaying()){
				Scene.log("Music: resuming "+currentMusicName);
				currentMusic.play();
			}
		else
			musicPlay(currentMusicName);
	}
	
	/** Stops the current music file being played */
	public static void musicStop(){
		if(currentMusic != null){
			Scene.log("Music: stoping "+currentMusicName);
			currentMusic.stop();
			currentMusic = null;
		}
	}
	
	/** Sets the volume music file being played */
	public static void musicVolume(){
		if(currentMusic != null);
			currentMusic.setVolume(Config.volMusic);
	}
	
	/** Disoposes the current music file being played */
	public static void musicDispose(){
		if(currentMusic != null);
			currentMusic.dispose();
	}
	
/***********************************************************************************************************
* 								Sound Related Global Functions							   				   *
************************************************************************************************************/
	/** Plays the sound file which was dynamically loaded if it is present otherwise logs the name
	 *  @param filename The Sound File name only
	 *  @ex <code>soundPlay("bang")</code>
	 *  */
	public static void soundPlay(String filename){
		if(Config.isSound){
			if(soundMap.containsKey(filename)){
				currentSound = soundMap.get(filename);
				long id = currentSound.play(Config.volSound);
				currentSound.setLooping(id, false);
				currentSound.setPriority(id, 99);
				Scene.log("Sound:"+"Play "+ filename);
			}
			else{
				Scene.log("Music File Not Found: "+filename);
			}
		}
	}
	
	/** Plays the sound file "click" */
	public static void soundClick(){
		if(Config.isSound && soundMap.containsKey("click")){
	        currentSound = soundMap.get("click");
			long id = currentSound.play(Config.volSound);
			currentSound.setLooping(id, false);
			currentSound.setPriority(id, 99);
			Scene.log("Sound:"+"Play "+ "click");
		}
	}
	
	/** Pauses the current sound file being played */
	public static void soundPause(){
		Scene.log("Sound:"+"Pausing");
		if(currentSound != null)
			currentSound.pause();
	}
	
	/** Resumes the current sound file being played */
	public static void soundResume(){
		Scene.log("Sound:"+"Resuming");
		if(currentSound != null)
			currentSound.resume();
	}
	
	/** Stops the current sound file being played */
	public static void soundStop(){
		Scene.log("Sound:"+"Stopping");
		if(currentSound != null)
			currentSound.stop();
	}
	
	/** Disposes the current sound file being played */
	public static void soundDispose(){
		Scene.log("Sound:"+"Disposing Sound");
		if(currentSound != null)
			currentSound.dispose();
	}
	
	
/***********************************************************************************************************
* 								BitmapFont Related Functions							   				   *
************************************************************************************************************/
	/** If key is present returns the BitmapFont that was dynamically loaded
	 *  else returns null
	 *  @param fontname The BitmapFont name
	 *  @return BitmapFont or null
	 *  @ex font("font1") or font("arial")
	 *  */
	public static BitmapFont font(String fontname){
		if(fontMap.containsKey(fontname)){
			return fontMap.get(fontname);
		}
		else{
			Scene.log("Font File Not Found: "+fontname);
			return null;
		}
	}
	
/***********************************************************************************************************
* 								Texture Related Functions							   				   	   *
************************************************************************************************************/
	/** If key is present returns the TextureRegion that was loaded from all the atlases
	 *  else returns null
	 *  @param textureregionName The TextureRegion name
	 *  @return TextureRegion or null
	 *  */
	public static TextureRegion tex(String textureregionName){
		if(texMap.containsKey(textureregionName)){
			return texMap.get(textureregionName);
		}
		else{
			Scene.log("TextureRegion Not Found: "+textureregionName);
			return null;
		}
	}
	
/***********************************************************************************************************
* 								Animation Related Functions							   				   	   *
************************************************************************************************************/
	private static Animation anim(String texName, int numberOfFrames, int hOffset) {
		// Key frames list
		TextureRegion[] keyFrames = new TextureRegion[numberOfFrames];
		TextureRegion texture = Asset.tex(texName);
		int width = texture.getRegionWidth() / numberOfFrames;
		int height = texture.getRegionHeight();
		// Set key frames (each comes from the single texture)
		for (int i = 0; i < numberOfFrames; i++)
			keyFrames[i] = new TextureRegion(texture, width * i, hOffset, width, height);
		Animation animation = new Animation(1f/numberOfFrames, keyFrames);
		return animation;
	}
	
	private static Animation anim(String texName, int numberOfFrames, float duration, int hOffset) {
		// Key frames list
		TextureRegion[] keyFrames = new TextureRegion[numberOfFrames];
		TextureRegion texture = Asset.tex(texName);
		int width = texture.getRegionWidth() / numberOfFrames;
		int height = texture.getRegionHeight();
		// Set key frames (each comes from the single texture)
		for (int i = 0; i < numberOfFrames; i++)
			keyFrames[i] = new TextureRegion(texture, width * i, hOffset, width, height);
		Animation animation = new Animation(duration, keyFrames);
		return animation;
	}
	
	/**
	 * Get animation from single textureRegion which contains all frames 
	 * (It is like a single png which has all the frames). Each frames' width should be same.
	 * <p>
	 * @param texName
	 * 			  the name of the texture region
	 * @param numberOfFrames
	 *            number of frames of the texture Region
	 * @return animation created
	 * 
	 * */
	public static Animation anim(String texName, int numberOfFrames) {
		return anim(texName, numberOfFrames, 0);
	}
	
	/**
	 * Get animation from single textureRegion which contains all frames 
	 * (It is like a single png which has all the frames). Each frames' width should be same.
	 * <p>
	 * @param texName
	 * 			  the name of the texture region
	 * @param numberOfFrames
	 *            number of frames of the texture Region
	 * @param duration
	 *            each frame duration on play
	 * @return animation created
	 * 
	 * */
	public static Animation anim(String texName, int numberOfFrames, float duration) {
		return anim(texName, numberOfFrames, duration, 0);
	}

	/**
	 * There is only single texture region which contains all frames 
	 * (It is like a single png which has all the frames). 
	 * The texture regions consists of both rows and columns
	 * <p>
	 * 
	 * @param textureAtlas
	 *            atlas which contains the single animation texture
	 * @param animationName
	 *            name of the animation in atlas
	 * @param numberOfFrames
	 *            number of frames of the animation
	 * @param numberOfMaximumFramesInTheSheet
	 *            maximum number of frame in a row in the sheet
	 * @param numberOfRows
	 *            number of rows that the sheet contains
	 * @param indexOfAnimationRow
	 *            the row index (starts from 0) that desired animation exists
	 * @param frameDuration
	 *            each frame duration on play
	 * @return animation created
	 * 
	 * */
	public static Animation[] anim(String texName, int rows, int cols, float duration) {
		TextureRegion texture = Asset.tex(texName);
		// Set key frames (each comes from the single texture)
		/*for (int i = 0; i < numberOfFrames; i++) {
			keyFrames[i] = new TextureRegion(
					textureRegion,
					(textureRegion.getRegionWidth() / numberOfMaximumFramesInTheSheet)
							* i, textureRegion.getRegionHeight() / numberOfRows
							* indexOfAnimationRow,
					textureRegion.getRegionWidth()
							/ numberOfMaximumFramesInTheSheet,
					textureRegion.getRegionHeight() / numberOfRows);
		}*/
		int height = texture.getRegionHeight()/rows;
		Animation[] animations = new Animation[rows];
		for(int i=0;i<rows;i++)
			animations[i] = anim(texName, cols, i*height);
		return animations;
	}
	
/***********************************************************************************************************
	* 								TMX MAP Related Functions							   				   *
************************************************************************************************************/
	/*
	 * Loads a Tmx map by specifying the map/level no
	 * eg: loadTmx(4) -> returns the TiledMap "map/level4.tmx"
	 * 
	 * Note: Tmx Maps must be loaded and unloaded manually as they may take a lot of time to load
	 */
	public static TiledMap map(int i){
		assetMan.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		assetMan.load(basePath+"map/level"+i+".tmx", TiledMap.class);
		assetMan.finishLoading();
		return assetMan.get(basePath+"map/level"+i+".tmx", TiledMap.class);
	}
	
	/*
	 * unloads a Tmx map by specifying the map/level no
	 * eg: unloadTmx(4) -> unloads the TiledMap "map/level4.tmx"
	 * 
	 * Note: Tmx Maps must be unloaded manually 
	 */
	public static void unloadmap(int i){
		assetMan.unload(basePath+"map/level"+i+".tmx");
	}
	
	/*
	 * Load a G3db model from the model directory
	 * @param modelName The name of the modelFile
	 * @example loadModel("ship");
	 */
	public static Actor3d loadModel(String modelName){
		assetMan.load(basePath+"model/"+modelName+".g3db", Model.class);
		assetMan.finishLoading();
		return new Actor3d(assetMan.get(basePath+"model/"+modelName+".g3db", Model.class));
	}
	
	/*
	 * Unload a G3db model which was previously loaded in the assetManager
	 * @param modelName The name of the modelFile that was loaded
	 * @example unloadModel("ship");
	 */
	public static void unloadModel(String modelName){
		assetMan.unload(basePath+"model/"+modelName+".g3db");
	}
	
	/*
	 * Load a Obj model from the model directory
	 * @param modelName The name of the modelFile
	 * @example loadModel("ship");
	 */
	public static Actor3d loadModelObj(String modelName){
		assetMan.load(basePath+"model/"+modelName+".obj", Model.class);
		assetMan.finishLoading();
		return new Actor3d(assetMan.get(basePath+"model/"+modelName+".obj", Model.class));
	}
	
	/*
	 * Unload a Obj model which was previously loaded in the assetManager
	 * @param modelName The name of the modelFile that was loaded
	 * @example unloadModel("ship");
	 */
	public static void unloadModelObj(String modelName){
		assetMan.unload(basePath+"model/"+modelName+".obj");
	}
	
/***********************************************************************************************************
* 								LOG Related Functions							   				   	   	   *
************************************************************************************************************/
	/*
	 * Logs all the assets that are loaded and cached
	 */
	public static void logAll(){
		logTextures();
		logFonts();
		logSounds();
		logMusics();
	}
	
	/*
	 * Logs all the TextureRegions that are loaded and cached
	 */
	public static void logTextures(){
		Scene.log("BEGIN logging Textures------------------");
		for(String na: texMap.keys())
			Scene.log(na);
		Scene.log("END logging Textures------------------");
	}
	
	/*
	 * Logs all the BitmapFonts that are loaded and cached
	 */
	public static void logFonts(){
		Scene.log("BEGIN logging Fonts------------------");
		for(String na: fontMap.keys())
			Scene.log(na);
		Scene.log("END logging Fonts------------------");
	}
	
	/*
	 * Logs all the Sounds that are loaded and cached
	 */
	public static void logSounds(){
		Scene.log("BEGIN logging Sounds------------------");
		for(String na: soundMap.keys())
			Scene.log(na);
		Scene.log("END logging Sounds------------------");
	}
	
	/*
	 * Logs all the Music that are loaded and cached
	 */
	public static void logMusics(){
		Scene.log("BEGIN logging Musics------------------");
		for(String na: musicMap.keys())
			Scene.log(na);
		Scene.log("END logging Musics------------------");
	}
	
	/*
	 * Unloads and disposes all the resources except for Tmx Maps
	 * This is called by Sink.exit();
	 */
	public static void unloadAll(){
		assetMan.dispose();
	}
	
	/*
	 * If using in eclipse set basePath to ./bin/
	*/
	public static void setBasePath(String path){
		basePath = path;
	}
}