RpgIDE v0.60
================
RpgIDE is a tool for creating awesome games using libGdx. It is written in Java using Swing, libgdx,
WebLookandfeel and RSyntaxTextArea. It has similar features of Xcode. It has all the features of libgdx
built-in so you can easily,start creating games with it. Tools like Hiero Font Editor, Particle Editor,
Texture Packer, GDXBuilder, TMXBuilder, ImagingTools are already built into it. 
It also has a powerful Game Framework based on libGDX inbuilt which allows the game coder 
to concentrate on the logic of the game and not bother about setting up assets or configuration. 
It has Automatic Asset Loading including Atlas, TextureRegions, BitmapFonts, Music, Sound.
It has the Latest Nightly Version of libGdx inside it so you don't need to download the libGdx at all,
when exporting to jar for desktop it automatically loads these libraries into it.


Checkout: [Sink](https://github.com/pyros2097/Sink)

Features
---------
#1. Automatic Build System
Uses an incremental Builder system like eclipse, so on the fly building so you can instantly
run or debug your application. Using the famous Eclipse Java Compiler(ECJ).
#2. Automatic File Saving
All Files are automatically save when you switch tabs or change views. No more wasting time pressing
CTRL+S or clicking the save button (inspired by Xcode)
#3. Studio
Your completely design your game scenes or menus using the inbuilt studio/game editor and access these
components in the editor and add your logic code. Your scenes are saved as json files and can be loaded in
your code.
#4. Editor
An eclipse like editor which supports code completion, incremental compiling and compile errors as you type.
It also allows drag and drop of actors and assets from their tables for fast typing.
#5. Export
Your game is exported to jar for Desktop and is run from the jar so you have complete
understanding of how your game works in its packaged state.
ex: Android, iOS, Desktop
#6. Platform Independent
Write Once Deploy Everywhere. You only need to write you game logic for one platform, cross compiling 
for different platforms and loading is done automatically.
#7. Dynamic Compilation
You can edit your scenes and add logic to your game and at the same time see the outcome in the studio.
Your source files are automatically compiled and loaded into the class loaded and displayed in the studio.
So your don't need to follow the monotonous approach compile->build->run. This saves a lot of time.
#7. Scenes Support
You can easily create scenes populate them with actors and load them.

					  	  					  	  					  	
Todo
-----
1. Make Animations Working
2. TMXBuilder
3. Automatic Updates
4. Make a signals/slots method for connecting actors with events (maybe make an interpreter)

Credits
--------
Thanks to all these awesome frameworks  
[Libgdx](http://libgdx.badlogicgames.com)  
[WebLookAndFeel](http://weblookandfeel.com)  
[RSyntaxTextArea](http://fifesoft.com/rsyntaxtextarea)  
		
