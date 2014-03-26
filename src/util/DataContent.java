package util;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;

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

	public HashSet<Path> getFileParentPaths() {
		HashSet<Path> parentSet = new HashSet<Path>();

		Iterator<Files> fileItr = files.iterator();

		while (fileItr.hasNext()) {
			Files file = fileItr.next();
			File parent = new File(file.getParent());
			parentSet.add(parent.toPath());
		}
		return parentSet;
	}

	public HashSet<Path> getFolderPaths() {
		HashSet<Path> folderSet = new HashSet<Path>();

		Iterator<Folders> folderItr = folders.iterator();

		while (folderItr.hasNext()) {
			Folders folder = folderItr.next();
			File folderPath = new File(folder.getFolder());
			folderSet.add(folderPath.toPath());
		}
		return folderSet;
	}

	public Folders getFolder(String path) {
		Iterator<Folders> itr = folders.iterator();
		while (itr.hasNext()) {
			Folders folder = itr.next();
			if (folder.getFolder().equals(path)) {
				return folder;
			}
		}
		return null;
	}

	public Files getFile(String path) {
		Iterator<Files> itr = files.iterator();
		while (itr.hasNext()) {
			Files file = itr.next();

			String filePath = file.getParent() + File.separator
					+ file.getFile();
			if (filePath.equals(path)) {
				return file;
			}
		}
		return null;
	}
}
