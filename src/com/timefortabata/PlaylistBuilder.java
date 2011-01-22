package com.timefortabata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlaylistBuilder {
	
	public static List<File> buildPlayList(String filename) 
	   throws IOException, FileNotFoundException {
		ArrayList<File> playList = new ArrayList<File>();

		if(filename.length() > 0){
			File file = new File(filename);

			if(file.isFile()){
				if(filename.endsWith(".mp3")){
					playList.add(file);
				}
				else if(filename.endsWith(".m3u")){
					readM3uFile(file, playList);
				}
				else{
					throw new IOException("Illegal file type");
				}
			}
			else if(file.isDirectory()){
				for(File f : file.listFiles(new FileNameEndingFilter(".mp3"))){
					playList.add(f);
				}
			}
			else{
				throw new FileNotFoundException(filename);
			}
		}
		
		return playList;
	}
	
	private static void readM3uFile(File m3uFile, List<File> playList)
	    throws FileNotFoundException, IOException {
		BufferedReader m3uInput =  new BufferedReader(new FileReader(m3uFile));
		String line;
		while((line = m3uInput.readLine()) != null){
			line = line.trim();
			if(line.startsWith("#")){
				// Comment or extended information, do nothing
			}
			else if(line.endsWith(".mp3")){
				if(line.startsWith("/")){
					// Absolute path
					File mp3File = new File(line);
					if(mp3File.isFile()){
						playList.add(mp3File);
					}
					else{
						throw new FileNotFoundException(line);
					}		
				}
				else{
					// Relative path
					String parentPath = m3uFile.getParent();
					if(parentPath == null){
						parentPath = "";
					}
					parentPath += "/";
					
					File mp3File = new File(parentPath + line);
					if(mp3File.isFile()){
						playList.add(mp3File);
					}
					else{
						throw new FileNotFoundException(parentPath + line);
					}		
				}
			}
			else{
				// Unknown line type, ignore it
			}
		}
		m3uInput.close();
	}

}
