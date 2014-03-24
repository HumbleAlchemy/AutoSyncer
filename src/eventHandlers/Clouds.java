package eventHandlers;
import java.io.IOException;
import java.net.MalformedURLException;

public class Clouds {
	public static void main(String args[]) {
		try {

			GetClient.executeRequest("http://localhost:80/UnifiedCloud/public/remote/update_clouds/1");
		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();

		}

	}
}
