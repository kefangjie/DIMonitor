package cc.ttm.di.ServersImpl;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.JTable;


import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;


import cc.ttm.di.common.CsvUtil;
import cc.ttm.di.common.DBOptionUtil;
import cc.ttm.di.common.DateUtil;
import cc.ttm.di.common.Keys;
import cc.ttm.di.common.Log;
import cc.ttm.di.common.PropertiesUtil;

import cc.ttm.di.mainform.MainForm;
import cc.ttm.di.model.AppInfo;
import cc.ttm.di.model.StdTableModel;



public class DIFileTransImpl {
  
		 public static Logger log;	
		 
		 private  Map<String,String> SPCTable = new HashMap<String,String>();
			
		 private  Map<String,String> SPCTableCols = new HashMap<String,String>();	
		
		 public SimpleDateFormat sdf ;
		
		 private String SourceDirectory ,TargetDirectory ;
		 
		 public boolean ServiceOpenFlag =false;
		 
		 private boolean ConnFlag =false;
		 
		 private File  targetFile ,sourceFile;
		 
		 private  CsvUtil   csv;
		 
		 private  AnalysisCVSFileImpl  analysisCVSFile;
		
		 public static  PropertiesUtil ppUtil  = new PropertiesUtil();
		 
		 private Timer DiJobTimer; 
		 
		 public  DIFileTransImpl(){		 
		    try {
				this.log=Log.getLoger();
			} catch (FileNotFoundException e) {
					
				e.printStackTrace();
			}
		 }
	 

		 
		public int startMonitor(AppInfo appInfo, JTable table ){
			
			 int Flag = 0;
			 initScanSys(appInfo);
		
			 if(ConnFlag ==false){
				 Flag = 1;
				 return Flag;
			 }
			  
			SynchronousMoitorInfo(appInfo,table);
			
			 return Flag;
		} 
		
		public void stopMonitor(){
			
			while(true){						
				if(ServiceOpenFlag == false){
					log.info("停止扫描！");	
					MainForm.showLog("停止扫描！");					
					DiJobTimer.cancel();
					DiJobTimer=null; 
					break;
				}else{					   
					try {
						TimeUnit.MILLISECONDS.sleep(1000);
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}
				}
			}	
		  
		}
		
		public void SynchronousMoitorInfo(final AppInfo appInfo, final JTable table ){
			DiJobTimer=new Timer(true);
			DiJobTimer.schedule(new TimerTask(){
	        	public void run() {
	    			try {	    				
	    				AutoTransJobByTimer(appInfo,table);
	    				
	    			} catch (Exception e) {
	    				e.printStackTrace();
	    			}
	    		}
	    	}, 30*1000,Integer.parseInt(appInfo.getInterval())* 60 * 1000);
	    }
		
