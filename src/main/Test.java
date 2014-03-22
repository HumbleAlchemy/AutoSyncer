package main;

import java.io.IOException;

import watcher.AutoSyncer;

public class Test {

	public static void main(String[] args) {
		try {
			AutoSyncer syncer = new AutoSyncer();
			String folderPath = "/home/abhishek/Documents/cloud/surbhi";
			//syncer.watch(folderPath);
			String folderPath2 = "/home/abhishek/Documents/cloud/abhi";
			//syncer.watch(folderPath2);
			//syncer.register(folderPath2);
			syncer.watch("chjdc");
		} catch (IOException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}
