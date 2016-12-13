package cc.ttm.di.mainform;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;

import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;

import javax.swing.JFrame;

import javax.swing.JPanel;
import javax.swing.JButton;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import sun.swing.table.DefaultTableCellHeaderRenderer;



import cc.ttm.di.ServersImpl.DIFileTransImpl;
import cc.ttm.di.common.Keys;
import cc.ttm.di.common.LineNumberHeaderView;
import cc.ttm.di.common.PropertiesUtil;
import cc.ttm.di.model.AppInfo;
import cc.ttm.di.model.MachineInfo;
import cc.ttm.di.model.StdTableModel;



public class MainForm extends JFrame  {
	
	private static final long serialVersionUID = 2814393656135035642L;
	private static  SimpleDateFormat df = new SimpleDateFormat(Keys.DATE_TIME_FORMAT2);
	public  JTable table = new JTable();
	private  JButton DBConfigBtn = new JButton("设置数据库");
	private  JButton BeginScanBtn = new JButton("开始扫描");
	private  JButton StopScanBtn = new JButton("停止扫描");
	private  JButton ExitSysBtn = new JButton("关闭系统");
	 
 
    private JTextField MonitorCount;
    private JTextField MonitorInterval;
    public  JTextField MonitorStatus;    
    public static  JTextArea logInfo;
    
    //public static java.awt.TextArea logInfo;
    
    public  AppInfo  appInfo ;
    public  List<MachineInfo> machineInfos = new ArrayList<MachineInfo>();
	
    public  PropertiesUtil ppUtil  = new PropertiesUtil();
    public  DIFileTransImpl    diTrans;
    public  DBTestDialog  dbDialog ;
    
    /**系统托盘*/ 
    private SystemTray systemTray; 
       
    /**托盘图标*/ 
    private TrayIcon trayIcon; 	
    
