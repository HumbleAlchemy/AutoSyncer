package watcher;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;

import util.DataContent;
import util.JSONUtilities;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import eventHandlers.EventHandler;

/**
 * Example to watch a directory (or tree) for changes to files.
 */

public class WatchDir {

	private final WatchService watcher;
	private Path[] addList;
	private Path[] addRecursiveList;
	private Path[] deleteList;
	private Path[] deleteRecursiveList;
	private DataContent dc = null;
	private final BiMap<WatchKey, Path> folderKeys;
	private final BiMap<WatchKey, Path> fileKeys;
	/**
	 * DEV NOTE: make data.json file in /home/{user}/.config/autosyncer/
	 */

	private final static String CONFIG_FOLDER_LOCATION = System
			.getProperty("user.home")
			+ File.separator
			+ ".config"
			+ File.separator + "autosyncer";
	private final static String CONFIG_FILE_LOCATION = CONFIG_FOLDER_LOCATION
			+ File.separator + "data.json";

	public WatchDir() throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		this.folderKeys = HashBiMap.create();
		this.fileKeys = HashBiMap.create();
	}

	@SuppressWarnings("unchecked")
	private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}

	/**
	 * Register the given directory with the WatchService
	 */
	private void registerFileParent(Path parentDir) throws IOException {
		WatchKey key = parentDir.register(watcher, ENTRY_CREATE, ENTRY_DELETE,
				ENTRY_MODIFY);

		fileKeys.put(key, parentDir);
	}

	private void registerFolder(Path dir) throws IOException {
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE,
				ENTRY_MODIFY);

		folderKeys.put(key, dir);

	}

	/**
	 * Process all events for keys queued to the watcher
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void processEvents() throws IOException, InterruptedException {
		Path dir;
		System.out.println("processEvents started");
		for (;;) {

			// wait for key to be signaled
			WatchKey key;
			key = watcher.take();
			System.out.println("key detected");
			
			if ((dir = folderKeys.get(key)) != null) {
				// key is a part of folderKeys
				System.out.println("key is part of folder keys");
				
				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent.Kind kind = event.kind();
					Path modified = getModified(event, dir);
					System.out.println("folderKeys Modified = " + modified);
					// OVERFLOW : Indicates that events might have been lost
					// or
					// discarded. You
					// do not have to register for the OVERFLOW event to
					// receive it.
					if (kind == OVERFLOW) {
						continue;
					} else {// Some other event occurred

						if (isFolder(modified.toString())) {
							// change to a folder
							if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
								// a folder has been created TODO
							} else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
								// a folder has been deleted TODO
							} else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
								// a folder has been modified TODO
							}
						} else {
							// change to a file
							if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
								// a file has been created
								System.out.println("A file has been created in the folder "+modified.toString());
								
								util.Folders folder = dc.getFolder(dir
										.toString());// CHECK
								if (folder == null) {
									continue;
								} else {
									// upload that file
//									EventHandler.upload(folder.getName(),
//											folder.getUser_cloudID(), "/"
//													+ modified.getParent()
//															.getFileName()
//															.toString(),
//
//											modified.toAbsolutePath()
//													.toString());
								}
							} else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
								// a file has been deleted
								util.Folders folder = dc.getFolder(dir
										.toString());
								if (folder == null) {
									continue;
								} else {
									// delete that file
									EventHandler.delete(folder.getName(),
											folder.getUser_cloudID(), "/"
													+ modified.getParent()
															.getFileName()
															.toString()+"/"+
													 modified.getFileName()
															.toString());
								}

							} else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
								// a file has been modified
								util.Folders folder = dc.getFolder(dir
										.toString());
								if (folder == null) {
									continue;
								} else {
									// delete that file
//									EventHandler.delete(folder.getName(),
//											folder.getUser_cloudID(), "/"
//													+ modified.getParent()
//															.getFileName()
//															.toString()+"/"
//													+ modified.getFileName()
//															.toString());
									// upload that file again
									EventHandler.upload(folder.getName(),
											folder.getUser_cloudID(), "/"
													+ modified.getParent()
															.getFileName()
															.toString(),
											modified.toAbsolutePath()
													.toString());

								}
							}// Entry_modify ends
						}// Change to file ends
					}// event other than Overflow occurred ends
				}// for ends
					// reset key and remove from set if directory no longer
					// accessible
				boolean valid = key.reset();
				if (!valid) {
					folderKeys.remove(key);
				}

			} else if ((dir = fileKeys.get(key)) != null) {
				// key is a part of fileKeys
				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent.Kind kind = event.kind();
					Path modified = getModified(event, dir);
					System.out.println("fileKeys Modified = " + modified);

					// OVERFLOW : Indicates that events might have been lost
					// or
					// discarded. You
					// do not have to register for the OVERFLOW event to
					// receive it.
					if (kind == OVERFLOW) {
						continue;
					} else {// Some other event occurred

						if (isFolder(modified.toString())) {
							// change to a folder
							// we don't care about any folder in
							// fileParentLists
							// ignore
						} else {
							// change to a file
							// a file has been changed , we need to check
							// first if the file is our config file
							// special processing is required for that
							if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
								// a file has been created
								// ignore
							} else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
								// a file has been deleted
								util.Files file = dc.getFile(modified
										.toString());
								if (file == null) {
									// the file deleted is none of our
									// business,
									// so ignore that file
									// if the file had been synced, file
									// object won't be null
									// this file is not under watch
									continue;
								} else if (file.getFullPath().equals(
										CONFIG_FILE_LOCATION)) {
									// check if config file has been deleted
									// , if yes then exit
									System.out
											.println("Config file has been deleted ");
									System.exit(-2);
								} else {
									// delete that file from the cloud
									EventHandler.delete(file.getName(),
											file.getUser_cloudID(), "/"
													+ modified.getParent()
															.getFileName()
															.toString()+"/"
													+ modified.getFileName()
															.toString());
								}
							} else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
								// a file has been modified
								util.Files file = dc.getFile(modified
										.toString());
								if (file == null) {
									// the file modified is none of our
									// business,
									// so ignore that file
									// if the file had been synced, file
									// object won't be null
									// this file is not under watch
									continue;
								} else if (file.getFullPath().equals(
										CONFIG_FILE_LOCATION)) {
									// check if config file has been
									// modified,
									// it requires special processing
									System.out.println("Config file modified ");
									createNewLists();
									// Now that addList, addRecursiveList
									// and deleteList
									// have
									// been initialized,
									// process them to modify map of keys
									// and folders
									modifyMap();

								} else {
									// file under watch has been modified
									// delete that file
//									EventHandler.delete(file.getName(),
//											file.getUser_cloudID(), "/"
//													+ modified.getFileName()
//															.toString());
									// upload that file again
									EventHandler.upload(file.getName(),
											file.getUser_cloudID(), "/",
											modified.toString());

								}
							}// Entry_modify ends
						}// Change to file ends
					}// event other than Overflow occurred ends
				}// for ends

				boolean valid = key.reset();
				if (!valid) {
					fileKeys.remove(key);
				}

			}// else if for fileKeys ends
			else {// key is not part of fileKeys and folderKeys both
					// key is invalid
				System.err.println("WatchKey not recognized!!");
				continue;

			}
		}// infinite for loop ends

	}// method ends

	private void modifyMap() throws IOException {
		for (Path addPath : addList) {
			System.out.println("Adding file parent to map"+addPath.toString());
			registerFileParent(addPath);
		}

		for (Path addPath : addRecursiveList) {
			System.out.println("Adding folder to map "+addPath.toString());
			registerFolder(addPath);
		}
		for (Path deletePath : deleteList) {
			
			BiMap<Path, WatchKey> reverseMap = fileKeys.inverse();
			WatchKey key = reverseMap.get(deletePath);
			// Cancel the registration of the folder
			if(!fileKeys.get(key).toString().equals(CONFIG_FOLDER_LOCATION) ){
					System.out.println("Deleting file parent to map "+deletePath.toString());
					key.cancel();
					// Remove the mapping from BiMap
					// Changes to reverseMap will be reflected in keys also
					reverseMap.remove(deletePath);
			}
			
		}

		for (Path deletePath : deleteRecursiveList) {
			System.out.println("Deleting folder to map "+deletePath.toString());

			BiMap<Path, WatchKey> reverseMap = folderKeys.inverse();
			WatchKey key = reverseMap.get(deletePath);
			// Cancel the registration of the folder
			key.cancel();
			// Remove the mapping from BiMap
			// Changes to reverseMap will be reflected in keys also
			reverseMap.remove(deletePath);
		}

	}

	private Path getModified(WatchEvent event, Path dir) {
		WatchEvent<Path> ev = cast(event);
		Path name = ev.context();
		Path child = dir.resolve(name);
		return child;

	}

	private boolean isFolder(String filePath) {
		File file = new File(filePath);
		if (file.isDirectory())
			return true;
		else
			return false;
	}

	public void start() throws IOException, InterruptedException {
		
		// Register the config file
		Path dir = Paths.get(CONFIG_FOLDER_LOCATION);
		registerFileParent(dir);
		createNewLists();
		modifyMap();
		// enable trace after initial registration
		processEvents();
	}

	public void createNewLists() throws IOException {
		// Read new entries from config file into hash map FileHashMap and
		// FolderHashMap
		dc = JSONUtilities.JsonToMap();
		// HashSet<Folders> folders = dc.getFolders();
		// HashSet<util.Files> files = dc.getFiles();
		HashSet<Path> jsonFolderSet = dc.getFolderPaths();// add recursive
		HashSet<Path> jsonFileParentSet = dc.getFileParentPaths();// add list

		// convert BiMap valueSet to HashSet
		// watchFolderSet = Key set of folders
		// watchFileSet = Key set of files
		// Mapping the values of keys to respective hash sets
		HashSet<Path> watchFolderSet = new HashSet<Path>(folderKeys.values());
		HashSet<Path> watchFileSet = new HashSet<Path>(fileKeys.values());

		// ============================== addList==================
		// Check difference between old and new hash map for folder
		SetView<Path> addListView = Sets.difference(jsonFileParentSet,
				watchFileSet);

		// delete old entries from addList

		addList = null;
		if (addListView != null)
			addList = addListView.toArray(new Path[addListView.size()]);

		// ============================== addRecursiveList==============
		// Check difference between old and new hash map
		SetView<Path> addRecursiveListView = Sets.difference(jsonFolderSet,
				watchFolderSet);

		// delete old entries from addRecursiveList
		addRecursiveList = null;
		if (addRecursiveListView != null)
			addRecursiveList = addRecursiveListView
					.toArray(new Path[addRecursiveListView.size()]);

		// ============================== DeleteList=====================
		// Check difference between old and new hash map
		SetView<Path> deleteListView = Sets.difference(watchFileSet,
				jsonFileParentSet);

		// delete old entries from deleteList
		deleteList = null;
		if (deleteListView != null)

			deleteList = deleteListView
					.toArray(new Path[deleteListView.size()]);

		// ========================DeleteRecursiveList=====================
		// Check difference between old and new hash map

		SetView<Path> deleteRecursiveListView = Sets.difference(watchFolderSet,
				jsonFolderSet);

		// delete old entries from deleteRecursiveList
		deleteRecursiveList = null;
		if (deleteRecursiveListView != null)
			deleteRecursiveList = deleteRecursiveListView
					.toArray(new Path[deleteRecursiveListView.size()]);

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
