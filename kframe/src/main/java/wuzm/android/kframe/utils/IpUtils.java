package wuzm.android.kframe.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.conn.util.InetAddressUtils;

/**
 * 
 * @author wuzm
 * @version 0.1beta
 * @since 2014/10/17
 *
 */
public class IpUtils {
	
//	/**
//	 * 获得本机十六进制字符串格式IP地址，例如 C0A80166
//	 * @return
//	 */
//	public static String getIpAddressHexString() {
//		return DataTypeConverter.bytesToHexString(getIpByteAddress(getLocalIpV4Address()));
//	}
	
	/**
	 * 获得本机ip地址的byte表现形式
	 * @param ip
	 * @return
	 */
	public static byte[] getIpByteAddress(String ip) {
		byte[] binIP = new byte[4]; 
		try {
			  String[] strs = ip.split("\\.");
			  for(int i=0;i<strs.length;i++){
			   binIP[i] = (byte) Integer.parseInt(strs[i]);
			  }
		}catch(NullPointerException e) {
			e.printStackTrace();
			return null;
		}
		  return binIP;
	}
	
	/**
	 * 获得本机ip地址的十进制字符串表现格式
	 * @return
	 */
	public static String getLocalIpV4Address() {
	       try {  
	            String ipv4;  
	            ArrayList<NetworkInterface>  nilist = Collections.list(NetworkInterface.getNetworkInterfaces());  
	            for (NetworkInterface ni: nilist)   
	            {  
	                ArrayList<InetAddress>  ialist = Collections.list(ni.getInetAddresses());  
	                for (InetAddress address: ialist){  
	                    if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4=address.getHostAddress()))   
	                    {   
	                        return ipv4;  
	                    }  
	                }  
	            }  
	        } catch (SocketException ex) { 
	        	ex.printStackTrace();
	        }  
	        return null;  
	}

}
