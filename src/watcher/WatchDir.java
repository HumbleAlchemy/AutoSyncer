package watcher;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Example to watch a directory (or tree) for changes to files.
 */

public class WatchDir {

	private final WatchService watcher;
	private Map<Path, Path> fileMap;
	private Map<Path, Path> folderMap;
	private Path[] addList;
	private Path[] addRecursiveList;
	private Path[] deleteList;

	private final BiMap<WatchKey, Path> keys;

	private boolean trace = false;

	/** 
	 * DEV NOTE: make data.json file in /home/{user}/.config/autosyncer/
	 */
	private final static String CONFIG_FOLDER_LOCATION = System
			.getProperty("user.dir") + File.separator + ".config" + File.separator + "autosyncer";
	private final static String CONFIG_FILE_LOCATION = CONFIG_FOLDER_LOCATION
			+ File.separator + "data.json";

	@SuppressWarnings("unchecked")
	private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}

	/**
	 * Register the given directory with the WatchService
	 */
	private void register(Path dir) throws IOException {
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE,
				ENTRY_MODIFY);

		if (trace) {
			Path prev = keys.get(key);
			if (prev == null) {
				System.out.format("register: %s\n", dir);
			} else {
				if (!dir.equals(prev)) {
					System.out.format("update: %s -> %s\n", prev, dir);
				}
			}
		}
		keys.put(key, dir);
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 */
	private void registerAll(final Path start) throws IOException {
		// register directory and sub-directories
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir,
					BasicFileAttributes attrs) throws IOException {
				register(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	/**
	 * Creates a WatchService and registers the given directory
	 */
	public WatchDir() throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = HashBiMap.create();
	}

	/**
	 * Process all events for keys queued to the watcher
	 * 
	 * @throws IOException
	 */
	public void processEvents() throws IOException {
		for (;;) {

			// wait for key to be signaled
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				return;
			}

			Path dir = keys.get(key);
			if (dir == null) {
				System.err.println("WatchKey not recognized!!");
				continue;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind kind = event.kind();

				// OVERFLOW : Indicates that events might have been lost or
				// discarded. You
				// do not have to register for the OVERFLOW event to receive it.
				if (kind == OVERFLOW) {
					continue;
				}

				// Context for directory entry event is the file name of entry
				WatchEvent<Path> ev = cast(event);
				Path name = ev.context();
				Path child = dir.resolve(name);

				// If the file that has been modified is the config file,
				// then process the contents of the config file
				if (child.toString().equals(CONFIG_FILE_LOCATION)) {
					System.out.println("Config file modified ");
					abhishek();
					// Now that addList, addRecursiveList and deleteList have
					// been initialized,
					// process them to modify map of keys and folders
					modifyMap();
				}
				// print out event
				// Handle event
				System.out.format("%s: %s\n", event.kind().name(), child);
				handleEvent(event);// TODO
			}

			// reset key and remove from set if directory no longer accessible
			boolean valid = key.reset();
			if (!valid) {
				keys.remove(key);
				// all directories are inaccessible, so break the loop
				if (keys.isEmpty()) {
					break;
				}
			}
		}
	}

	private void modifyMap() throws IOException {
		for (Path addPath : addList) {
			register(addPath);
		}

		for (Path addPath : addRecursiveList) {
			registerAll(addPath);
		}
		for (Path deletePath : addList) {
			BiMap<Path, WatchKey> reverseMap = keys.inverse();
			WatchKey key = reverseMap.get(deletePath);
			// Cancel the registration of the folder
			key.cancel();
			// Remove the mapping from BiMap
			// Changes to reverseMap will be reflected in keys also
			reverseMap.remove(deletePath);
		}
	}

	private void handleEvent(WatchEvent event) {

	}
	
	// if directory is created, and watching recursively, then
	// register it and its sub-directories
/*	if (recursive && (kind == ENTRY_CREATE)) {
		try {
			if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
				registerAll(child);
			}
		} catch (IOException x) {
			// ignore to keep sample readable
		}
	}
*/
	public void start() throws IOException {
		// Register the config file
		Path dir = Paths.get(CONFIG_FOLDER_LOCATION);
		register(dir);
		// enable trace after initial registration
		this.trace = true;
		processEvents();
	}

	private void abhishek() {// TODO
		// Read new entries from config file into hash map FileHashMap and
		// FolderHashMap
		
		// Check difference between old and new hash map
		// Create an AddList and DeleteList which are arrays of type Path
		// AddList = List of Paths that are to be added to the watch service
		// DeleteList = List of Paths that are to be deleted from the watch
		// service
		// Initialize AddList and DeleteList of Class
		// Note: Create two separate Lists for folders , one which have to be
		// synced completely with subfolders and other which
		// have to be synced individually without subfolders
	}

}
