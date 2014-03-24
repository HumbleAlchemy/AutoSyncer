package util;

import java.util.HashSet;
import java.util.List;

public class DataContent {
	
	private HashSet<Folders> folders;
	private HashSet<Files> files;
	
	public HashSet<Folders> getFolders() {
		return folders;
	}
	public void setFolders(HashSet<Folders> folders) {
		this.folders = folders;
	}
	public HashSet<Files> getFiles() {
		return files;
	}
	public void setFiles(HashSet<Files> files) {
		this.files = files;
	}
	
}
