package com.example.kernel.khttp.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

public class SystemUtil {
	
	/**
	 * Gets the number of cores available in this device, across all processors.
	 * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
	 * @return The number of cores, or 1 if failed to get result
	 */
	public static int getNumCores() {
	    //Private Class to display only CPU devices in the directory listing
	    class CpuFilter implements FileFilter {
	        @Override
	        public boolean accept(File pathname) {
	            //Check if filename is "cpu", followed by a single digit number
	            if(Pattern.matches("cpu[0-9]", pathname.getName())) {
	                return true;
	            }
	            return false;
	        }      
	    }
	    try {
	        //Get directory containing CPU info
	        File dir = new File("/sys/devices/system/cpu/");
	        //Filter to only list the devices we care about
	        File[] files = dir.listFiles(new CpuFilter());
	        System.out.println("CPU Count->" + files.length);
	        //Return the number of cores (virtual CPU devices)
	        return files.length;
	    } catch(Exception e) {
	    	System.out.println("Get CPU Count->Failed");
	        e.printStackTrace();
	        //Default to return 1 core
	        return 1;
	    }
	}
}
