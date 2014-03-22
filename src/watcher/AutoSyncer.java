package watcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

public class AutoSyncer {

	private static WatchService watcher = null;
	private final static String configFolderLocation = System
			.getProperty("user.dir") + File.separator + "config";
	private final static String configFileLocation = configFolderLocation
			+ File.separator + "config.json";

	public AutoSyncer() throws IOException {
		System.out.println(configFileLocation);
		System.out.println(configFolderLocation);
		watcher = FileSystems.getDefault().newWatchService();
		Path configFolder = Paths.get(configFolderLocation);
		configFolder.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
				StandardWatchEventKinds.ENTRY_DELETE,
				StandardWatchEventKinds.ENTRY_MODIFY);
	}

	public void register(String folderPath) throws Exception {
		Path folder = Paths.get(folderPath);
		folder.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
				StandardWatchEventKinds.ENTRY_DELETE,
				StandardWatchEventKinds.ENTRY_MODIFY);
	}

	public void watch(String folderPath) throws Exception {
		WatchKey watchKey;
		try {
			while (true) {

				String folderPath2 = "/home/abhishek/Documents/cloud/dhruv";
				Path mydir1 = Paths.get(folderPath2);
				mydir1.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
						StandardWatchEventKinds.ENTRY_DELETE,
						StandardWatchEventKinds.ENTRY_MODIFY);
				// wait for key to be signaled
				watchKey = watcher.take();
				List<WatchEvent<?>> events = watchKey.pollEvents();

				for (WatchEvent event : events) {
					Path filePath = (Path) event.context();
					System.out.println(filePath.toFile().getAbsolutePath());

					if (filePath.toAbsolutePath().equals(configFileLocation)) {
						System.out.println(true);
						return;
					}
					return;
					/*
					 * if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE)
					 * { System.out.println("Created: " +
					 * event.context().toString()); } if (event.kind() ==
					 * StandardWatchEventKinds.ENTRY_DELETE) {
					 * System.out.println("Delete: " +
					 * event.context().toString()); } if (event.kind() ==
					 * StandardWatchEventKinds.ENTRY_MODIFY) {
					 * System.out.println("Modify: " +
					 * event.context().toString()); } } boolean valid =
					 * watchKey.reset(); // Check the validity of the key, if it
					 * is not valid, exit if (!valid) { break; }
					 */}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private boolean isFolder(String filePath) {
		File file = new File(filePath);
		if (file.isDirectory())
			return true;
		else
			return false;
	}

	private void handleFileCreation() {

	}

	private void handleFileDeletion() {

	}

	private void handleFileModification() {

	}

	private void handleFolderCreation() {

	}

}