		public void AutoTransJobByTimer(AppInfo appInfo, JTable table ){
			
			 StdTableModel tableModel = ((StdTableModel)table.getModel());	
			 
			 int RowCount = tableModel.getRowCount();
			 ServiceOpenFlag = true;
			 String today,LastUpTime,FileName,FileFormat,FileType="",targetFilePath;
			 
			 int between_days =0 ,UpLoad_Flage =0;
			 targetFilePath  = 	appInfo.getTargetDirectory();
			 sdf = new SimpleDateFormat(Keys.DATE_FORMAT2);
			 today = sdf.format(new Date());
			 double start  = System.currentTimeMillis() ;
			
			 try{   				
				
				 Map<String,String> param ; 
				 analysisCVSFile = new AnalysisCVSFileImpl();
				 log.info("本次Job开始执行");
				 MainForm.showLog("本次Job开始执行");
				 
				 CountDownLatch runningThreadNum = new CountDownLatch(RowCount);
				 
				 for(int row =0 ;row <RowCount; row++){	
					 param = new HashMap<String,String>(); 
					 param = getTableNameAndFields(table.getValueAt(row, 11).toString().trim());
					 param.put("divCode", appInfo.getDivCode());
					 param.put("cellsPos", appInfo.getCellIndexs());
					 param.put("currRow", row+"");
					 param.put("Machine", table.getValueAt(row, 1).toString());		
					 param.put("lastCounter", table.getValueAt(row, 7).toString());	
					 param.put("totalRows", table.getValueAt(row, 8).toString());						
					 param.put("LastCheckDate", table.getValueAt(row, 10).toString());	
					 
					 TargetDirectory = checkTargetDirectory(targetFilePath, table.getValueAt(row, 1).toString());				 
					 param.put("TargetDirectory", TargetDirectory);	
					 
					 SourceDirectory =  table.getValueAt(row, 4).toString() ;
					 if(SourceDirectory.indexOf(":\\") == -1 ){
						 SourceDirectory = "\\\\"+table.getValueAt(row,2).toString() +File.separator+SourceDirectory;
					 } 
					 sourceFile = new File(SourceDirectory);
					 if(!sourceFile.exists()){
						  log.info("访问机台:"+table.getValueAt(row,1)+"失败");
						  MainForm.showLog("访问机台:"+table.getValueAt(row,1)+"失败");	
						  MainForm.showLog("访问机台目录:"+SourceDirectory+"失败");	
						  tableModel.setValueAt("已离线", row, 3);
					 }else{
						  log.info("正在监控:"+table.getValueAt(row,1)+"号机台");
						  MainForm.showLog("正在监控:"+table.getValueAt(row,1)+"号机台");
						  MainForm.showLog("访问机台目录:"+SourceDirectory+"成功");	
						  tableModel.setValueAt("已连线", row, 3);
					 }	
					 
					 FileName    = table.getValueAt(row,5).toString().toLowerCase().trim() ;
					 FileFormat  = table.getValueAt(row,5).toString().toLowerCase().trim() ;
					 LastUpTime  = table.getValueAt(row,6).toString();
				
					
				    sdf = new SimpleDateFormat(Keys.DATE_FORMAT7);
			         
				    WorkParam  wp =  new WorkParam();
				    wp.setMachineID(param.get("Machine"));				   
				    wp.setLastCounter(param.get("lastCounter"));
				    wp.setLastCheckDate(param.get("LastCheckDate"));							 
				    wp.setCurrRow(row);
				    wp.setFormTable(param.get("formTable"));
				    wp.setFormName(param.get("formName"));
				    wp.setFormCols(param.get("formCols"));
				    wp.setFormID(param.get("formID"));
				    wp.setfDatePOS(Integer.parseInt(param.get("fDatePOS")));
				    wp.setCellsPos(appInfo.getCellIndexs());
				    wp.setFormCode(param.get("formCode"));
				    wp.setDivCode(param.get("divCode"));
				    wp.setToday(today);
					wp.setSourceDirectory(SourceDirectory);	
				    wp.setTargetDirectory(param.get("TargetDirectory"));
				    wp.setTableModel(tableModel);				    
				   
					new WorkThread(wp,runningThreadNum);
					
					
					
				    /** 
					 if(!"".equals(LastUpTime) &&  LastUpTime !=null){						
						  between_days =  DateUtil.daysBetween(LastUpTime.substring(0, 10),today);
						  MainForm.showLog("between_days:"+between_days);	
						  for(int m =between_days ; m>=0;m--){
							 // MainForm.showLog("m:"+m);
							  sdf = new SimpleDateFormat(Keys.DATE_FORMAT7);
							  FileName = DateUtil.AddDateTime(sdf.format(new Date()), 0-m ,5 ,Keys.DATE_FORMAT7);
							 // MainForm.showLog("m:"+m);
							  
							  param.put("checkDate", FileName);	
							
							  
							  if(FileFormat.startsWith(Keys.DATE_FORMAT4)){ 
							      FileType = FileFormat.replace(Keys.DATE_FORMAT4,"");
							      FileName = FileName + FileType;
							  }else if(FileFormat.startsWith(Keys.DATE_FORMAT5)){
								  FileType = FileFormat.replace(Keys.DATE_FORMAT5,"");
								  FileName = FileName.substring(2) + FileType;
							  }	
							
							  sourceFile = new File(SourceDirectory+File.separator+FileName);
							  param.put("SourceDirectory", SourceDirectory+File.separator+FileName);
							
							  //ProcessingFile(tableModel,param);	
								
						  } 
						
					 }else {
						  if(FileName.startsWith(Keys.DATE_FORMAT4)){ 							     
						      FileName = FileName.replace(Keys.DATE_FORMAT4, sdf.format(new Date()));
						  }else if(FileName.startsWith(Keys.DATE_FORMAT5)){
							  FileName = FileName.replace(Keys.DATE_FORMAT5, sdf.format(new Date()).substring(2));
						  }		
						  
						 param.put("checkDate", sdf.format(new Date()));	
						
						 SourceDirectory = SourceDirectory+File.separator+FileName;  
						 param.put("SourceDirectory", SourceDirectory);
						 
						 sourceFile = new File(SourceDirectory);
						// ProcessingFile(tableModel,param);
								

					 } //文件格式处理	
					 */
				    
					 param.clear();
				 } //for end
				 
				 runningThreadNum.await();
				 
			 } catch (Exception e) {				
					e.printStackTrace();					
					log.error(e.getMessage());	
					MainForm.showLog("系统运行异常");
			 }finally{	
			  
				 double end = System.currentTimeMillis() ;  
				 end = (end-start)/1000;
			
				 PropertiesUtil   currProperties  = new PropertiesUtil(); 
			     for(int row =0 ;row <RowCount; row++){	
			    	if("已上传".equals(table.getValueAt(row, 3).toString().trim())){
						currProperties.updateProperties("Scan.LastTime"+table.getValueAt(row, 1).toString(), table.getValueAt(row, 6).toString());						
						currProperties.updateProperties("Scan.LastCounter"+table.getValueAt(row, 1).toString(), table.getValueAt(row, 7).toString());
						currProperties.updateProperties("Scan.TotalRows"+table.getValueAt(row, 1).toString(), table.getValueAt(row, 8).toString().toString());
						currProperties.updateProperties("Scan.CurrRows"+table.getValueAt(row, 1).toString(), table.getValueAt(row, 9).toString());
						currProperties.updateProperties("Scan.LastCheckDate"+table.getValueAt(row, 1).toString(), table.getValueAt(row, 10).toString());
					}
				}
			
				 sdf = new SimpleDateFormat(Keys.DATE_TIME_FORMAT2);
				 FileName = DateUtil.AddDateTime(sdf.format(new Date()), Integer.parseInt(appInfo.getInterval()) ,12 ,Keys.DATE_TIME_FORMAT2);
				 log.info("本次Job耗时(S):"+end);
				 log.info("本次Job结束,下次Job开始时间:"+FileName);
				 MainForm.showLog("本次Job耗时(S):"+end);
				 MainForm.showLog("本次Job结束,下次Job开始时间:"+FileName);
				 
				 ServiceOpenFlag = false;
			 
			 }
		}
		

		
	
		
		private  void ProcessingFile(StdTableModel tableModel,Map<String,String> param){
			int UpLoad_Flage =0;	
			try{
				 if(sourceFile.exists()){	
					 sdf = new SimpleDateFormat(Keys.DATE_TIME_FORMAT2);
					 
					 String LastDate = sdf.format(new Date(sourceFile.lastModified()));
					 String checkDate =  param.get("checkDate").toString();
					 
					 String LastTime  =  LastDate.substring(11);
					 LastDate = LastDate .substring(1,10);
					 
					 LastDate = LastDate.replaceAll("/", "");
					 if (Integer.parseInt(LastDate) > Integer.parseInt(checkDate)){
						 LastDate = checkDate.substring(1, 4) +"/" + checkDate.substring(5, 6) +"/"+ checkDate.substring(7, 8) +" "+LastTime.trim();
						 param.put("LastDate", LastDate);
					 }else{
					     param.put("LastDate", sdf.format(new Date(sourceFile.lastModified())));
					 }	
					 
					 if(tableModel.getValueAt(Integer.parseInt(param.get("currRow")),6).toString().equals(param.get("LastDate").toString())){
						 log.info(sourceFile.getName()+"已上传,最后上传时间:"+param.get("LastDate").toString());
						 MainForm.showLog(sourceFile.getName()+",最后上传时间:"+param.get("LastDate").toString());
						if( sourceFile.delete()){
							 tableModel.setValueAt("监控中", Integer.parseInt(param.get("currRow")), 3);
							 log.info("删除重复文件:"+sourceFile.getName());
							 MainForm.showLog("删除重复文件:"+sourceFile.getName());
						}
					 }else{
						 TargetDirectory = param.get("TargetDirectory").toString()+File.separator+sourceFile.getName(); 
						 targetFile = new File(TargetDirectory);
						 
			
						 
						 tableModel.setValueAt("正在上传", Integer.parseInt(param.get("currRow")), 3);
						 log.info("正在上传文件:"+sourceFile.getName()+" 文件大小:");
						 MainForm.showLog("正在上传文件:"+sourceFile.getName()+" 文件大小:");
						 
						 UpLoad_Flage = UpLoadFileforChannel(sourceFile,targetFile);
						 
						while(true){
							  if(UpLoad_Flage ==1){									 
								 if(targetFile.exists() && targetFile.isFile()){ 
									 UpLoad_Flage = 0;
									 log.info("文件:"+sourceFile.getName()+" 已上传完");
									 MainForm.showLog("文件:"+sourceFile.getName()+" 已上传完");
									 tableModel.setValueAt("已上传待导入", Integer.parseInt(param.get("currRow")), 3);
									 ImpDataForFile(tableModel,targetFile,param);
									// TimeUnit.MILLISECONDS.sleep(1000);
									 break;
								 }
							   }else{
								   
								   TimeUnit.MILLISECONDS.sleep(1000);
							   }
						 }
					}
					 
				 }
			 }catch (Exception e) {				
					e.printStackTrace();					
					log.error(e.getMessage());	
					MainForm.showLog("文件处理异常");
			 }
		}
		
