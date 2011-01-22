package com.timefortabata;

import java.io.File;
import java.io.FilenameFilter;

public class FileNameEndingFilter implements FilenameFilter {

	private String ending;

	public FileNameEndingFilter(String e){
		ending = e;
	}
	
	public boolean accept(File dir, String filename) {
		if(filename.endsWith(ending)){
			return true;
		}
		else{
			return false;
		}
	}
}
