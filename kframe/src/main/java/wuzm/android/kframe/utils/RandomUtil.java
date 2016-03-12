package wuzm.android.kframe.utils;

import android.util.Log;

import java.util.Random;

/**
 * 
 * @author wuzm
 * @version 0.1beta
 * @since 2014/10/17
 *
 */
public class RandomUtil {
	
	public int getRandomInt() {
		return new Random().nextInt();
	}
	
	/**
	 * 
	 * @param min
	 * @param max
	 * @return range from min to max
	 */
	public int getRandomInt (int min,int max) {
		
		if(min < 0 || max < 0) {
			Log.e("getRandomInt","max and min must bigger than 0");
			return 0;
		}
		
		if(max - min < 1) {
			Log.e("getRandomInt","max - min must bigger than 1");
			return 0;
		}
		
		return new Random().nextInt(max+ 1 - min) + min;
	}
	
	/**
	 * 
	 * @return range from 0 to 1 , not include 1
	 */
	public float getRandomFloat() {
		return new Random().nextFloat();
	}
	
	/**
	 * 
	 * @return range from 0 to 1 , not include 1
	 */
	public double getRandomDouble() {
		return new Random().nextDouble();
	}
	
	public long getRandomLong() {
		return new Random().nextLong();
	}
	
//	public long getRandomLong(long min) {
//		Random rd = new Random();
//		return rd.nextLong() + min;
//	}
	
	public boolean getRandomBoolean() {
		return new Random().nextBoolean();
	}

}
