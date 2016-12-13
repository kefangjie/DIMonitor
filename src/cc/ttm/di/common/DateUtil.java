package cc.ttm.di.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
* @author   jieke
* @version  1.0
*/

public class DateUtil {
   
	public DateUtil(){}
	
    public String SetDateFormat(String myDate,String strFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
        String sDate = sdf.format(sdf.parse(myDate));

        return sDate;        
    }
  
    public static String AddDateTime(String myDate, int dayNum ,int Type ,String Format) {
    	Date  date=null ;
	    SimpleDateFormat sdf = new SimpleDateFormat(Format);
        try {
    	    Calendar   ca   =   new   GregorianCalendar();
    	    //System.out.println(Type +"="+dayNum+"-"+Format);
			ca.setTime(sdf.parse(myDate));
	        ca.add(Type,dayNum);
	        date=ca.getTime(); 
		} catch (ParseException e) {			
			e.printStackTrace();
		}finally{
			 return  sdf.format(date);
		}       
    } 
    
    public String DayAdd(String myDate, int dayNum) throws Exception {
    	Date  date=null ;
	    SimpleDateFormat sdf = new SimpleDateFormat(Keys.DATE_FORMAT);	
	    
	    Calendar   ca   =   new   GregorianCalendar();				
        ca.setTime(sdf.parse(myDate));
        ca.add(ca.DATE,dayNum);
        date=ca.getTime(); 
        
        return  sdf.format(date);
    } 
    
    public String MonthAdd(String myDate, int monthNum) throws Exception{
    	Date  date=null ;
	    SimpleDateFormat sdf = new SimpleDateFormat(Keys.DATE_FORMAT);	
	    Calendar   ca   =   new   GregorianCalendar();				
        ca.setTime(sdf.parse(myDate));
        ca.add(ca.MONTH,monthNum);
        date=ca.getTime(); 
        return  sdf.format(date);
    }    
    
    public boolean isLeapYear(int yearNum){
        boolean isLeep = false;
        if((yearNum % 4 == 0) && (yearNum % 100 != 0)){
            isLeep = true;
        }  else if(yearNum % 400 ==0){
            isLeep = true;
        } else {
            isLeep = false;
        }
        return isLeep;
   }

    /**  
     * 计算两个日期之间相差的天数  
     * @param smdate 较小的时间 
     * @param bdate  较大的时间 
     * @return 相差天数 
     * @throws ParseException  
     */    
    public  int daysBetween(Date smdate,Date bdate) throws ParseException    
    {    
        SimpleDateFormat sdf=new SimpleDateFormat(Keys.DATE_FORMAT2);  
        smdate=sdf.parse(sdf.format(smdate));  
        bdate=sdf.parse(sdf.format(bdate));  
        Calendar cal = Calendar.getInstance();    
        cal.setTime(smdate);    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(bdate);    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
            
       return Integer.parseInt(String.valueOf(between_days));           
    }    
      
	/** 
		*字符串的日期格式的计算 
     */  
    public static  int daysBetween(String smdate,String bdate) { 
        long between_days =0;
    	try{
	        SimpleDateFormat sdf=new SimpleDateFormat(Keys.DATE_FORMAT2);  
	        Calendar cal = Calendar.getInstance();    
	        cal.setTime(sdf.parse(smdate));    
	        long time1 = cal.getTimeInMillis();                 
	        cal.setTime(sdf.parse(bdate));    
	        long time2 = cal.getTimeInMillis();
	        
	         between_days=(time2-time1)/(1000*3600*24);  
    	}catch (ParseException e) {			
			e.printStackTrace();
		} 
    	
       return Integer.parseInt(String.valueOf(between_days));     
    }    
    
    
    public int getWeekNumOfYearDay(String strDate ) throws ParseException{
     
    	Calendar calendar = Calendar.getInstance(); 
        SimpleDateFormat format = new SimpleDateFormat(Keys.DATE_FORMAT);
        Date curDate = format.parse(strDate);
        calendar.setTime(curDate);
        int iWeekNum = calendar.get(Calendar.WEEK_OF_YEAR);
        return iWeekNum;
    }
    
