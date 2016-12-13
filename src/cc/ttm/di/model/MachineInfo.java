package cc.ttm.di.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MachineInfo implements PropertyChangeListener{
	
	 
	  @BeanColumn(name = "序号", index = 0)
	  private Integer id;	  
	  
	  @BeanColumn(name = "机台号(#)", index = 1)
	  private String machineNo;
	  
	  @BeanColumn(name = "主机IP", index = 2)
	  private String hostIP;
	  
	  @BeanColumn(name = "状态", index = 3)
	  private String state;
	  
	  @BeanColumn(name = "扫描目录", index = 4)
	  private String targetDirectory;
	 
	  @BeanColumn(name = "文件格式", index = 5)
	  private String fileFormat;

	  @BeanColumn(name = "最后上传时间", index = 6)
	  private String lastTime;	  
	  
	  @BeanColumn(name = "最后上传ID", index = 7)
	  private String lastCounter;
	  
	  @BeanColumn(name = "累计上传行数", index = 8)
	  private String totalRows;
	  
	  @BeanColumn(name = "本次上传行数", index = 9)
	  private String currRows;	  

	  @BeanColumn(name = "最近检查时间", index = 10)
	  private String lastCheckDate;
	  
	  @BeanColumn(name = "App Code", index = 11)
	  private String appCode;	  
	  
	  public MachineInfo() {}
	  
	  public MachineInfo(Integer id,String MachineNo,String HostIP,String State,
			             String TargetDirectory,String FileFormat,String LastTime,
			             String LastCounter,String TotalRows,String CurrRows,String lastCheckDate,String appCode){
		  this.id = id;
		  this.machineNo = MachineNo;
		  this.hostIP = HostIP;
		  this.state = State;
		  this.targetDirectory  = TargetDirectory;
		  this.fileFormat   = FileFormat;
		  this.lastTime     = LastTime;
		  this.lastCounter  = LastCounter;
		  this.totalRows =  TotalRows;
		  this.currRows  =  CurrRows;
		  this.lastCheckDate = lastCheckDate;
		  this.appCode   =  appCode;
	  } 



	public int hashCode() {
		    final int prime = 31;
		    int result = 1;
		    result = prime * result + ((id == null) ? 0 : id.hashCode());
		    return result;
	  }


	  //@Override
	  public boolean equals(Object obj) {
	    if (this == obj)
	      return true;
	    if (obj == null)
	      return false;
	    if (getClass() != obj.getClass())
	      return false;
	    MachineInfo other = (MachineInfo) obj;
	    if (id == null) {
	      if (other.id != null)
	        return false;
	    } else if (!id.equals(other.id))
	      return false;
	    return true;
	  }


	  public void printName() {
		System.out.println(MachineInfo.class.getName());
		
	  }
  
	  
	  
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMachineNo() {
		return machineNo;
	}

	public void setMachineNo(String machineNo) {
		this.machineNo = machineNo;
	}

	public String getHostIP() {
		return hostIP;
	}

	public void setHostIP(String hostIP) {
		this.hostIP = hostIP;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTargetDirectory() {
		return targetDirectory;
	}

	public void setTargetDirectory(String targetDirectory) {
		this.targetDirectory = targetDirectory;
	}

	public String getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public String getLastCounter() {
		return lastCounter;
	}

	public void setLastCounter(String lastCounter) {
		this.lastCounter = lastCounter;
	}

	public String getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(String totalRows) {
		this.totalRows = totalRows;
	}

	public String getCurrRows() {
		return currRows;
	}

	public void setCurrRows(String currRows) {
		this.currRows = currRows;
	}

    public String getLastCheckDate() {
		return lastCheckDate;
	}

	public void setLastCheckDate(String lastCheckDate) {
		this.lastCheckDate = lastCheckDate;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}






}
