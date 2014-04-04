import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JOptionPane;

import com.badlogic.gdx.utils.ArrayMap;

public class Export {
	
	private static final String[] frameWorkClasses = {
		"Actions3d", "Action3d", "Actor3d", "Group3d", "Event3d", "InputEvent3d", "Scene3d", "Stage3d",
		"Asset", "Config", "Effect", "ImageJson", "Map", "MapActor", "Net", "Scene", "Serializer", 
		"Sprite", "Stage", "Type", "Listener", 
		
		"com/badlogic", "com/esotericsoftware", "gdx",
		
		"com/jcraft", "javazoom", "org/lwjgl", "sfd.ser",
		
		"lwjgl", "OpenAL", "openal",
	};
	
	private static final String[] excludeClasses = {
		"org/fife", "org/eclipse" , "com/threerings", "Panel", "tools", "web/laf"
	};
	
	private static final String eclipse_project =
	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	+"<projectDescription>"
	+	"<name>$$$</name>"
	+		"<comment></comment>"
	+	"<projects>"
	+	"</projects>"
	+	"<buildSpec>"
	+	"<buildCommand>"
	+		"<name>org.eclipse.jdt.core.javabuilder</name>"
	+		"<arguments>"
	+		"</arguments>"
	+	"</buildCommand>"
	+	"</buildSpec>"
	+	"<natures>"
	+	"<nature>org.eclipse.jdt.core.javanature</nature>"
	+	"</natures>"
	+"</projectDescription>";
	
	/*private static final String eclipse_cp =
	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	+"<classpath>"
		<classpathentry kind="src" path="atlas"/>"
		<classpathentry kind="src" path="font"/>
		<classpathentry kind="src" path="map"/>
		<classpathentry kind="src" path="model"/>
		<classpathentry kind="src" path="music"/>
		<classpathentry kind="src" path="particle"/>
		<classpathentry kind="src" path="skin"/>
		<classpathentry kind="src" path="sound"/>
		<classpathentry kind="src" path="source"/>
		<classpathentry kind="src" path="splash"/>
		<classpathentry kind="src" path="actor"/>
		<classpathentry kind="src" path="asset"/>
		<classpathentry kind="src" path="config"/>
		<classpathentry kind="src" path="scene"/>
		<classpathentry kind="src" path="icon"/>
		<classpathentry kind="lib" path="pathtogdxsutiod.jar"/>
		<classpathentry kind="output" path="bin"/>
		<classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER"/>
	+"</classpath>";*/

	
	public static void createProject(String foldername){
		Main.log("Creating Project: "+Content.getProject());
		Content.setProject(foldername);
		new File(Content.getProject()+"atlas").mkdir();
		new File(Content.getProject()+"font").mkdir();
		new File(Content.getProject()+"map").mkdir();
		new File(Content.getProject()+"model").mkdir();
		new File(Content.getProject()+"music").mkdir();
		new File(Content.getProject()+"sound").mkdir();
		new File(Content.getProject()+"source").mkdir();
		new File(Content.getProject()+"skin").mkdir();
		new File(Content.getProject()+"pack").mkdir();
		new File(Content.getProject()+"particle").mkdir();
		new File(Content.getProject()+"META-INF").mkdir();
		ArrayMap<String, String> map = new ArrayMap<String, String>();
		map.put("Title", "");
		map.put("TargetSize", "");
		map.put("ScreenSize", "");
		map.put("AudioBufferCount", "");
    	map.put("Resize", "");
    	map.put("ForceExit", "");
    	map.put("FullScreen", "");
    	map.put("UseGL20", "");
    	map.put("VSync", "");
    	map.put("DisableAudio", "");
    	map.put("KeepAspectRatio", "");
    	map.put("ShowFPS", "");
    	map.put("LoggingEnabled", "");
    	map.put("Version", "");
    	map.put("Target", "");
    	File prjFile = new File(Content.getProject());
		Export.writeFile("config", Stage.json.toJson(map, ArrayMap.class));
		Export.writeFile(".project", eclipse_project.replace("$$$", prjFile.getName()));
		Frame.setEnabledProject();
		createJar();
	}
	
	public static void openProject(String foldername){
		Main.log("Opening Project: "+foldername);
		Content.setProject(foldername);
		Frame.setEnabledProject();
	}
	
