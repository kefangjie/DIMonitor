package cc.ttm.di.ServersImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import cc.ttm.di.common.DBOptionUtil;
import cc.ttm.di.common.DateUtil;

import cc.ttm.di.common.Keys;

import cc.ttm.di.mainform.MainForm;
import cc.ttm.di.model.StdTableModel;

public class WorkThread implements Runnable{
	
	
	ThreadLocal<WorkParam> studenThreadLocal = new ThreadLocal<WorkParam>();
	
    //子线程记数器,记载着运行的线程数

    private CountDownLatch runningThreadNum;
   
	
	public SimpleDateFormat sdf ;
	 
	private WorkParam wp ;
	
    private String TargetDirectory ;
   
    private File  targetFile ,sourceFile;    
	
	 
    
	
	public WorkThread (CountDownLatch runningThreadNum ){
		   super();
		   
		   this.runningThreadNum= runningThreadNum;
		   
	}
	
	public WorkThread(WorkParam wp,CountDownLatch runningThreadNum){		
		super();
		 this.runningThreadNum= runningThreadNum;
		this.wp = wp;	
		studenThreadLocal.set(wp);
		new Thread(this,this.wp.getMachineID()).start();
	}
	
    private WorkParam getWorkParam() {
    	WorkParam wp2 = studenThreadLocal.get();
        if (wp2 == null ) {

        	wp2 = new WorkParam(this.wp.getTableModel(),this.wp.getSourceDirectory(),this.wp.getTargetDirectory(),this.wp.getMachineID(),
        			            this.wp.getCheckDate(),this.wp.getLastCheckDate(),this.wp.getCurrRow(),this.wp.getLastCounter(),
        			            this.wp.getLastCheckDate(),this.wp.getFormTable(),this.wp.getFormName(),this.wp.getFormCols(),
        			            this.wp.getFormID(),this.wp.getfDatePOS(),this.wp.getFormCode(),this.wp.getCellsPos(),this.wp.getDivCode(),this.wp.getToday());
            studenThreadLocal.set(wp2);
        }  
        return wp2;
    }
	
	@Override
	public void run() {
	
	   	synchronized(this){
			 wp = getWorkParam(); 
			// MainForm.showLog("线程:"+Thread.currentThread().getName()+" 开始工作");
			// DIFileTransImpl.WriteLog("线程:"+Thread.currentThread().getName()+" 开始工作");
			 
			 int between_days =0 ;
			 String FileName = this.wp.getTableModel().getValueAt(this.wp.getCurrRow(),5).toString().toLowerCase().trim(),
					FileFormat = this.wp.getTableModel().getValueAt(this.wp.getCurrRow(),5).toString().toLowerCase().trim();
			 String FileType="";
			 MainForm.showLog("Today:"+wp.getToday());
			 if(!"".equals(this.wp.getTableModel().getValueAt(this.wp.getCurrRow(),6).toString()) &&  
				this.wp.getTableModel().getValueAt(this.wp.getCurrRow(),6).toString() !=null){	
				 
				  between_days =  DateUtil.daysBetween(wp.getTableModel().getValueAt(wp.getCurrRow(),6).toString().substring(0, 10),wp.getToday());
				  MainForm.showLog("between_days:"+between_days);	
				  for(int m =between_days ; m>=0;m--){
					 // MainForm.showLog("m:"+m);
					  sdf = new SimpleDateFormat(Keys.DATE_FORMAT7);
					  FileName = DateUtil.AddDateTime(sdf.format(new Date()), 0-m ,5 ,Keys.DATE_FORMAT7);
					 // MainForm.showLog("m:"+m);
					 
					  wp.setCheckDate(FileName);	
					  
					  if(FileFormat.startsWith(Keys.DATE_FORMAT4)){ 
					      FileType = FileFormat.replace(Keys.DATE_FORMAT4,"");
					      FileName = FileName + FileType;
					  }else if(FileFormat.startsWith(Keys.DATE_FORMAT5)){
						  FileType = FileFormat.replace(Keys.DATE_FORMAT5,"");
						  FileName = FileName.substring(2) + FileType;
					  }	
					
					 // sourceFile = new File(wp.getSourceDirectory()+File.separator+FileName);				
					
					  wp.setSourceFile(wp.getSourceDirectory()+File.separator+FileName);	
				
					//  try {
						 this.ProcessingFile(this.wp.getTableModel(), wp);
						 //Thread.sleep(10);
					// } catch (InterruptedException e) {
						// TODO Auto-generated catch block
			        //	e.printStackTrace();
					 //}	
				  } 
				
			 }else {
				  if(FileName.startsWith(Keys.DATE_FORMAT4)){ 							     
				      FileName = FileName.replace(Keys.DATE_FORMAT4, sdf.format(new Date()));
				  }else if(FileName.startsWith(Keys.DATE_FORMAT5)){
					  FileName = FileName.replace(Keys.DATE_FORMAT5, sdf.format(new Date()).substring(2));
				  }		
				  					
				 wp.setCheckDate(sdf.format(new Date()));
				 wp.setSourceFile(wp.getSourceDirectory()+File.separator+FileName);	
				 //sourceFile = new File(wp.getSourceDirectory());
				 
				 
				// try {
					 this.ProcessingFile(this.wp.getTableModel(), wp);
				//	Thread.sleep(1000);
				//} catch (InterruptedException e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
				//}
				 

			 } //文件格式处理	

			// MainForm.showLog("线程:"+Thread.currentThread().getName()+" "+" 结束工作");
			// DIFileTransImpl.WriteLog("线程:"+Thread.currentThread().getName()+" "+" 结束工作");
		     
			 runningThreadNum.countDown();
	   }
	}
	
