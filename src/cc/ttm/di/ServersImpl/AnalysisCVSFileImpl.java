package cc.ttm.di.ServersImpl;

import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import au.com.bytecode.opencsv.CSVReader;


import cc.ttm.di.common.CsvUtil;
import cc.ttm.di.common.DateUtil;
import cc.ttm.di.common.Keys;
import cc.ttm.di.mainform.MainForm;



public class AnalysisCVSFileImpl {
    
	@SuppressWarnings("finally")
	public String  ParserCSVWithDIFile(PreparedStatement ips, String monitorId,String FilePath,String cellsPos,String checkDate,
			                           String LastCheckDate, String lastCounter,int fDatePOS,String divCode) throws Exception{
		int count=0;
		
		long CurrCounter = Long.parseLong(lastCounter.trim());
		String  Csvfilename = FilePath.substring(FilePath.lastIndexOf("\\")+1);	
		String cell="";
		CSVReader reader =null;
		try{
			int index = 99;	
			Date LastUpdate =null;             
		   
			if(cellsPos.indexOf("["+monitorId) !=-1){
			   cellsPos = cellsPos.substring(cellsPos.indexOf("["+monitorId),cellsPos.indexOf(monitorId+"]"));
			   cellsPos = cellsPos.replace("["+monitorId,"");
			}
			cellsPos = cellsPos.replace("，",",");
			

			
			String []  cellIndex= cellsPos.split(",");
			int colNum = cellIndex.length ;
			
			SimpleDateFormat sd = new  SimpleDateFormat(Keys.DATE_TIME_FORMAT4);
			SimpleDateFormat sd2 = new SimpleDateFormat(Keys.DATE_FORMAT7);
			if(LastCheckDate !=null && !"".equals(LastCheckDate)){
				LastCheckDate = LastCheckDate.replaceAll("-", "/").trim(); 
				LastUpdate  = sd.parse(LastCheckDate.trim());
			}
			Date OptDate = sd2.parse(checkDate);
			String  CycleNum = DateUtil.getCycle(checkDate,Keys.DATE_FORMAT7);	
			
			reader = new CSVReader(new FileReader(FilePath), ',');
			//read line by line
			String[] record = null;
			//skip header row
			record =  reader.readNext();
			 
		    //MainForm.showLog(FilePath+"=Head="+record.length);
			//DIFileTransImpl.WriteLog(FilePath+"=Head="+record.length);	
			
		     while((record = reader.readNext()) != null){					
		    	 if(record.length > 30){ 
		    		 if((record[0] == null || "-".equals(record[0].toString()) || "".equals(record[0].toString())) || 
		    			(record[7] == null || "-".equals(record[7].toString()) || "".equals(record[7].toString())) ||
		    			(record[8] == null || "-".equals(record[8].toString()) || "-".equals(record[8].toString()))|| 
		    			(record[30] == null || "NG".equals(record[30].toString())) ){
		    			 continue; 
		    		 }else{		    	      
		    			 CurrCounter = Integer.parseInt(record[7]);
		    			// MainForm.showLog(count+" CurrCounter="+CurrCounter);
						// DIFileTransImpl.WriteLog(count+" CurrCounter="+CurrCounter);		
							
						  if(count > 0 || LastUpdate ==null ||  LastUpdate.before(sd.parse(record[0].toString().trim().substring(0,19).replaceAll("-", "/")))){	
							    for(int j=0; j< colNum; j++){		    	
							    	if(fDatePOS > 0 && ((fDatePOS-j)==1)){ //判断日期位置
									
							    		 ips.setDate(j+1, new java.sql.Date(OptDate.getTime()));
							    	}else{
							    		
							    		index = Integer.parseInt(cellIndex[j]==null || "".equals(cellIndex[j])?"-3":cellIndex[j]);
								    	if(index ==-1){	
								    	    cell= CycleNum;						    		
								    	}else if(index ==-2){
								    		if(CurrCounter < 431244){
								    			 cell= "9#";
								    		}else if(CurrCounter > 539054 && CurrCounter <754677){
								    			 cell= "2#";
								    		}else if(CurrCounter >= 431244 && CurrCounter <=539054){
								    			 cell= "4#";
								    		}else if(CurrCounter >= 754677 && CurrCounter <=862487){
								    			 cell= "3#";
								    		}else if(CurrCounter >= 862488  && CurrCounter <=970298){
								    			 cell= "6#";
								    		}else if(CurrCounter >= 970299 && CurrCounter <=1078109){
								    			 cell= "5#";
								    		}else if(CurrCounter >= 1078110 && CurrCounter <=1185920){
								    			 cell= "1#";
								    		}else if(CurrCounter >1185920){
								    			 cell= "7#";
								    		}     
								    	}else {
								    	
								    		if(index <0){
								    			break;
								    		}
								    	    cell = record[index].toString();
								    	  
								    	    
								    	    if(index ==11 && cell!=null && !"".equals(cell)){
									    		cell = cell.trim();							    		
							
								    	    	if((colNum-j>3)){		
									    	    	if(cell.indexOf("_") != -1){
									    	    		cell = cell.substring(0, cell.indexOf("_"));	
									    	    	}else if(cell.indexOf("-") !=-1){  
									    	    		cell = cell.substring(0, cell.indexOf("-"));	
									    	    	}								    	    	
									    	    	cell = cell.toUpperCase();
								    	    	}else{	
									    	    	if(cell.indexOf("_") != -1){
									    	    		cell = cell.substring(cell.indexOf("_"));	
									    	    	}else if(cell.indexOf("-") !=-1){  
									    	    		cell = cell.substring(cell.indexOf("-"));	
									    	    	}	
									    	    	
								    	    		if(Csvfilename.toLowerCase().contains("left")){
								    	    			cell= "Left"+cell; 
								    	    		}else if(Csvfilename.toLowerCase().contains("right")){
								    	    			cell= "Right"+cell; 
								    	    		}else{
								    	    			cell= "#"+cell; 
								    	    		}
								    	    	}					    	    
								    	    }
								    	}
								   
							    		if(cell==null ){
							    			ips.setString(j+1,"");
							    		}else{
							    			cell = cell.replace("'", "''");
							    			cell = cell.replace(",", "'|| CHR(44) ||'");	
							    			cell = cell.replace("&", "'|| CHR(38) ||'"); 
							    		
								        	ips.setString(j+1, cell.trim());	
								        }		    	  		    	   
							    	}			    	
							    }
							 
								if(fDatePOS == 0){			
									ips.setDate(colNum+1, new java.sql.Date(OptDate.getTime()));
									ips.setString(colNum+2, divCode);
								}else{
									ips.setString(colNum+1, divCode);				
								}
							    ips.addBatch();
							    
							    count = count + 1;	
							    LastCheckDate = record[0].toString().trim().substring(0,19);
						 }						 
		    	    }
		    	 }else{
		    		 continue; 
		    	 }
		      }
							
			cell = count+";"+CurrCounter+";"+LastCheckDate;
		}catch (Exception e){
			e.printStackTrace();
			count =-1;
			cell = count+";"+CurrCounter+";"+LastCheckDate;
			MainForm.showLog(e.getMessage());
			DIFileTransImpl.WriteLog(e.getMessage());
		}finally{	
	    	if(reader != null)      	  //关闭CsvReader  
		       reader.close(); 
	    	
	    	MainForm.showLog(Csvfilename+"="+cell);
			DIFileTransImpl.WriteLog(Csvfilename+"="+cell);			
			
			return cell;
		}
	}		
	
