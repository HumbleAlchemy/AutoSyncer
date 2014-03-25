package eventHandlers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;

public class Clouds {
	private final static String CONFIGURATION_FOLDER = System.getProperty("user.home")
			+ File.separator + ".config" + File.separator + "autosyncer"
			+ File.separator ;
	private final static String CLOUD_INFO_LOCATION = CONFIGURATION_FOLDER+ "clouds.json";
	private final static String USER_INFO_LOCATION = CONFIGURATION_FOLDER+"user.json";
	
	public static void main(String args[]) throws MalformedURLException,
			IOException {
		// Get User ID from USER_INFO_LOCATION
		//TODO
		String jsonresponse = GetClient
				.executeRequest("http://localhost:80/UnifiedCloud/public/remote/update_clouds/1");
		FileWriter file = new FileWriter(CLOUD_INFO_LOCATION);
		file.write(jsonresponse);
		file.flush();
		file.close();
	}
}