	private  void ProcessingFile(StdTableModel tableModel,WorkParam wp){
		int UpLoad_Flage =0;	
		try{		
		
			 if(!"".equals(wp.getSourceFile()) && wp.getSourceFile() !=null){
			     sourceFile = new File(wp.getSourceFile()); 
			 }
			 if(sourceFile.exists()){	
				 sdf = new SimpleDateFormat(Keys.DATE_TIME_FORMAT2);
				 
				 String LastDate = sdf.format(new Date(sourceFile.lastModified()));
				 String checkDate =   wp.getCheckDate();
				
				
				 
				 String LastTime  =  LastDate.substring(11);
				 LastDate = LastDate .substring(1,10);
				 
				 LastDate = LastDate.replaceAll("/", "");
				 if (Integer.parseInt(LastDate) > Integer.parseInt(checkDate)){
					 LastDate = checkDate.substring(1, 4) +"/" + checkDate.substring(5, 6) +"/"+ checkDate.substring(7, 8) +" "+LastTime.trim();
				
					 wp.setLastDate(LastDate);
				 }else{
				    
				     wp.setLastDate(sdf.format(new Date(sourceFile.lastModified())));
				 }	
				 
			
				
				 if(tableModel.getValueAt(wp.getCurrRow(),6).toString().equals( wp.getLastDate())){
					 DIFileTransImpl.WriteLog(sourceFile.getName()+"已上传,最后上传时间:"+ wp.getLastDate());
					 MainForm.showLog(sourceFile.getName()+",最后上传时间:"+ wp.getLastDate());
					if( sourceFile.delete()){
						 tableModel.setValueAt("监控中", wp.getCurrRow(), 3);
						 DIFileTransImpl.WriteLog("删除重复文件:"+sourceFile.getName());
						 MainForm.showLog("删除重复文件:"+sourceFile.getName());
					}
				 }else{
					 TargetDirectory = wp.getTargetDirectory()+File.separator+sourceFile.getName(); 
					 targetFile = new File(TargetDirectory);
					 
		
					 
					 tableModel.setValueAt("正在上传", wp.getCurrRow(), 3);
					 DIFileTransImpl.WriteLog("正在上传文件:"+sourceFile.getName()+" 文件大小:");
					 MainForm.showLog("正在上传文件:"+sourceFile.getName()+" 文件大小:");
					 
					// UpLoad_Flage = UpLoadFileforChannel(sourceFile,targetFile);
					 UpLoad_Flage = customBufferStreamCopy(sourceFile,targetFile);
					 
					 
					while(true){					
						  if(UpLoad_Flage ==1){									 
							 if(targetFile.exists() && targetFile.isFile()){ 
								 UpLoad_Flage = 0;
								 DIFileTransImpl.WriteLog("文件:"+sourceFile.getName()+" 已上传完");
								 MainForm.showLog("文件:"+sourceFile.getName()+" 已上传完");								
								 
								 tableModel.setValueAt("已上传待导入", wp.getCurrRow(), 3);
								 ImpDataForFile(tableModel,targetFile,wp);
								// TimeUnit.MILLISECONDS.sleep(1000);
								 break;
							 }
						   }else if (UpLoad_Flage == 9){	
							   break;
						   }else{							   
							   TimeUnit.MILLISECONDS.sleep(1000);
						   }
					 }
				}
				 
			 }else{
				 DIFileTransImpl.WriteLog("文件不存在:"+wp.getSourceFile()); 
			 }
		 }catch (Exception e) {				
				e.printStackTrace();					
				DIFileTransImpl.WriteLog(e.getMessage()+" 文件处理异常:"+sourceFile.getPath());	
				MainForm.showLog("文件处理异常:"+sourceFile.getPath());
		 }
	}

