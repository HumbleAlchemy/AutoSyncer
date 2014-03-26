package eventHandlers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

public class EventHandler {
	private static final String PREFIX = "http://localhost:80/UnifiedCloud/public/";

	public static void delete(String cloudName, String userCloudID, String path)
			throws MalformedURLException, IOException {
		System.out.println("Event Handler ..deleting "+path);
		GetClient.executeRequest(PREFIX + "remote/delete/" + cloudName + "/"
				+ userCloudID + "?path=" + path);
	}

	public static void upload(String cloudName, String userCloudID,
			String cloudDestinationPath, String sourceFile)
			throws UnsupportedEncodingException, IOException {
		System.out.println("Event Handler ..uploading"+sourceFile+" to "+cloudDestinationPath);
		PostClient.executeRequest(PREFIX + "remote/upload/" + cloudName + "/"
				+ userCloudID, cloudDestinationPath, sourceFile);
	}

	public static void createFolder(String cloudName, String userCloudID,
			String folderPath) throws MalformedURLException, IOException {
		System.out.println("Event Handler ..creating folder"+folderPath);
		GetClient.executeRequest(PREFIX + "remote/create_folder/" + cloudName
				+ "/" + userCloudID + "?folderPath=" + folderPath);
	}
}
