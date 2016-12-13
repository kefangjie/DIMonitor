package cc.ttm.di.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;




public class CsvUtil { 


	public String filename ; 
	
    private int colsCount;
    
    

	private BufferedReader bufferedreader = null; 

	private List list =new ArrayList(); 
	
	public CsvUtil(){ 
	
	} 

	
	public CsvUtil(File currFile) throws IOException{ 	
	      this.filename = currFile.getName(); 	    
	      bufferedreader =  new BufferedReader(new InputStreamReader(new FileInputStream(currFile)));
	      String stemp; 
	      list =new ArrayList();
	      while((stemp = bufferedreader.readLine()) != null){ 	     
	             list.add(stemp); 
	       } 
	} 

	public CsvUtil(String currFile) throws IOException{ 
		double start  = System.currentTimeMillis() ; 
		CSVReader reader = null;
		try{
			
		      this.filename = currFile.substring(currFile.lastIndexOf("\\")+1);	  
		      
		      reader = new CSVReader(new FileReader(filename), ',');
		      
		     //  bufferedreader = new BufferedReader(new FileReader(filename)); 
		      String[] record = null;
		      while((record = reader.readNext()) != null){
		    	 if(record.length > 30){ 
		    		 if((record[0] == null || "-".equals(record[0].toString())) || 
		    			(record[7] == null || "-".equals(record[7].toString())) ||
		    			(record[8] == null || "-".equals(record[8].toString())) || 
		    			(record[30] == null || "NG".equals(record[30].toString())) ){
		    			 continue; 
		    		 }else{
		    	         list.add(record); 
		    	    }
		    	 }else{
		    		 continue; 
		    	 }
		      }
	
		      
	      }catch (FileNotFoundException e) {  	    	  
	    	  System.out.println("Error reading csv file."); 
	      } catch (IOException e){  
	    	  e.getStackTrace(); 
	      }finally {  
	    	   if(reader != null)      	  //关闭CsvReader  
	    	      reader.close();  
			    double end = System.currentTimeMillis() ;  
			    end = (end-start)/1000;
			    
			    System.out.println("end="+end);
	      }   
	
	} 

	public String getFilename() {
		return filename;
	}


	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public int getColsCount() {
		return colsCount;
	}


	public void setColsCount(int colsCount) {
		this.colsCount = colsCount;
	}


	public List getList() throws IOException { 
	
	        return list; 
	} 
    

	//寰楀埌csv鏂囦欢鐨勮鏁�
	public int getRowNum(){ 
	
	        return list.size(); 
	} 


	//寰楀埌csv鏂囦欢鐨勫垪鏁�
	public int getColNum(){ 
	
       if(!list.toString().equals("[]")) { 	       
            if(list.get(0).toString().contains(",")) { //csv鏂囦欢涓紝姣忓垪涔嬮棿鐨勬槸鐢�,'鏉ュ垎闅旂殑 
                    return list.get(0).toString().split(",").length; 
            }else if(list.get(0).toString().trim().length() != 0) { 
                return 1; 
            }else{ 
                return 0; 
                  } 
            }else{ 
                return 0; 
        } 
	} 



	//鍙栧緱鎸囧畾琛岀殑鍊�
	
	public String getRow(int index) { 
	
         if (this.list.size() != 0) return (String) list.get(index); 
         else                      
        	 return null; 
	} 

	public  int getCurrRowColNum(int row) { 
		
	      if (this.list.size() != 0 &&  row>0) 	    	  
	    	  return  list.get(row).toString().split(",").length; 
	       else                      
	          return -1; 
	}

	//鍙栧緱鎸囧畾鍒楃殑鍊�
	public String getCol(int index){ 
	
	       if (this.getColNum() == 0){ 
	                return null; 
	       } 
	       
	      StringBuffer scol = new StringBuffer(); 
	      String temp = null; 
	      int colnum = this.getColNum(); 
	      
	      if (colnum > 1){ 
	         for (Iterator it = list.iterator(); it.hasNext();) { 
	              temp = it.next().toString(); 
	              scol = scol.append(temp.split(",")[index] + ","); 
	          } 
	      }else{ 
	           for (Iterator it = list.iterator(); it.hasNext();) { 
	                temp = it.next().toString(); 
	                scol = scol.append(temp + ","); 
	            } 
	      } 
	        String str=new String(scol.toString()); 
	        str = str.substring(0, str.length() - 1); 
	        return str; 
	} 


	//鍙栧緱鎸囧畾琛岋紝鎸囧畾鍒楃殑鍊�
	public String getString(int row, int col) { 	
	        String temp = null; 
	        int colnum = this.getColNum(); 
	        
	        if(colnum > 1){ 
	        	//System.out.println("row="+row+"colnum=="+colnum+"=col=="+col);
                temp = list.get(row).toString().split(",")[col]; 
                temp = temp.replaceAll("\"", "");
                temp = temp.trim();
	        }else if(colnum == 1) { 
                temp = list.get(row).toString(); 
                temp = temp.replaceAll("\"", "");
                temp = temp.trim();
	        }else{ 
	                temp = null; 
	        } 
	        return temp; 
	 } 

   public  long  getLong(int row, int col) { 	
	    String  result =  this.getString(row,col);	
	     if(result ==null || "".equals(result)){
	    	 result ="-1";
	     }
	    return Long.parseLong(result);
   }
	
	public void CsvClose() throws IOException { 
		   if(this.bufferedreader !=null){
	         this.bufferedreader.close(); 
	        }
		   if(this.list !=null && this.list.size()>0){
	         this.list.clear();
		   }
	         
	} 

	public void run(String filename) throws IOException { 
	         CsvUtil cu = new CsvUtil(filename); 
	         
	         BufferedWriter writer=null; 
	         try {   
	                 writer=new BufferedWriter(new FileWriter("d://tesst.txt",true));//true琛ㄧず寰�枃浠跺悗闈㈠啓锛屼笉浼氳鐩栧師鏈夊唴瀹�  
	          } catch (IOException e) {   
	                e.printStackTrace(); 
	          } 
	  
	         for(int i=0;i<cu.getRowNum();i++){ 	
		           String SSCCTag = cu.getString(i,2);//寰楀埌绗琲琛�绗竴鍒楃殑鏁版嵁. 
		         
		           String SiteName = cu.getString(i,19);//寰楀埌绗琲琛�绗簩鍒楃殑鏁版嵁. 
		       
		           String StationId= cu.getString(i,20); 		

	               System.out.println(SSCCTag+"    "+SiteName+"    "+StationId); 
	              try {   
	                   writer.write(SSCCTag+"       "+SiteName+"       "+StationId);  
	                    writer.newLine(); 
	                    writer.flush(); 
	              
	              } catch (IOException e) {   
	                 e.printStackTrace(); 
	               } 
				//  System.out.println("===SSCC Tag:"+SSCCTag); 
				// System.out.println("===Site Name:"+SiteName); 
				//System.out.println("===Station Id:"+StationId); 				
	
	         } 
	         
	         try {   
	             writer.close(); 
	             
	         } catch (IOException e) {   
	                e.printStackTrace(); 
	          } 
	         cu.CsvClose(); 
	} 


}