	private  int ImpDataForFile(StdTableModel tableModel,File impFile,WorkParam wp) throws Exception{
		PreparedStatement ips=null;
		String Massege="" ,CurrStatus="";
		Connection conn=DBOptionUtil.getThreadLocalConnection();
		int count = 0;
		double start  = System.currentTimeMillis() ; 
		String  lastCounter = wp.getLastCounter();
		String  LastCheckDate = wp.getLastCheckDate(); 
		try{			
			lastCounter = (lastCounter==null || "".equals(lastCounter))?"0":lastCounter;
			
			tableModel.setValueAt(("导入中"), wp.getCurrRow(), 3); 
		
			String insertSql = insertSQL(wp.getFormTable(),  wp.getFormCols(), wp.getFormID(), wp.getfDatePOS());
			ips= conn.prepareStatement(insertSql);
			MainForm.showLog("正在解析文件:"+impFile.getName());
		    //CsvUtil csv = new CsvUtil(impFile);			
		    AnalysisCVSFileImpl  analysisCVSFile = new  AnalysisCVSFileImpl();		    
			//Massege = analysisCVSFile.ImpDIProdDataForCSV(ips,wp.getFormCode(),csv,wp.getCellsPos(),wp.getCheckDate(),LastCheckDate, lastCounter,wp.getfDatePOS(),wp.getDivCode());
		    
		    Massege = analysisCVSFile.ParserCSVWithDIFile(ips,wp.getFormCode(),impFile.getPath(),wp.getCellsPos(),wp.getCheckDate(),LastCheckDate, lastCounter,wp.getfDatePOS(),wp.getDivCode());
			
		    if(Massege.indexOf(";") >0){
			    count =Integer.parseInt(Massege.substring(0, Massege.indexOf(";")));
			    Massege =Massege.substring(Massege.indexOf(";")+1);
			    
			    lastCounter =Massege.substring(0, Massege.indexOf(";"));
			    
			    LastCheckDate =Massege.substring(Massege.indexOf(";")+1);
			}
			if(count>=1){	
				if(count %1000 !=0){
				   ips.executeBatch();
				}else{
				   ips.executeBatch();
				}
				Massege = "成功上传数据";				
				CurrStatus = "Succeed";				
			}else if(count <0){
				CurrStatus = "Fail";
				Massege = "文件解析错误，无法读取数据，数据上传失败";
			}else{
				Massege = "没有找到有效数据!";				
				CurrStatus = "Succeed";						
			}
		}catch (Exception e){
			e.printStackTrace();
			lastCounter = wp.getLastCounter();
			DIFileTransImpl.WriteLog("解析文件:"+impFile.getName()+"失败,失败原因如下:");;	
			MainForm.showLog("解析文件:"+impFile.getName()+"失败,失败原因如下:");
			MainForm.showLog(e.getMessage());
			Massege = e.getMessage().substring(0, (e.getMessage().length()>3000?3000:e.getMessage().length()));
			CurrStatus = "Fail";	
	 
		}finally{
			if(ips !=null){
				ips.close();
				ips=null;
			}
		
		    double end = System.currentTimeMillis() ;  
		    end = (end-start)/1000;
			sdf = new SimpleDateFormat(Keys.DATE_TIME_FORMAT2);
			tableModel.setValueAt(("Fail".equals(CurrStatus)?"上传失败":"已上传"), wp.getCurrRow(), 3); 
		    tableModel.setValueAt(wp.getLastDate(), wp.getCurrRow(), 6);
		    tableModel.setValueAt(lastCounter, wp.getCurrRow(), 7); 
		    tableModel.setValueAt((Long.parseLong(tableModel.getValueAt(wp.getCurrRow(), 8).toString())+count)+"", wp.getCurrRow(), 8);
		   
		    tableModel.setValueAt(count+"", wp.getCurrRow(), 9);
		    tableModel.setValueAt(LastCheckDate, wp.getCurrRow(), 10);
		    /*
		   //PropertiesUtil   currProperties  = new PropertiesUtil(); 	
			currProperties.updateProperties("Scan.LastTime"+wp.getMachineID(), wp.getLastDate());						
			currProperties.updateProperties("Scan.LastCounter"+wp.getMachineID(), lastCounter);
			currProperties.updateProperties("Scan.TotalRows"+wp.getMachineID(), tableModel.getValueAt(wp.getCurrRow(), 8).toString());
			currProperties.updateProperties("Scan.CurrRows"+wp.getMachineID(), count+"");
			currProperties.updateProperties("Scan.LastCheckDate"+wp.getMachineID(), LastCheckDate);
			*/				    
		    
			DIFileTransImpl.WriteLog(wp.getMachineID()+"#机台本次数据采集文件:"+impFile.getName()+"完成，结果:"+CurrStatus+" 上传数据行:"+count+"  耗时:"+end+"(S)");	
			MainForm.showLog(wp.getMachineID()+"#机台本次数据采集文件:"+impFile.getName()+"完成，结果:"+CurrStatus+" 上传数据行:"+count+"  耗时:"+end+"(S)");	

			excuteSQL(wp.getFormCode(),wp.getFormName(),count,impFile.getName(),Massege,CurrStatus,wp.getDivCode());
			DBOptionUtil.closeThreadLocalConnection();
		    return count;
		} 	
   }
	