	public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
           Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
					new MainForm().setVisible(true);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
	}
	
	public MainForm() {
		super();	
		 initComponents();
		 diTrans = new DIFileTransImpl();
		
	}
	
	public  void initComponents(){	
		
		this.setTitle("DI数据采集系统");
		//this.setLocationRelativeTo(getOwner());
		this.setLocationRelativeTo(null);          
		Toolkit kit = Toolkit.getDefaultToolkit();    // 定义工具包 
	    Dimension screenSize = kit.getScreenSize();   // 获取屏幕的尺寸 
        int screenWidth = screenSize.width/2;         // 获取屏幕的宽 
        int screenHeight = screenSize.height/2;       // 获取屏幕的高 
        int height = this.getHeight(); 
        int width = this.getWidth(); 
        this.setLocation(screenWidth-width/4, screenHeight-height/4); 
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);       
        
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(null, "是否退出该系统?","系统提示", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (option == JOptionPane.YES_OPTION && diTrans.ServiceOpenFlag){
                	 JOptionPane.showMessageDialog(null,"正在上传数据,不能退出系统");
                }
                if (option == JOptionPane.YES_OPTION && diTrans.ServiceOpenFlag ==false)
                    System.exit(0);
            }
        });
             
        //设置图标
        BufferedImage spcimage = null;
        try {        	
          spcimage = ImageIO.read(new FileInputStream(Keys.SysRoot+"/config/monitor.jpg"));         
        } catch (IOException e) {
        	e.printStackTrace();
        }
        this.setIconImage(spcimage);
        
        //检查当前系统是否支持系统托盘	     
	      Image image  =  kit.getDefaultToolkit().getImage(Keys.SysRoot+"/config/monitor.jpg");	              
	  
	      if (SystemTray.isSupported()) { //当前平台是否支持系统托盘 
		        PopupMenu pop = new PopupMenu(); // 构造一个右键弹出式菜单  
		        MenuItem hide = new MenuItem("Hide"); 
		        MenuItem show = new MenuItem("Open ");  
		        MenuItem exit = new MenuItem("Exit");
		    
		        pop.add(hide);  
		        pop.add(show);  
		        pop.add(exit);  
		        
		        trayIcon = new TrayIcon(image, "DI数据采集系统",  pop);   
		        trayIcon.addMouseListener(new java.awt.event.MouseAdapter(){
		        	
		           @Override
		           public void mouseClicked(MouseEvent e) { 
		        	   if (e.getClickCount() == 2) // 鼠标双击  
		               {  
		        		   setExtendedState(JFrame.NORMAL);  
		        		   setVisible(true); // 显示窗口  
		                   toFront();  
		               }  
		           }     
		        });
		        hide.addActionListener(new ActionListener() {  // 点击“隐藏窗口”菜单后将窗口隐藏起来  
		            public void actionPerformed(ActionEvent e) {  
		                setExtendedState(JFrame.NORMAL);  
		                setVisible(false); // 隐藏窗口  
		                toFront();  
		            }  
		        }); 
		        show.addActionListener(new ActionListener(){  // 点击“显示窗口”菜单后将窗口显示出来   
		            public void actionPerformed(ActionEvent e) {  
		                setExtendedState(JFrame.NORMAL);  
		                setVisible(true); // 显示窗口  
		                toFront();  
		            }  
		        });  
		        exit.addActionListener(new ActionListener(){  // 点击“退出演示”菜单后退出程序  
		            public void actionPerformed(ActionEvent e) { 
					        try {
								//DBOptionUtil.close();
							} catch (Throwable e1) {
								e1.printStackTrace();
								showLog(e1.getMessage()+"\n");
							
							}
					        // 记得在程序退出时手动关闭,否则会造成内存泄漏
					        systemTray.remove(trayIcon);
				           // TaskBarMonitor.getInstance().setEnable(false);
					       // tray.remove(trayIcon); // 从系统的托盘实例中移除托盘图标  
				                System.exit(0); // 退出程序  
		            }  
		        });  
		        try{   
		            //获取托盘菜单 
		            systemTray = SystemTray.getSystemTray(); 
		            //添加托盘图标 
		            systemTray.add(trayIcon); 
		          //   tray.add(trayIcon);  // 将 TrayIcon 添加到 SystemTray。 
		        } catch   (AWTException   e)     {   
		        	showLog(e.getMessage()+"\n");
		        }
	      }
		
	      
	      /**
	       * 设计界面
	       * 获取系统配置数据
	       */

	      //初始化Grid
	         readSysConfig();    
	         
	         BorderLayout borderLayout = new BorderLayout(10, 10);
	         this.setLayout(borderLayout);
	      
	         StdTableModel tableModel = new StdTableModel<MachineInfo>(machineInfos);
	         table.setModel(tableModel);
	         
	         //设置表头居中显示
	         DefaultTableCellHeaderRenderer hr = new DefaultTableCellHeaderRenderer();
	         hr.setHorizontalAlignment(JLabel.CENTER);
	         table.getTableHeader().setDefaultRenderer(hr);

	         table.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(30);
	         table.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(34);
	         table.getTableHeader().getColumnModel().getColumn(3).setPreferredWidth(45);
	         table.getColumnModel().getColumn(0).setPreferredWidth(30);
	         table.getColumnModel().getColumn(1).setPreferredWidth(45);
	         table.getColumnModel().getColumn(3).setPreferredWidth(45);
	         
	         /*
	          *       Enumeration<TableColumn> cms = table.getColumnModel().getColumns();
        while(cms.hasMoreElements()){
            cms.nextElement().setPreferredWidth(width);
        }
	          */
	         
	        //对其方式设置  
	         DefaultTableCellRenderer d = new DefaultTableCellRenderer(); 
	         //设置表格单元格的对齐方式为居中对齐方式  
	         d.setHorizontalAlignment(JLabel.CENTER);  
             for(int i = 0; i< table.getColumnCount();i++){  
                 TableColumn col = table.getColumn(table.getColumnName(i));  
                 col.setCellRenderer(d);                
             } 
         
	         JScrollPane scrollPane = new JScrollPane(table);

	         //设定表格在面板上的大小

	         table.setPreferredScrollableViewportSize(new Dimension(this.getWidth(), 100));      

	         scrollPane.setAutoscrolls(true);  

	     	 JPanel headPanel = new JPanel();
	     	 headPanel.setBorder(BorderFactory.createTitledBorder(appInfo.getDivCode()+" 数据采集监控机台清单:"));
	     	 headPanel.setLayout(new BorderLayout());
	     	 headPanel.add(scrollPane,BorderLayout.CENTER);	         
	         
	     	 /*
	     	 JLabel labhead = new JLabel(appInfo.getDivCode()+" DI监控机台清单:",JLabel.CENTER);
	         JPanel panel = new JPanel();
             BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
             panel.setLayout(boxLayout);
             panel.add(labhead);
             panel.add(scrollPane);
             */
             this.add(headPanel,BorderLayout.NORTH);

	         JPanel panelDisplay = new JPanel(); //Ros3
	         
	         JLabel idLab = new JLabel("监控机台数:");
	         MonitorCount  =new JTextField(appInfo.getMachineCount());
	         MonitorCount.setEditable(false); 
	
	         JLabel idLa1 = new JLabel("监控频率(分钟):");
	         MonitorInterval  =new JTextField(appInfo.getInterval());
	         MonitorInterval.setEditable(false); 
	         
	         JLabel idLa2 = new JLabel("系统状态:");
	         MonitorStatus  =new JTextField("已启动");  
	         
	         panelDisplay.add(idLab);
	         panelDisplay.add(MonitorCount);
	         panelDisplay.add(idLa1);
	         panelDisplay.add(MonitorInterval);
	         panelDisplay.add(idLa2);
	         panelDisplay.add(MonitorStatus);	      
	         
	         JPanel panelBtn = new JPanel(); 	//Ros4
	         panelBtn.add(DBConfigBtn);	         
	         panelBtn.add(BeginScanBtn);	         
	         StopScanBtn.setEnabled(false);	         
	         panelBtn.add(StopScanBtn);
	         panelBtn.add(ExitSysBtn);	
	         
	         DBConfigBtn.addActionListener(new MenuListener());
	         BeginScanBtn.addActionListener(new MenuListener());
	         StopScanBtn.addActionListener(new MenuListener());
	         ExitSysBtn.addActionListener(new MenuListener());
	         
	        //  this.add(panelBtn);
    
	         JPanel CenterPanel = new JPanel(new GridLayout(2,1));
	         
	         CenterPanel.add(panelDisplay);
	         CenterPanel.add(panelBtn, BorderLayout.SOUTH);
             
	         /*
	         JPanel panel2 = new JPanel();
             BoxLayout boxLayout2 = new BoxLayout(panel2, BoxLayout.Y_AXIS);
             panel2.setLayout(boxLayout2);
             panel2.add(panelDisplay);
             panel2.add(panelBtn);
             */
             this.add(CenterPanel,BorderLayout.CENTER);
             
	        // JLabel lablog = new JLabel("系统运行日志:");
            // logInfo =  new java.awt.TextArea();
	         logInfo = new JTextArea(); 
	         //logInfo.setPreferredSize(new Dimension(screenWidth-width/4+80, screenHeight-height*2/3-50)); 
	        // logInfo.setSize(new Dimension(screenWidth-width/4+80, screenHeight-height*2/3-50)); 
	         logInfo.setEditable(false);
	
             
         	//设置下载表面板
     		JPanel downloadsPanel = new JPanel();
     		downloadsPanel.setBorder(BorderFactory.createTitledBorder("系统运行日志:"));
     		
     		JScrollPane scroll = new JScrollPane(logInfo);     	
     		scroll.setPreferredSize(new Dimension(screenWidth-width/4+80, screenHeight-height*2/3-55)); 
     		scroll.setSize(new Dimension(screenWidth-width/4+80, screenHeight-height*2/3-55)); 
     		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
     		
     		LineNumberHeaderView lineNumberHeader = new LineNumberHeaderView();
     		scroll.setRowHeaderView(lineNumberHeader);
     		
     		//scrollPane.setAutoscrolls(true); 
     		//scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
     		downloadsPanel.setLayout(new BorderLayout());
     		downloadsPanel.add(scroll,BorderLayout.CENTER);
     		
             
	       //  panel3.add(lablog);
	       //  panel3.add(scrollPaneLog);   
	         this.add(downloadsPanel,BorderLayout.SOUTH);	  
	      
	        pack();
	}

	private class MenuListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			 if(e.getSource()==ExitSysBtn){
				 if(diTrans.ServiceOpenFlag ==false){
	               System.exit(0);
				 }
			 }else if(e.getSource()==DBConfigBtn){  
				 //JOptionPane.showMessageDialog(null,appInfo.getUserName());
				 dbDialog.setVisible(true);	              
	          }else if(e.getSource()==BeginScanBtn){ 
				
				 if(diTrans.ServiceOpenFlag ==false){
					 MonitorStatus.setText("开始扫描");
					 dbDialog.setVisible(false);	    
					 StopScanBtn.setEnabled(true);
					 BeginScanBtn.setEnabled(false);
					 DBConfigBtn.setEnabled(false);		
					 diTrans.startMonitor(appInfo, table);
				 }else{
					 JOptionPane.showMessageDialog(null,"正在运行");
				 }
				
				 
	          }else if(e.getSource()==StopScanBtn){ 	        	  
	        	 diTrans.stopMonitor();
	        	 if(!diTrans.ServiceOpenFlag){
	        		 MonitorStatus.setText("暂停扫描");
		        	 dbDialog.setVisible(false);
					 StopScanBtn.setEnabled(false);
					 BeginScanBtn.setEnabled(true);
					 DBConfigBtn.setEnabled(true);
				 }
	          }
			
		}
		
		
	}
	
	public  void readSysConfig(){
	      if(ppUtil !=null){
	    	  appInfo  = new AppInfo();
	    	
	    	  appInfo.setAppID(ppUtil.getKeyValue("Monitor.AppID"));
	    	  appInfo.setCellIndexs(ppUtil.getKeyValue("Monitor.CellIndexs"));
	    	  
	    	  appInfo.setDivCode(ppUtil.getKeyValue("Monitor.DivCode"));
	    	  appInfo.setMachineCount(ppUtil.getKeyValue("Monitor.MachineCount"));
	    	  appInfo.setInterval(ppUtil.getKeyValue("Monitor.Interval"));
	    	  appInfo.setConnURL(ppUtil.getKeyValue("DB.ConnURL"));
	    	  appInfo.setUserName(ppUtil.getKeyValue("DB.UserName"));;
	    	  appInfo.setPassWord(ppUtil.getKeyValue("DB.PassWord"));
	    	  appInfo.setTargetDirectory(ppUtil.getKeyValue("TargetDirectory"));	 
	    	  
	    	  
	    	  if(appInfo.getMachineCount()==null || "".equals( appInfo.getMachineCount())){
	    		  appInfo.setMachineCount("0");
	    	  }
	    	  
	    	  dbDialog = new DBTestDialog(this, appInfo);
	    	   
	    	  int Rows = Integer.parseInt(appInfo.getMachineCount()); 
	    	  if(machineInfos !=null ){
	    		  machineInfos.clear();  
	    	  }
	    	  
	    	  for(int i=1;i<=Rows;i++){
	    		  
	    		    MachineInfo  currMachInfo = new MachineInfo();
	    		    currMachInfo.setId(i);
	    		    currMachInfo.setMachineNo(ppUtil.getKeyValue("Scan.MachineNo"+i));
	    		    currMachInfo.setHostIP(ppUtil.getKeyValue("Scan.HostIP"+i));
	    		    currMachInfo.setState("未连线");
	    		    currMachInfo.setTargetDirectory(ppUtil.getKeyValue("Scan.TargetDirectory"+i));
	    		    currMachInfo.setFileFormat(ppUtil.getKeyValue("Scan.FileFormat"+i));
	    		    currMachInfo.setLastTime(ppUtil.getKeyValue("Scan.LastTime"+i));
	    		    currMachInfo.setLastCounter(ppUtil.getKeyValue("Scan.LastCounter"+i));
	    		    currMachInfo.setTotalRows(ppUtil.getKeyValue("Scan.TotalRows"+i));
	    		    currMachInfo.setCurrRows(ppUtil.getKeyValue("Scan.CurrRows"+i));
	    		    currMachInfo.setLastCheckDate(ppUtil.getKeyValue("Scan.LastCheckDate"+i));
	    		    currMachInfo.setAppCode(ppUtil.getKeyValue("Scan.AppCode"+i));
	    		    machineInfos.add(currMachInfo);
	    	  }    	  
	    	  
	      }
		
	}
	

	
	public static  synchronized  void showLog(String msg){
		if(logInfo.getLineCount() >=10000){
			//logInfo.removeAll();
			logInfo.setText(""); 
		}
		logInfo.insert(df.format(new Date())+": "+msg+"\n",0);
		
		//logInfo.append(df.format(new Date())+": "+msg+"\n");		
	}
	

}
