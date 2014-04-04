GdxStudio 0.7.1
================
GdxStudio is used for creating awesome games using libGdx. It has all the features of libgdx
built-in so you can easily,start creating games with it. Tools like Font Editor, Particle Editor,
Texture Packer, SceneEditor, MapEditor, ActorEditor, ImagingTools are already built into it. 
It also has a powerful Game Framework based on libGDX inbuilt which allows the game coder 
to concentrate on the logic of the game and not bother about setting up assets or configuration. 
It has Automatic Asset Loading including Atlas, TextureRegions, BitmapFonts, Music, Sound.
It has the Latest Nightly Version of libGdx inside it so you don't need to download the libGdx at all,
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
		