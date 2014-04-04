import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

/**
 * A Swing application that downloads file from an HTTP server.
 * @author www.codejava.net
 *
 */
public class Update extends JFrame implements PropertyChangeListener, ActionListener {
	private static final long serialVersionUID = 1L;
	private JLabel labelFileName = new JLabel("File name: ");
	private JTextField fieldFileName = new JTextField(20);
	
	private JLabel labelFileSize = new JLabel("File size (bytes): ");
	private JTextField fieldFileSize = new JTextField(20);
	
	private JLabel labelProgress = new JLabel("Progress:");
	private JProgressBar progressBar = new JProgressBar(0, 100);
	
	private JButton updateButton = new JButton("Update");
	
	public static String[] libs = {
		"adnroid", "antlr", "autocomplete", "bluecove-glp", "bluecove", "dx", "ecj", 
		"gdx-backend-android", "gdx-backend-lwjgl-natives", "gdx-backend-lwjgl", "gdx-natives", "gdx",
		"proguard", "rsta", "socketio", "weblaflite"
	};
	
	String workDir = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
	String saveDir = workDir+"temp";
	
	public Update() {
		super("Swing File Download from HTTP server");
		System.setProperty("jsse.enableSNIExtension", "false");
		if(checkNewVersion());
			//startDownload("gdx.jar");
		else
			return;
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 5, 5);

		fieldFileName.setEditable(false);
		fieldFileSize.setEditable(false);
		
		progressBar.setPreferredSize(new Dimension(200, 30));
		progressBar.setStringPainted(true);
		
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 0.0;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.NONE;
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.CENTER;
		add(updateButton, constraints);
		updateButton.addActionListener(this);
		
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;
		add(labelFileName, constraints);
		
		constraints.gridx = 1;
		add(fieldFileName, constraints);
		
		constraints.gridy = 4;
		constraints.gridx = 0;
		add(labelFileSize, constraints);
		
		constraints.gridx = 1;
		add(fieldFileSize, constraints);
		
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;
		add(labelProgress, constraints);

		constraints.gridx = 1;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(progressBar, constraints);

		pack();
		setLocationRelativeTo(null);	// center on screen
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	@Override
	public void actionPerformed(ActionEvent e){
		new File(saveDir).mkdir();
		String file = "gdx.jar";
		//for(String file: libs){
		try {
			progressBar.setValue(0);
			int serverFileLength = getContentLength("https://github.com/pyros2097/Scene3d/raw/master/libs/"+file);
			long localFileLength = new File(workDir+"libs/"+file).length();
			Main.log("serverFileLength:"+serverFileLength+" localFileLength:"+localFileLength);
			if(serverFileLength == localFileLength)
				return;//continue;
			AsyncDownloader as = new AsyncDownloader("https://github.com/pyros2097/Scene3d/raw/master/libs/", file);
			as.addPropertyChangeListener(this);
			
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Error executing upload task: " + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}		
		//}
		new File(saveDir).deleteOnExit();
	}
	
	public String getNewVersion(){
		String line = "";
		HttpURLConnection httpConn = openConnection("https://github.com/pyros2097/GdxStudio/raw/master/README.md");
		if(httpConn == null)
			return Main.version;
		if(isOK(httpConn)) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
				line = br.readLine();
				br.close();
				httpConn.getInputStream().close();
				httpConn.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return line.substring(10);
	}
	
	public HttpURLConnection openConnection(String url){
		try {
			return (HttpURLConnection) new URL(url).openConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean isOK(HttpURLConnection httpConn){
		try {
			return (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public int getContentLength(String fileURL){
		HttpURLConnection httpConn = openConnection(fileURL);
		int length = getContentLength(httpConn);
		httpConn.disconnect();
		return length;
	}
	
	public int getContentLength(HttpURLConnection httpConn){
		int length = 0;
		if(httpConn == null)
			return length;
		if(isOK(httpConn)) 
			length = httpConn.getContentLength();
		return length;
	}
	
	private boolean checkNewVersion(){
		String[] vals1 = Main.version.split("\\.");
		String[] vals2 = getNewVersion().split("\\.");
		int i=0;
		while(i<vals1.length && i<vals2.length && vals1[i].equals(vals2[i])) {
		  i++;
		}
		if (i<vals1.length && i<vals2.length) {
		    int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
		    if(Integer.signum(diff) != 1)
		    	return true;
		}
		return false;
	}

	void setFileInfo(String name, int size) {
		fieldFileName.setText(name);
		fieldFileSize.setText(String.valueOf(size));
	}
	
	/**
	 * Update the progress bar's state whenever the progress of download changes.
	 */	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("progress")) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		}
	}
	
	/**
	 * Execute file download in a background thread and update the progress. 
	 * @author www.codejava.net
	 *
	 */
	public class AsyncDownloader extends SwingWorker<Void, Void> {
		private static final int BUFFER_SIZE = 4096;	
		private String downloadURL;
		private File saveFile;
		
		public AsyncDownloader(String downloadURL, String fileName) {
			this.downloadURL = downloadURL+fileName;
			saveFile = new File(saveDir+ File.separator + fileName);
			execute();
		}
		
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = -1;
		long totalBytesRead = 0;
		int percentCompleted = 0;
		long fileSize;
		HttpURLConnection conn;
		/**
		 * Executed in background thread
		 */	
		@Override
		protected Void doInBackground() throws Exception {
			try {
				conn = openConnection(downloadURL);
				if(conn == null)
					return null;
				fileSize = getContentLength(conn);
				setFileInfo(saveFile.getName(), getContentLength(conn));
				InputStream inputStream = conn.getInputStream();
				FileOutputStream outputStream = new FileOutputStream(saveFile.getPath());
			
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
					totalBytesRead += bytesRead;
					percentCompleted = (int) (totalBytesRead * 100 / fileSize);
	
					setProgress(percentCompleted);			
				}
	
				outputStream.close();
				inputStream.close();
				conn.disconnect();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(null, "Error downloading file: " + ex.getMessage(),
						"Error", JOptionPane.ERROR_MESSAGE);			
				ex.printStackTrace();
				setProgress(0);
				cancel(true);			
			}
			return null;
		}
	
		/**
		 * Executed in Swing's event dispatching thread
		 */
		@Override
		protected void done() {
			if (!isCancelled()) {
				try {
					Files.copy(saveFile.toPath(), // replace existing files with downloaded files
							new File(workDir+"libs/"+saveFile.getName()).toPath(), 
							StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				JOptionPane.showMessageDialog(null, "File has been downloaded successfully!", "Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}	
	}
}