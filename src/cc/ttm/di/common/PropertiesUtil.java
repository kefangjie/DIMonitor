package cc.ttm.di.common;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;


/**
* @author
* @version
*/
public class PropertiesUtil {
	//属性文件的路径
    static String profilepath=System.getProperty("user.dir") + "/config/DIConfig.properties";
    /**
    * 采用静态方法
    */
    private static Properties props = new Properties();
    static {
        try {
            props.load(new FileInputStream(profilepath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {       
            System.exit(-1);
        }
    }

    /**
    * 读取属性文件中相应键的值
    * @param key
    *            主键
    * @return String
    */
    public static String getKeyValue(String key) {
    	String result =null;
    	if(props.containsKey(key))
    		result = props.getProperty(key);
    	
         return  result;
    }

    /**
    * 根据主键key读取主键的值value
    * @param filePath 属性文件路径
    * @param key 键名
    */
    public static String readValue(String filePath, String key) {
        Properties props = new Properties();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(
                    filePath));
            props.load(in);
            String value = props.getProperty(key);   
            in.close();
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            
            return null;
        }
    }
   
    /**
    * 更新（或插入）一对properties信息(主键及其键值)
    * 如果该主键已经存在，更新该主键的值；
    * 如果该主键不存在，则插件一对键值。
    * @param keyname 键名
    * @param keyvalue 键值
    */
    public static void writeProperties(String keyname,String keyvalue) {       
        try {
            // 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。
            // 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
        	
        	OutputStream fos = new FileOutputStream(profilepath);
            props.setProperty(keyname, keyvalue);
            // 以适合使用 load 方法加载到 Properties 表中的格式，
            // 将此 Properties 表中的属性列表（键和元素对）写入输出流
            props.store(fos, "Update '" + keyname + "' value");
          
            fos.close();
        } catch (IOException e) {
        	  
        	  e.printStackTrace();
        	  System.err.println(keyname+"属性文件更新错误"+keyvalue+" "+e.getMessage());
        }
    }

    /**
    * 更新properties文件的键值对
    * 如果该主键已经存在，更新该主键的值；
    * 如果该主键不存在，则插件一对键值。
    * @param keyname 键名
    * @param keyvalue 键值
    */
    public static void updateProperties(String keyname,String keyvalue) {
        try {
            props.load(new FileInputStream(profilepath));
           
            // 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。
            // 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
            OutputStream fos = new FileOutputStream(profilepath);//true表示追加打开         
         
            props.setProperty(keyname, keyvalue);
            // 以适合使用 load 方法加载到 Properties 表中的格式，
            // 将此 Properties 表中的属性列表（键和元素对）写入输出流
            props.store(fos, "Update '" + keyname + "' value");
            fos.close();
        } catch (IOException e) {
         	  e.printStackTrace();
        	  System.err.println(keyname+"属性文件更新错误"+keyvalue+" "+e.getMessage());
        }
    }
  
    //娴嬭瘯浠ｇ爜
    public  void main(String[] args) {
        readValue("mail.properties", "MAIL_SERVER_PASSWORD");
        writeProperties("MAIL_SERVER_INCOMING", "327@ttmtch.com");       
        System.out.println("操作完成");
    }
} 