	public static void writeFile(String filename, String data){
		if(!Content.projectExists())
			return;
		Main.log("Writing File: "+filename);
		File file = new File(Content.getProject()+filename);
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(data.getBytes());
			fos.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Couldn't Save File: "+filename, "Error", 
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	public static String readFile(String filename){
		if(!Content.projectExists())
			return "";
		Main.log("Reading File: "+filename);
		StringBuffer sb = new StringBuffer();
		File file = new File(Content.getProject()+filename);
		if(!file.exists())
			return "";
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);
			String line;
            while ((line=br.readLine())!=null){
            	sb.append(line);
            	sb.append(System.lineSeparator());;
            }
            br.close();
			fr.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Couldn't Read File: "+filename, "Error", 
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public static String readFileFromClassPath(String filename){
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					Export.class.getClassLoader().getResourceAsStream(filename)));
			for (int c = br.read(); c != -1; c = br.read()) 
				sb.append((char)c);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public static ArrayList<String> listFiles(String foldername){
		if(!Content.projectExists())
			return null;
		ArrayList<String> list = new ArrayList<String>();
		File folder = new File(Content.getProject()+foldername);
		if(folder.exists())
			for (String member : folder.list())
				list.add(member);
		return list;
	}
	
	public static void deleteFile(String filename){
		if(!Content.projectExists())
			return;
		Main.log("Deleting File: "+filename);
		try {
			Files.delete(new File(Content.getProject()+filename).toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static File prjFile;
	static List<File> fileList = new ArrayList<File>();
	static Manifest manifest = new Manifest();
	static{
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		manifest.getMainAttributes().put(Attributes.Name.CLASS_PATH, ". ");
		manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, "Stage");
		manifest.getMainAttributes().put(Attributes.Name.IMPLEMENTATION_VENDOR, "pyros2097");
	}

	public static void createJar(){
		if(!Content.projectExists())
			return;
		String filename = Export.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		if(!filename.endsWith(".jar"))
			return;
		Main.log("Exporting Project: "+Content.getProject());
		fileList.clear();
		prjFile = new File(Content.getProject());
		generateFileList(prjFile);
		byte[] buffer = new byte[1024];
		try{
			FileOutputStream fos = new FileOutputStream(Content.getProject()+prjFile.getName()+".jar");
			JarOutputStream jos = new JarOutputStream(fos, manifest);
			JarEntry je;
			for(File file : fileList){
				if(file.getParentFile().equals(prjFile) || file.getParentFile().getName().equals("bin"))
					je = new JarEntry(file.getName());
				else
					je = new JarEntry(file.getParentFile().getName()+"/"+file.getName());
				jos.putNextEntry(je);
				FileInputStream in = new FileInputStream(file);
				int len;
				while ((len = in.read(buffer)) > 0) {
					jos.write(buffer, 0, len);
				}
				in.close();
				jos.closeEntry();
			}
			addLibrary(jos);
			jos.close();
		}catch(IOException ex){
			ex.printStackTrace();   
		}
		JOptionPane.showMessageDialog(null, "Exported: "+prjFile.getName()
				+" to "+Content.getProject()+prjFile.getName()+".jar", "Export", 
				JOptionPane.INFORMATION_MESSAGE);
	}

	private static void generateFileList(File node){
		if(node.isFile()){
			if(!node.getName().contains("pack") && !node.getName().equals(prjFile.getName()+".jar")){
				fileList.add(node);
			}
		}
		else{
			for(String filename : node.list())
				generateFileList(new File(node, filename));
		}
	}
	
	private static void addLibrary(JarOutputStream jos){
		String filename = Export.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		if(!filename.endsWith(".jar"))
			return;
		ZipFile jf = null;
		byte[] buffer = new byte[512];
		try {
			jf = new ZipFile(filename);
			Enumeration<? extends ZipEntry> e = jf.entries();
			while (e.hasMoreElements()) 
			{
				ZipEntry je = (ZipEntry) e.nextElement();
				String entryName = je.getName();
				for(String name: frameWorkClasses){
					if(!entryName.contains(excludeClasses[0]) && !entryName.contains(excludeClasses[1]) 
						&& !entryName.contains(excludeClasses[2]) && !entryName.contains(excludeClasses[3]) 
						&& !entryName.contains(excludeClasses[4]) && !entryName.contains(excludeClasses[5])
						&& entryName.contains(name)) {
							Main.log(entryName);
							ZipEntry newEntry = new ZipEntry(entryName); 
							jos.putNextEntry(newEntry);
							InputStream in = jf.getInputStream(je);
					        while (0 < in.available()){
					            int read = in.read(buffer);
					            jos.write(buffer,0,read);
					        }
					        in.close();
							jos.closeEntry();
							break;
						}
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		finally{
			try {
				if(jf != null)
					jf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void createDex(){
		File prjFile = new File(Content.getProject());
		String name = prjFile.getName();
		String buildArgs = "--no-strict --output="+Content.getProject()+name+".apk "+Content.getProject()+"/bin";
		try {
			com.android.dx.command.dexer.Main.main(buildArgs.split(" "));
		} catch (IOException e) {
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, "Exported: "+name+" to "+Content.getProject()+name+".apk",
				"Export", JOptionPane.INFORMATION_MESSAGE);
	}
	
}