	public  int customBufferStreamCopy(File source, File target) throws Exception {  
        InputStream fis = null;  
        OutputStream fos = null;  
        int Result =0;
        try {  
            fis = new FileInputStream(source);  
            fos = new FileOutputStream(target);  
            
    	    DIFileTransImpl.WriteLog("上传文件"+source.getName()+"大小:"+getFormatSize(source.length()));
    		MainForm.showLog("上传文件"+source.getName()+"大小:"+getFormatSize(source.length()));
    		sdf = new SimpleDateFormat(Keys.DATE_TIME_FORMAT2);
    		DIFileTransImpl.WriteLog("上传文件"+source.getName()+"最后修改时间:"+ sdf.format(new Date(source.lastModified())));
    		MainForm.showLog("上传文件"+source.getName()+"最后修改时间:"+ sdf.format(new Date(source.lastModified())));
    		
            byte[] buf = new byte[4096];  
            int i;  
            while ((i = fis.read(buf)) != -1) {  
                fos.write(buf, 0, i);  
            }
            Result =1;
        }  
        catch (Exception e) {  
            e.printStackTrace(); 
            Result = 9;
        } finally {  
        	if(fis !=null){
        	   fis.close();
        	}
        	if(fos !=null){
        	   fos.close();  
        	}
        	if(source.isFile()){
        		source.delete();
        		//source.deleteOnExit();
        	}
        	
        	return Result;
        }  
    }
    