    public static String getCycle(String strDate ,String Format) throws ParseException{
        
    	Calendar calendar = Calendar.getInstance(); 
        SimpleDateFormat format = new SimpleDateFormat(Format);
        Date curDate = format.parse(strDate);
        calendar.setTime(curDate);
        int iWeekNum = calendar.get(Calendar.WEEK_OF_YEAR);
        
        SimpleDateFormat format2 = new SimpleDateFormat(Keys.DATE_FORMAT6);
         String Cycle =  format2.format(format.parse(strDate)) + iWeekNum;
         
        return Cycle;
    }
    
    public String getYearWeekFirstDay(int yearNum,int weekNum) throws ParseException {
     
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, yearNum);
        cal.set(Calendar.WEEK_OF_YEAR, weekNum);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        String tempYear = Integer.toString(yearNum);
        String tempMonth = Integer.toString(cal.get(Calendar.MONTH) + 1);
        if ( tempMonth.length()<2){
    	    tempMonth="0"+tempMonth;
        }
        String tempDay = Integer.toString(cal.get(Calendar.DATE));
        if ( tempDay.length()<2){
    	  tempDay="0"+tempDay;
        }
        String tempDate = tempMonth + "/" +tempDay + "/" + tempYear;
        return SetDateFormat(tempDate,Keys.DATE_FORMAT);     
    }

    public String getYearWeekEndDay(int yearNum,int weekNum) throws ParseException {
   
    	 Calendar cal = Calendar.getInstance();
         cal.set(Calendar.YEAR, yearNum);
         cal.set(Calendar.WEEK_OF_YEAR, weekNum + 1);
         cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
     
         String tempYear = Integer.toString(yearNum);
         String tempMonth = Integer.toString(cal.get(Calendar.MONTH) + 1);
         if ( tempMonth.length()<2){
     	    tempMonth="0"+tempMonth;
         }         
         String tempDay = Integer.toString(cal.get(Calendar.DATE));
         if ( tempDay.length()<2){
       	     tempDay="0"+tempDay;
         }
         String tempDate = tempMonth + "/" +tempDay + "/" + tempYear;
         return SetDateFormat(tempDate,Keys.DATE_FORMAT);  
    }
   
   
    public String getYearMonthFirstDay(int yearNum,int monthNum) throws ParseException {
     
         String tempYear = Integer.toString(yearNum);
         String tempMonth = Integer.toString(monthNum);
         if ( tempMonth.length()<2){
      	    tempMonth="0"+tempMonth;
          }         
         String tempDay = "01";
         String tempDate = tempMonth + "/" +tempDay + "/" + tempYear;
         return SetDateFormat(tempDate,Keys.DATE_FORMAT);  
     
    }

    public String getYearMonthEndDay(int yearNum,int monthNum) throws ParseException {
        String tempYear = Integer.toString(yearNum);
        String tempMonth = Integer.toString(monthNum);
        String tempDay = "31";
       if (tempMonth.equals("1") || tempMonth.equals("3") || tempMonth.equals("5") || tempMonth.equals("7") ||tempMonth.equals("8") || tempMonth.equals("10") ||tempMonth.equals("12")) {
         tempDay = "31";
       }
       if (tempMonth.equals("4") || tempMonth.equals("6") || tempMonth.equals("9")||tempMonth.equals("11")) {
         tempDay = "30";
       }
      if (tempMonth.equals("2")) {
         if (isLeapYear(yearNum)) {
            tempDay = "29";
         } else {
            tempDay = "28";
         }
      }
      
      if ( tempMonth.length()<2){
    	    tempMonth="0"+tempMonth;
      } 
      
      String tempDate = tempMonth + "/" +tempDay + "/" + tempYear;
      return SetDateFormat(tempDate,Keys.DATE_FORMAT);

    }

	
}
