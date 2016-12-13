package cc.ttm.di.common;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileNotFoundException;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.OutputStream;  
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
  
import javax.swing.JOptionPane;  
  
/** 
 * 复制文件或文件夹 
 *  
 * zww 
 */  
public class CopyFileUtil {  
  
    private static String MESSAGE = "";  
  
    /** 
     * 复制单个文件 
     *  
     * @param srcFileName 
     *            待复制的文件名 
     * @param descFileName 
     *            目标文件名 
     * @param overlay 
     *            如果目标文件存在，是否覆盖 
     * @return 如果复制成功返回true，否则返回false 
     */  
    public static boolean copyFile(String srcFileName, String destFileName,  
            boolean overlay) {  
        File srcFile = new File(srcFileName);  
  
        // 判断源文件是否存在  
        if (!srcFile.exists()) {  
            MESSAGE = "源文件：" + srcFileName + "不存在！";  
            JOptionPane.showMessageDialog(null, MESSAGE);  
            return false;  
        } else if (!srcFile.isFile()) {  
            MESSAGE = "复制文件失败，源文件：" + srcFileName + "不是一个文件！";  
            JOptionPane.showMessageDialog(null, MESSAGE);  
            return false;  
        }  
  
        // 判断目标文件是否存在  
        File destFile = new File(destFileName);  
        if (destFile.exists()) {  
            // 如果目标文件存在并允许覆盖  
            if (overlay) {  
                // 删除已经存在的目标文件，无论目标文件是目录还是单个文件  
                new File(destFileName).delete();  
            }  
        } else {  
            // 如果目标文件所在目录不存在，则创建目录  
            if (!destFile.getParentFile().exists()) {  
                // 目标文件所在目录不存在  
                if (!destFile.getParentFile().mkdirs()) {  
                    // 复制文件失败：创建目标文件所在目录失败  
                    return false;  
                }  
            }  
        }  
  
        // 复制文件  
        int byteread = 0; // 读取的字节数  
        InputStream in = null;  
        OutputStream out = null;  
  
        try {  
            in = new FileInputStream(srcFile);  
            out = new FileOutputStream(destFile);  
            byte[] buffer = new byte[1024];  
  
            while ((byteread = in.read(buffer)) != -1) {  
                out.write(buffer, 0, byteread);  
            }  
            return true;  
        } catch (FileNotFoundException e) {  
            return false;  
        } catch (IOException e) {  
            return false;  
        } finally {  
            try {  
                if (out != null)  
                    out.close();  
                if (in != null)  
                    in.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
  
    /** 
     * 复制整个目录的内容 
     *  
     * @param srcDirName 
     *            待复制目录的目录名 
     * @param destDirName 
     *            目标目录名 
     * @param overlay 
     *            如果目标目录存在，是否覆盖 
     * @return 如果复制成功返回true，否则返回false 
     */  
    public static boolean copyDirectory(String srcDirName, String destDirName,  
            boolean overlay) {  
        // 判断源目录是否存在  
        File srcDir = new File(srcDirName);  
        if (!srcDir.exists()) {  
            MESSAGE = "复制目录失败：源目录" + srcDirName + "不存在！";  
            JOptionPane.showMessageDialog(null, MESSAGE);  
            return false;  
        } else if (!srcDir.isDirectory()) {  
            MESSAGE = "复制目录失败：" + srcDirName + "不是目录！";  
            JOptionPane.showMessageDialog(null, MESSAGE);  
            return false;  
        }  
  
        // 如果目标目录名不是以文件分隔符结尾，则加上文件分隔符  
        if (!destDirName.endsWith(File.separator)) {  
            destDirName = destDirName + File.separator;  
        }  
        File destDir = new File(destDirName);  
        // 如果目标文件夹存在  
        if (destDir.exists()) {  
            // 如果允许覆盖则删除已存在的目标目录  
            if (overlay) {  
                new File(destDirName).delete();  
            } else {  
                MESSAGE = "复制目录失败：目的目录" + destDirName + "已存在！";  
                JOptionPane.showMessageDialog(null, MESSAGE);  
                return false;  
            }  
        } else {  
            // 创建目的目录  
            System.out.println("目的目录不存在，准备创建。。。");  
            if (!destDir.mkdirs()) {  
                System.out.println("复制目录失败：创建目的目录失败！");  
                return false;  
            }  
        }  
  
        boolean flag = true;  
        File[] files = srcDir.listFiles();  
        for (int i = 0; i < files.length; i++) {  
            // 复制文件  
            if (files[i].isFile()) {  
                flag = CopyFileUtil.copyFile(files[i].getAbsolutePath(),  
                        destDirName + files[i].getName(), overlay);  
                if (!flag)  
                    break;  
            } else if (files[i].isDirectory()) {  
                flag = CopyFileUtil.copyDirectory(files[i].getAbsolutePath(),  
                        destDirName + files[i].getName(), overlay);  
                if (!flag)  
                    break;  
            }  
        }  
        if (!flag) {  
            MESSAGE = "复制目录" + srcDirName + "至" + destDirName + "失败！";  
            JOptionPane.showMessageDialog(null, MESSAGE);  
            return false;  
        } else {  
            return true;  
        }  
    }  
    //不考虑多线程优化，单线程文件复制最快的方法是(文件越大该方法越有优势，一般比常用方法快30+%):
    private static void nioTransferCopy(File source, File target) throws IOException {  
        FileChannel in = null;  
        FileChannel out = null;  
        FileInputStream inStream = null;  
        FileOutputStream outStream = null;  
        try {  
            inStream = new FileInputStream(source);  
            outStream = new FileOutputStream(target);  
            in = inStream.getChannel();  
            out = outStream.getChannel();  
            in.transferTo(0, in.size(), out);  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
          
            inStream.close();
            in.close();
            outStream.close();
            out.close();
           
        }  
    }  
    
    //如果需要监测复制进度，可以用第二快的方法(留意buffer的大小，对速度有很大影响):
    private static void nioBufferCopy(File source, File target) throws IOException {  
        FileChannel in = null;  
        FileChannel out = null;  
        FileInputStream inStream = null;  
        FileOutputStream outStream = null;  
        try {  
            inStream = new FileInputStream(source);  
            outStream = new FileOutputStream(target);  
            in = inStream.getChannel();  
            out = outStream.getChannel();  
            ByteBuffer buffer = ByteBuffer.allocate(4096);  
            while (in.read(buffer) != -1) {  
                buffer.flip();  
                out.write(buffer);  
                buffer.clear();  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            inStream.close();
            in.close();
            outStream.close();
            out.close();
        }  
    } 
    
    private static void customBufferBufferedStreamCopy(File source, File target) throws IOException {  
        InputStream fis = null;  
        OutputStream fos = null;  
        try {  
            fis = new BufferedInputStream(new FileInputStream(source));  
            fos = new BufferedOutputStream(new FileOutputStream(target));  
            byte[] buf = new byte[4096];  
            int i;  
            while ((i = fis.read(buf)) != -1) {  
                fos.write(buf, 0, i);  
            }  
        }  
        catch (Exception e) {  
            e.printStackTrace();  
        } finally { 
        	fis.close();
        	fos.close();       
        }  
    } 
    
    private static void customBufferStreamCopy(File source, File target) throws IOException {  
        InputStream fis = null;  
        OutputStream fos = null;  
        try {  
            fis = new FileInputStream(source);  
            fos = new FileOutputStream(target);  
            byte[] buf = new byte[4096];  
            int i;  
            while ((i = fis.read(buf)) != -1) {  
                fos.write(buf, 0, i);  
            }  
        }  
        catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
        	fis.close();
        	fos.close();  
        }  
    }
    
    public static void main(String[] args) {  
        String srcDirName = "C:/test/test0/test1";  
        String destDirName = "c:/ttt";  
        CopyFileUtil.copyDirectory(srcDirName, destDirName, true);  
    }  
}  