		private int ImpDataForFile(StdTableModel tableModel,File impFile,Map<String,String> param) throws Exception{
				PreparedStatement ips=null;
				String Massege="" ,CurrStatus="";
				Connection conn=DBOptionUtil.getThreadLocalConnection();
				int count = 0;
				double start  = System.currentTimeMillis() ; 
				String  lastCounter = param.get("lastCounter");
				String  LastCheckDate = param.get("LastCheckDate");
				try{			
					lastCounter = (lastCounter==null || "".equals(lastCounter))?"0":lastCounter;
					
					tableModel.setValueAt(("导入中"), Integer.parseInt(param.get("currRow")), 3); 
				
					String insertSql = insertSQL(param.get("formTable"),  param.get("formCols"), param.get("formID"), Integer.parseInt(param.get("fDatePOS")));
					ips= conn.prepareStatement(insertSql);
					MainForm.showLog("正在解析文件:"+impFile.getName());
					csv = new CsvUtil(impFile);		
					Massege = analysisCVSFile.ImpDIProdDataForCSV(ips,param.get("formCode"),csv,param.get("cellsPos"),param.get("checkDate"),LastCheckDate, lastCounter,Integer.parseInt(param.get("fDatePOS")),param.get("divCode"));
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
					lastCounter = param.get("lastCounter");
					log.info("解析文件:"+impFile.getName()+"失败,失败原因如下:");;	
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
					tableModel.setValueAt(("Fail".equals(CurrStatus)?"上传失败":"已上传"), Integer.parseInt(param.get("currRow")), 3); 
				    tableModel.setValueAt(param.get("LastDate"), Integer.parseInt(param.get("currRow")), 6);
				    tableModel.setValueAt(lastCounter, Integer.parseInt(param.get("currRow")), 7); 
				    tableModel.setValueAt((Long.parseLong(tableModel.getValueAt(Integer.parseInt(param.get("currRow")), 8).toString())+count)+"", Integer.parseInt(param.get("currRow")), 8);
				   
				    tableModel.setValueAt(count+"", Integer.parseInt(param.get("currRow")), 9);
				    tableModel.setValueAt(LastCheckDate, Integer.parseInt(param.get("currRow")), 10);
					if(ppUtil !=null){
						ppUtil.updateProperties("Scan.LastTime"+param.get("Machine"), param.get("LastDate"));						
						ppUtil.updateProperties("Scan.LastCounter"+param.get("Machine"), lastCounter);
						ppUtil.updateProperties("Scan.TotalRows"+param.get("Machine"), tableModel.getValueAt(Integer.parseInt(param.get("currRow")), 8).toString());
						ppUtil.updateProperties("Scan.CurrRows"+param.get("Machine"), count+"");
						ppUtil.updateProperties("Scan.LastCheckDate"+param.get("Machine"), LastCheckDate);
					}				    
				    
					log.info(param.get("Machine")+"#机台本次数据采集文件:"+impFile.getName()+"完成，结果:"+CurrStatus+" 上传数据行:"+count+"  耗时:"+end+"(S)");	
					MainForm.showLog(param.get("Machine")+"#机台本次数据采集文件:"+impFile.getName()+"完成，结果:"+CurrStatus+" 上传数据行:"+count+"  耗时:"+end+"(S)");	

					excuteSQL(param.get("formCode"),param.get("formName"),count,impFile.getName(),Massege,CurrStatus,param.get("divCode"));
					DBOptionUtil.closeThreadLocalConnection();
				    return count;
				} 	
	  }
		
		
		public  int  UpLoadFileforChannel(File f1,File f2) throws Exception{		   
		    int length=2097152;
		    FileInputStream in=new FileInputStream(f1);
		    FileOutputStream out=new FileOutputStream(f2);
		    FileChannel inC=in.getChannel();
		    FileChannel outC=out.getChannel();
		    ByteBuffer b=null;
			log.info("上传文件大小:"+getFormatSize(f1.length()));
			MainForm.showLog("上传文件大小:"+getFormatSize(f1.length()));
			sdf = new SimpleDateFormat(Keys.DATE_TIME_FORMAT2);
			log.info("上传文件最后修改时间:"+ sdf.format(new Date(f1.lastModified())));
			MainForm.showLog("上传文件最后修改时间:"+ sdf.format(new Date(f1.lastModified())));
			
		    while(true){
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
		            length=2097152;
		        b=ByteBuffer.allocateDirect(length);
		        inC.read(b);
		        b.flip();
		        outC.write(b);
		        outC.force(false);
		    }
		}
		
