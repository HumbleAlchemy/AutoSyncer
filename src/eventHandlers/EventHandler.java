package eventHandlers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

public class EventHandler {
	private final String PREFIX = "http://localhost:80/UnifiedCloud/public/";
	
	public static void handleEvent(WatchEvent event) {
		if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
			System.out.println("Created: " + event.context().toString());
			
		}
		if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
			System.out.println("Delete: " + event.context().toString());
			
			//delete();
		}
		if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
			System.out.println("Modify: " + event.context().toString());
			
		}
	}
	
	private void delete(String cloudName, String userCloudID, String path) throws MalformedURLException, IOException{
		GetClient.executeRequest(PREFIX+"remote/delete/"+cloudName+"/"+userCloudID+"?path="+path);
	}
	private void upload(String cloudName, String userCloudID, String cloudDestinationPath, String sourceFile) throws UnsupportedEncodingException, IOException{
		PostClient.executeRequest(PREFIX+"remote/upload/"+cloudName+"/"+userCloudID, cloudDestinationPath, sourceFile);
	}
	private void createFolder(String cloudName, String userCloudID, String folderPath) throws MalformedURLException, IOException{
		GetClient.executeRequest(PREFIX+"remote/create_folder/"+cloudName+"/"+userCloudID+"?folderPath="+folderPath);	
	}
}
