package cc.ttm.di.model;

public class AppInfo {	
 
	 private  String appID; 

	 private  String  cellIndexs;
	
     private  String divCode;
     
     private  String machineCount;
     
     private  String  interval;
     
     private  String  connURL;
     
     private  String userName;
     
     private  String  passWord;
     
     
     private  String targetDirectory;
          
     public AppInfo(){}
     
     public AppInfo(String appID,String  cellIndexs,String divCode,String machineCount,String  interval,String  connURL,String  passWord,String targetDirectory){
    	 this.appID = appID;
    	 this.cellIndexs = cellIndexs;
    	 this.divCode = divCode;
    	 this.machineCount = machineCount;
    	 this.interval = interval;
    	 this.connURL =  connURL;
    	 this.passWord = passWord;
    	 this.targetDirectory = targetDirectory;
     }
     
	 public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}

	public String getCellIndexs() {
		return cellIndexs;
	}

	public void setCellIndexs(String cellIndexs) {
		this.cellIndexs = cellIndexs;
	}     

	public String getDivCode() {
		return divCode;
	}

	public void setDivCode(String divCode) {
		this.divCode = divCode;
	}

	public String getMachineCount() {
		return machineCount;
	}

	public void setMachineCount(String machineCount) {
		this.machineCount = machineCount;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	public String getConnURL() {
		return connURL;
	}

	public void setConnURL(String connURL) {
		this.connURL = connURL;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getTargetDirectory() {
		return targetDirectory;
	}

	public void setTargetDirectory(String targetDirectory) {
		this.targetDirectory = targetDirectory;
	} 
}
