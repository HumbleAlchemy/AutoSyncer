package main;

import java.io.IOException;

import watcher.WatchDir;

public class Test {

	public static void main(String[] args) throws IOException {
		WatchDir watcher = new WatchDir();
		try {
			watcher.start();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
