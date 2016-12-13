package cc.ttm.di.ServersImpl;


import cc.ttm.di.model.StdTableModel;

public class WorkParam{
	
	private  String sourceDirectory ;
	
	private  String targetDirectory;
	
	private  String machineID;
	
	private  String checkDate;
	
	private  String lastDate;
	
	private  int  currRow;
	
	private  String  lastCounter;
	
	private  String  LastCheckDate;
	
	private  String  formTable;
	
	private  String  formName;
	
	private  String  formCols;
	
	private  String  formID;
	
	private  int  fDatePOS;
	
	private  String  formCode;
	
	private  String  cellsPos;
	
	private  String  divCode;	
	
	private  String  today;
	
	private  String sourceFile ;
	
	private  StdTableModel  tableModel;	
		
	
	public WorkParam(){
		
	}


	public WorkParam(StdTableModel  tableModel,String sourceDirectory,String targetDirectory,String machineID,
			         String checkDate,String lastDate,int  currRow,String  lastCounter,String  LastCheckDate,
			         String  formTable,String  formName,String  formCols,String  formID,int  fDatePOS,
			         String  formCode, String  cellsPos,String  divCode,String  today){
		this.tableModel = tableModel;
		this.sourceDirectory   = sourceDirectory;
		this.targetDirectory   = targetDirectory;
		this.machineID    = machineID;
		this.checkDate    = checkDate;
		this.lastDate     = lastDate;
		this.currRow      = currRow;
		this.lastCounter  = lastCounter;
		this.LastCheckDate= LastCheckDate;
		this.formTable    = formTable;
		this.formCols     = formCols;
		this.formID       = formID;
		this.fDatePOS     = fDatePOS;
		this.formCode     = formCode;
		this.cellsPos     = cellsPos;
		this.divCode      = divCode;
		this.today        = today;
	}
	
	public StdTableModel getTableModel() {
		return tableModel;
	}

	
	public void setTableModel(StdTableModel tableModel) {
		this.tableModel = tableModel;
	}


	public String getSourceDirectory() {
		return sourceDirectory;
	}

	public void setSourceDirectory(String sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	public String getTargetDirectory() {
		return targetDirectory;
	}

	public void setTargetDirectory(String targetDirectory) {
		this.targetDirectory = targetDirectory;
	}

	public String getMachineID() {
		return machineID;
	}

	public void setMachineID(String machineID) {
		this.machineID = machineID;
	}

	public String getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(String checkDate) {
		this.checkDate = checkDate;
	}

	public String getLastDate() {
		return lastDate;
	}

	public void setLastDate(String lastDate) {
		this.lastDate = lastDate;
	}

	public int getCurrRow() {
		return currRow;
	}

	public void setCurrRow(int currRow) {
		this.currRow = currRow;
	}

	public String getLastCounter() {
		return lastCounter;
	}

	public void setLastCounter(String lastCounter) {
		this.lastCounter = lastCounter;
	}

	public String getLastCheckDate() {
		return LastCheckDate;
	}

	public void setLastCheckDate(String lastCheckDate) {
		LastCheckDate = lastCheckDate;
	}

	public String getFormTable() {
		return formTable;
	}

	public void setFormTable(String formTable) {
		this.formTable = formTable;
	}

	public String getFormName() {
		return formName;
	}


	public void setFormName(String formName) {
		this.formName = formName;
	}


	public String getFormCols() {
		return formCols;
	}

	public void setFormCols(String formCols) {
		this.formCols = formCols;
	}

	public String getFormID() {
		return formID;
	}

	public void setFormID(String formID) {
		this.formID = formID;
	}

	public int getfDatePOS() {
		return fDatePOS;
	}

	public void setfDatePOS(int fDatePOS) {
		this.fDatePOS = fDatePOS;
	}

	public String getFormCode() {
		return formCode;
	}

	public void setFormCode(String formCode) {
		this.formCode = formCode;
	}

	public String getCellsPos() {
		return cellsPos;
	}

	public void setCellsPos(String cellsPos) {
		this.cellsPos = cellsPos;
	}

	public String getDivCode() {
		return divCode;
	}

	public void setDivCode(String divCode) {
		this.divCode = divCode;
	}


	public String getToday() {
		return today;
	}


	public void setToday(String today) {
		this.today = today;
	}


	public String getSourceFile() {
		return sourceFile;
	}


	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}
	
	
}