	@SuppressWarnings("finally")
	public String  ImpDIProdDataForCSV(PreparedStatement ips, String monitorId,CsvUtil csv,String cellsPos,String checkDate,
			                           String LastCheckDate, String lastCounter,int fDatePOS,String divCode) throws SQLException, ParseException{
		int count=0;
		
		long CurrCounter = Long.parseLong(lastCounter.trim());
		
		String cell="";
		try{
			int index = 99;
			int rowNum = csv.getRowNum(); 
			int colNum = csv.getColNum();
			Date LastUpdate =null;
             
		
			if(cellsPos.indexOf("["+monitorId) !=-1){
			   cellsPos = cellsPos.substring(cellsPos.indexOf("["+monitorId),cellsPos.indexOf(monitorId+"]"));
			   cellsPos = cellsPos.replace("["+monitorId,"");
			}
			cellsPos = cellsPos.replace("，",",");
			

			
			String []  cellIndex= cellsPos.split(",");
			colNum = cellIndex.length ;
			
			SimpleDateFormat sd = new  SimpleDateFormat(Keys.DATE_TIME_FORMAT4);
			SimpleDateFormat sd2 = new SimpleDateFormat(Keys.DATE_FORMAT7);
			if(LastCheckDate !=null && !"".equals(LastCheckDate)){
				LastCheckDate = LastCheckDate.replaceAll("-", "/").trim(); 
				LastUpdate  = sd.parse(LastCheckDate.trim());
			}
			Date OptDate = sd2.parse(checkDate);
			String  CycleNum = DateUtil.getCycle(checkDate,Keys.DATE_FORMAT7);	
			for (int i = 1; i < rowNum; i++) {	
				if(csv.getCurrRowColNum(i)>30){ 					
				   cell = csv.getString(i,30);
				   //-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,
				   if((csv.getString(i,0).equals("-") || csv.getString(i,7).equals("-") || csv.getString(i,8).equals("-")) || "NG".equals(cell.trim())){
					   continue;
				   }else{
				       CurrCounter = csv.getLong(i,7);
				   }
				}else{
					continue;
				}
				
				
				if(cell !=null && !"".equals(cell)){ //去掉重复数据
				  if(count > 0 || LastUpdate ==null ||  LastUpdate.before(sd.parse(csv.getString(i,0).trim().substring(0,19).replaceAll("-", "/")))){	
					    for(int j=0; j< colNum; j++){		    	
					    	if(fDatePOS > 0 && ((fDatePOS-j)==1)){ //判断日期位置
							
					    		 ips.setDate(j+1, new java.sql.Date(OptDate.getTime()));
					    	}else{
					    		
					    		index = Integer.parseInt(cellIndex[j]==null || "".equals(cellIndex[j])?"-3":cellIndex[j]);
						    	if(index ==-1){	
						    	    cell= CycleNum;						    		
						    	}else if(index ==-2){
						    		if(CurrCounter < 431244){
						    			 cell= "9#";
						    		}else if(CurrCounter > 539054 && CurrCounter <754677){
						    			 cell= "2#";
						    		}else if(CurrCounter >= 431244 && CurrCounter <=539054){
						    			 cell= "4#";
						    		}else if(CurrCounter >= 754677 && CurrCounter <=862487){
						    			 cell= "3#";
						    		}else if(CurrCounter >= 862488  && CurrCounter <=970298){
						    			 cell= "6#";
						    		}else if(CurrCounter >= 970299 && CurrCounter <=1078109){
						    			 cell= "5#";
						    		}else if(CurrCounter >= 1078110 && CurrCounter <=1185920){
						    			 cell= "1#";
						    		}else if(CurrCounter >1185920){
						    			 cell= "7#";
						    		}     
						    	}else {
						    	
						    		if(index <0){
						    			break;
						    		}
						    	    cell = csv.getString(i,index);	
						    	    
						    	    //System.out.println(j+"=index=="+index+"=cell=="+cell);
						    	    
						    	    if(index ==11 && cell!=null && !"".equals(cell)){
							    		cell = cell.trim();							    		
					
						    	    	if((colNum-j>3)){		
							    	    	if(cell.indexOf("_") != -1){
							    	    		cell = cell.substring(0, cell.indexOf("_"));	
							    	    	}else if(cell.indexOf("-") !=-1){  
							    	    		cell = cell.substring(0, cell.indexOf("-"));	
							    	    	}								    	    	
							    	    	cell = cell.toUpperCase();
						    	    	}else{	
							    	    	if(cell.indexOf("_") != -1){
							    	    		cell = cell.substring(cell.indexOf("_"));	
							    	    	}else if(cell.indexOf("-") !=-1){  
							    	    		cell = cell.substring(cell.indexOf("-"));	
							    	    	}	
							    	    	
						    	    		if(csv.filename.toLowerCase().contains("left")){
						    	    			cell= "Left"+cell; 
						    	    		}else if(csv.filename.toLowerCase().contains("right")){
						    	    			cell= "Right"+cell; 
						    	    		}else{
						    	    			cell= "#"+cell; 
						    	    		}
						    	    	}					    	    
						    	    }
						    	}
						   
					    		if(cell==null ){
					    			ips.setString(j+1,"");
					    		}else{
					    			cell = cell.replace("'", "''");
					    			cell = cell.replace(",", "'|| CHR(44) ||'");	
					    			cell = cell.replace("&", "'|| CHR(38) ||'"); 
					    		
						        	ips.setString(j+1, cell.trim());	
						        }		    	  		    	   
					    	}			    	
					    }
					 
						if(fDatePOS == 0){			
							ips.setDate(colNum+1, new java.sql.Date(OptDate.getTime()));
							ips.setString(colNum+2, divCode);
						}else{
							ips.setString(colNum+1, divCode);				
						}
					    ips.addBatch();
					    
					    count = count + 1;	
			
				   }
				  LastCheckDate = csv.getString(i,0).trim().substring(0,19);
				}	    
			    
			}
			
			cell = count+";"+CurrCounter+";"+LastCheckDate;
		}catch (Exception e){
			e.printStackTrace();
			count =-1;
			cell = count+";"+CurrCounter+";"+LastCheckDate;
			MainForm.showLog(e.getMessage());
			DIFileTransImpl.WriteLog(e.getMessage());
		}finally{
		    try { 
		    	MainForm.showLog(csv.getFilename()+"="+cell);
				DIFileTransImpl.WriteLog(csv.getFilename()+"="+cell);
				csv.CsvClose();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			return cell;
		}
	}	
	
	
	@SuppressWarnings("finally")
	public int CommonCSVFileParsing(PreparedStatement ips, String monitorId,CsvUtil csv,int fDatePOS,String divCode) throws SQLException, ParseException{
		int count=0,pos,inc =0 ;
		int rowNum = csv.getRowNum(); 
		int colNum = csv.getColNum();
		String cell;
		try{
			for (int i = 1; i < rowNum; i++) {	
				
				if("P-LDR-Effic-01".equals(monitorId)){
				    cell = csv.filename;
				    pos = cell.indexOf("#");
				    if(pos >=1 ){
				    	cell = cell.substring(0, pos+1);				    	
				    	pos = cell.indexOf("_");				    	
				    	if (pos >=1 )
				    		cell = cell.substring(pos+1);
				    }else{
				    	cell = "0#" ; 
				    }
				    inc =1 ;
				    ips.setString(inc, cell.trim());				  
				}
				
			    for(int j=0; j< colNum; j++){			    	
			    	cell = csv.getString(i,j);			    	
			    	if(fDatePOS > 0 && (((inc ==1) && (fDatePOS-j==2)) ||  (fDatePOS-j)==1)){ 
			    		if(cell == null || cell.equals("")){		
			    			
			    		   ips.setDate(j+1+inc, new java.sql.Date(new Date().getTime()));
			    		   
						}else {
							// 2013/09/10 
							if(cell.startsWith("'")){
								cell = cell.replace("'", "");
							}
							cell = cell.trim();
						
							if(cell.substring(4, 4).equals("-")){
							   ips.setDate(j+1+inc, new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(cell).getTime()));
							}else if(cell.substring(4, 4).equals("/")){
								ips.setDate(j+1+inc, new java.sql.Date(new SimpleDateFormat("yyyy/MM/dd").parse(cell).getTime()));
							}else if(cell.substring(2, 2).equals("-")){
								ips.setDate(j+1+inc, new java.sql.Date(new SimpleDateFormat("MM-dd-yyyy").parse(cell).getTime()));
							}else if(cell.substring(2, 2).equals("/")){
								ips.setDate(j+1+inc, new java.sql.Date(new SimpleDateFormat("MM/dd/yyyy").parse(cell).getTime()));
							}else{
								ips.setDate(j+1+inc, new java.sql.Date(new Date().getTime()));
							}								
						}
			    	}else{
			    		if(cell==null ){
			    			ips.setString(j+1+inc,"");
			    		}else{
			    			cell = cell.replace("'", "''");
			    			cell = cell.replace(",", "'|| CHR(44) ||'");	
			    			cell = cell.replace("&", "'|| CHR(38) ||'");			
				        	ips.setString(j+1+inc, cell.trim());	
				        }		    	  		    	   
			    	}			    	
			    }
			 
				if(fDatePOS == 0){			
					ips.setDate(colNum+1+inc, new java.sql.Date(new Date().getTime()));
					ips.setString(colNum+2+inc, divCode);
				}else{
					ips.setString(colNum+1+inc, divCode);				
				}
			    ips.addBatch();
			    
			    count = count + 1;
			    
			}
		}catch (Exception e){
			e.printStackTrace();
			MainForm.showLog(e.getMessage());
		}finally{
			return count++;
		}
	}	
}