		public  String getFormatSize(double size) {  
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
		
       public String checkTargetDirectory(String OldFilePath,String MachNo){    
			
    	   if(OldFilePath.equals("\\")){			
				 sdf = new SimpleDateFormat(Keys.DATE_FORMAT8);
				 OldFilePath = OldFilePath +sdf.format(new Date());
			 }else{
				 sdf = new SimpleDateFormat(Keys.DATE_FORMAT8);
				 OldFilePath = OldFilePath +File.separator+sdf.format(new Date());
			 }
    	   
        	String TargetFilePath = OldFilePath +File.separator+ MachNo ;				
			 targetFile = new File(TargetFilePath);
			 if(!targetFile.exists()){
					File targetDirParent=new File(targetFile.getParent());
					if(!targetDirParent.exists()){
						targetDirParent.mkdirs();
					}
					targetFile.mkdir(); 
					log.info("本地目录创建成功"+TargetFilePath);
			        MainForm.showLog("本地目录创建成功"+TargetFilePath);
				}else{
					log.info("本地目录位置:"+TargetFilePath);
				    MainForm.showLog("本地目录位置:"+TargetFilePath);
			}
        	
        	return TargetFilePath;
        }	
		
	   public  Map<String,String> getTableNameAndFields(String AppCode){			
			  Map<String,String> param = new HashMap<String,String>();
			  int fDatePOS = 0;
			  String[] formArray;			
			  String formID = null;		
			  String formTable = null;			
			  String formName = null;
			  String formCols;	
				 
			   if(SPCTable.containsKey(AppCode)){
					formArray = SPCTable.get(AppCode).split("=jk=");				
					formID     = formArray[0];
					formTable  = formArray[1];
					formName   = formArray[2];	
				} 
				if(formID !=null && !"".equals(formID)){
					formCols = SPCTableCols.get(formID);
					param.put("formCols", formCols);
					for(String Field : formCols.split(",")){
						fDatePOS = fDatePOS +1;
						if(Field.toUpperCase().equals("OPTDATE"))
						   break;	
					}
				}
			  param.put("formID", formID);
			  param.put("formTable", formTable);
			  param.put("formCode", AppCode);
			  param.put("formName", formName);
			  param.put("fDatePOS", fDatePOS+"");
			  return  param;
			
		}		
		
		private  void  initScanSys(AppInfo appInfo) {
			try {
				DBOptionUtil.dataSourceConf(appInfo.getConnURL(), appInfo.getUserName(), appInfo.getPassWord());
				ConnFlag = true;
			} catch (MappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ConnFlag = false;
				log.error(e.getMessage());
				MainForm.showLog("连接数据库失败，停止扫描");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ConnFlag = false;
				log.error(e.getMessage());	
				MainForm.showLog("读取系统配置文件失败，停止扫描");
			}
			if(SPCTable.isEmpty()){
				SPCFormHeadList(appInfo.getDivCode(),appInfo.getAppID());
			}
			if(SPCTableCols.isEmpty()){
				SPCFormDetail(appInfo.getDivCode(),appInfo.getAppID());
			}
			log.info("系统配置信息读取成功！");	
			MainForm.showLog("系统配置信息读取成功！");
		}
	   
		private void SPCFormHeadList(String divCode,String AppPkeys){			
			Session session = DBOptionUtil.getSession();		
			StringBuffer hql = new StringBuffer();
			hql.append(" SELECT  replace(PARAMCODE,'/','-')PARAMCODE, (PKEY || '=jk=' || TABLENAME || '=jk=' || PARAMNAME)Pkey ");
			hql.append(" from  MTG_ProdParamHead ");
			hql.append(" where PKEY in ("+AppPkeys+")");
			hql.append("       and div_code in ('"+divCode+"','MTG') ");
			hql.append(" order by PARAMCODE ");
			Query  qty=session.createSQLQuery(hql.toString());
			List<Object[]> list = (List<Object[]>)qty.list(); 
			if (list != null) {			
				SPCTable.clear();
			   for (Object[] array: list) { 			 
				   SPCTable.put(array[0].toString(), array[1].toString());
			   }
		    }
			DBOptionUtil.closeSession();
		}

		private void SPCFormDetail(String divCode ,String AppPkeys){
			Session session = DBOptionUtil.getSession();		
			StringBuffer hql = new StringBuffer();
			hql.append(" SELECT  im.PARAMH_PTR,im.FIELDNAME  from    MTG_ProdParamItems im ");
			hql.append(" where ACTION_FLAG ='1' and exists (select 1 from  MTG_ProdParamHead hd ");
			hql.append(" where im.paramh_ptr = hd.pkey and  hd.PKEY in ("+AppPkeys+")");
			hql.append("   and hd.div_code  in ('"+divCode+"','MTG') )");
			hql.append(" order by  im.PARAMH_PTR,im.seq ");
			Query  qty=session.createSQLQuery(hql.toString());
			List<Object[]> list = (List<Object[]>)qty.list(); 
			if (list != null) {			
				SPCTableCols.clear();
			   for (Object[] array: list) { 
					if(SPCTableCols.get(array[0].toString())!="" && 
					   SPCTableCols.get(array[0].toString())!=null){					
					   SPCTableCols.put(array[0].toString(),SPCTableCols.get(array[0].toString())+array[1].toString()+",");				
					}else{
						SPCTableCols.put(array[0].toString(), array[1].toString()+",");
					}			  
			   }
		    }
			DBOptionUtil.closeSession();
		}

		private String insertSQL(String formTable, String formCols, String formID, int fDatePOS) throws Exception{
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
			 sql.append( RPT_Code+"','"+RPT_Name+"',sysdate,'TowERP',"+Imp_Count);
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
		
		public  static synchronized  void WriteLog(String msg){		
			log.info(msg);			
		}
}
