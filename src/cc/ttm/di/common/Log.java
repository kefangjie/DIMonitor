package cc.ttm.di.common;

import java.io.FileNotFoundException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log {  
	 private static Logger loger;   
	 
	 private static Logger newInstance() throws FileNotFoundException{
		  //获得日志类loger的实�? 
		  loger=Logger.getLogger(Log.class);  
		  //loger�?��的配置文件路�?
		  PropertyConfigurator.configure(System.getProperty("user.dir") + "/config/log4j.properties");  
		  return loger;
	 }
	   
	 public static Logger getLoger() throws FileNotFoundException{  
	  if(loger!=null)  
	   return loger; 
	  else  
	   return newInstance();  
	 } 
}  