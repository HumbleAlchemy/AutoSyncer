package eventHandlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class PostClient {

	public static void executeRequest(String url, String cloudDestinationPath,
			String sourceFile) throws UnsupportedEncodingException, IOException {
		File fileToUpload = new File(sourceFile);
		String boundary = Long.toHexString(System.currentTimeMillis());
		URLConnection connection = new URL(url).openConnection();
		connection.setDoOutput(true); // This sets request method to POST.
		connection.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + boundary);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new OutputStreamWriter(
					connection.getOutputStream(), "UTF-8"));

			writer.println("--" + boundary);
			writer.println("Content-Disposition: form-data; name=\"cloudDestinationPath\"");
			writer.println("Content-Type: text/plain; charset=UTF-8");
			writer.println();
			writer.println(cloudDestinationPath);

			writer.println("--" + boundary);
			writer.println("Content-Disposition: form-data; name=\"file\"; filename=\""
					+ fileToUpload.getName() + "\"");
			writer.println("Content-Type: text/plain; charset=UTF-8");
			writer.println();
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(fileToUpload), "UTF-8"));
				for (String line; (line = reader.readLine()) != null;) {
					writer.println(line);
				}
			} finally {
				if (reader != null)
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}

			writer.println("--" + boundary + "--");
		} finally {
			if (writer != null)
				writer.close();
		}

		// Connection is lazily executed whenever you request any status.
		int responseCode = ((HttpURLConnection) connection).getResponseCode();
		System.out.println(responseCode); // Should be 200

	}

}