	public  int  UpLoadFileforChannel(File f1,File f2) throws Exception{		   
	    int length=209715200; // 2M--200M (int)f1.length();
	    FileInputStream in=new FileInputStream(f1);
	    FileOutputStream out=new FileOutputStream(f2);
	    FileChannel inC=in.getChannel();
	    FileChannel outC=out.getChannel();
	    ByteBuffer b=null;
	    DIFileTransImpl.WriteLog("上传文件"+f1.getName()+"大小:"+getFormatSize(f1.length()));
		MainForm.showLog("上传文件"+f1.getName()+"大小:"+getFormatSize(f1.length()));
		sdf = new SimpleDateFormat(Keys.DATE_TIME_FORMAT2);
		DIFileTransImpl.WriteLog("上传文件"+f1.getName()+"最后修改时间:"+ sdf.format(new Date(f1.lastModified())));
		MainForm.showLog("上传文件"+f1.getName()+"最后修改时间:"+ sdf.format(new Date(f1.lastModified())));
		
	    while(true){
		    try{	
		        if(inC.position()==inC.size()){
		            
		            inC.close();
		            outC.close();
		            out.close();
		            in.close();
		            f1.delete();
		            return 1;
		        }
		        if((inC.size()-inC.position())<length){
		            length=(int)(inC.size()-inC.position());
		        }else
		            length=(int)209715200; //f1.length();
		        b=ByteBuffer.allocateDirect(length);
		        inC.read(b);
		        b.flip();
		        outC.write(b);
		        outC.force(false);
		    }catch (Exception e){
		    	return 9;
		    }
	    }
	}
	
	public   String getFormatSize(double size) {  
        double kiloByte = size/1024;  
        if(kiloByte < 1) {  
            return size + "Byte(s)";  
        }  
          
        double megaByte = kiloByte/1024;  
        if(megaByte < 1) {  
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));  
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";  
        }  
          
        double gigaByte = megaByte/1024;  
        if(gigaByte < 1) {  
            BigDecimal result2  = new BigDecimal(Double.toString(megaByte));  
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";  
        }  
          
        double teraBytes = gigaByte/1024;  
        if(teraBytes < 1) {  
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));  
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";  
        }  
        BigDecimal result4 = new BigDecimal(teraBytes);  
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";  
    }  
	
	
	private  String insertSQL(String formTable, String formCols, String formID, int fDatePOS) throws Exception{
		StringBuffer iSql = new StringBuffer();
		iSql.append("INSERT INTO " +formTable+"(PKEY, PARAMH_PTR,"+formCols);
		if(fDatePOS == 0){
			iSql.append("OPTDATE,DIV_CODE,LAST_TX_DT,LAST_TX_USERNAME) VALUES(");	
		}else{
			iSql.append("DIV_CODE,LAST_TX_DT,LAST_TX_USERNAME) VALUES(");	
		}		
		
		iSql.append(formTable+"_SEQ.NEXTVAL,"+formID+",:"+formCols.replace(",", ",:"));
		if(fDatePOS == 0){
			iSql.append("OPTDATE,:DIV_CODE,sysdate,'SysAuto')");	
		}else{			
			iSql.append("DIV_CODE,sysdate,'SysAuto')");	
		}

		return iSql.toString();
	}
	
	private  int excuteSQL(String RPT_Code,String RPT_Name,int Imp_Count,String File_Name,
        String Reason_Failure,String Status,String DIVCODE){
		int count=0;
		Session session=DBOptionUtil.getSession();
		try {
			DBOptionUtil.beginTransaction();
		 StringBuffer sql = new StringBuffer();
		 sql.append(" insert into MTG_SPCMonitor_LOG(PKEY,RPT_Code,RPT_Name,Opt_Date,Imp_Direction,Imp_Count,File_Name,Reason_Failure,Status,DIV_CODE)");
		 sql.append(" VALUES(MTG_SPCMonitor_LOG_SEQ.NEXTVAL,'");
		 sql.append( RPT_Code+"','"+RPT_Name+"',sysdate,'UP_DI',"+Imp_Count);
		 sql.append( ",'"+File_Name+"','"+Reason_Failure+"','"+Status+"','"+DIVCODE+"')");
			SQLQuery  query=session.createSQLQuery(sql.toString());			
			count=query.executeUpdate();
		} catch (HibernateException e) {				
			e.printStackTrace();			
			MainForm.showLog(e.getMessage());
			return count;
		} 
		DBOptionUtil.commitTransaction();
		DBOptionUtil.closeSession();
		return count;
	
	}	  
   
}