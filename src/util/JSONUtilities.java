package util;
/**
 * @author Abhishek Nair.
 * JSON utilities to convert JSON string to LinkedHashMap and vice versa.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;


public class JSONUtilities {
	
	private final static String CONFIG_FOLDER_LOCATION = "/home" + File.separator + "surbhi" + File.separator + ".config" + File.separator + "autosyncer";
	private final static String CONFIG_FILE_LOCATION = CONFIG_FOLDER_LOCATION
			+ File.separator + "data.json";

	/**
	 * function converts JSON to Map
	 * @param JSONString : String
	 * @return Map<String,ArrayList<String>>
	 */
	public static DataContent JsonToMap(String JSONString) {
		//Map<String,DataContent> myMap= new HashMap<String,DataContent>();
		DataContent myMap = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			 
			//convert JSON string to Map
			myMap = mapper.readValue(JSONString, DataContent.class);
	 
			System.out.println(myMap);
	 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return myMap;
	}
	
	public static String readFile(String fileName) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}
	
	 public static void main(String[] args) {
		
		String JSONString;
		try {
			JSONString = readFile(CONFIG_FILE_LOCATION);
			System.out.println(JSONString);
			DataContent myMap = JsonToMap(JSONString);
			System.out.println("folder: " + ((Folders) myMap.getFolders().toArray()[0]).getFolder());
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}
	
}
