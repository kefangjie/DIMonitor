package cc.ttm.di.mainform;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.hibernate.MappingException;


import cc.ttm.di.common.DBOptionUtil;

import cc.ttm.di.common.PropertiesUtil;
import cc.ttm.di.model.AppInfo;

public class DBTestDialog  extends JDialog{
	
    private JButton test,submit,cancel;
    private boolean ok;
    
    private JTextField DBUrl ;
    private JTextField UserName ;
    private JPasswordField UserPass ;
    
    
    public  PropertiesUtil ppUtil  = new PropertiesUtil();
    public  DBOptionUtil   dbUtil  = new cc.ttm.di.common.DBOptionUtil(); 
 
    public  DBTestDialog(){}
    
	public DBTestDialog(JFrame parent,AppInfo appInfo){
			 super(parent,"数据库连接设置",true);
			 setLocationRelativeTo(parent);
			 setSize(350, 220 );	
		    //setLocation(180, 150);
		     GridLayout gridLayout = new GridLayout(4, 1, 5, 5);  
		     
		     setLayout(gridLayout);
		     
			 JLabel idLab = new JLabel("  连接URL:",JLabel.RIGHT);
			 DBUrl = new JTextField(100);
			 DBUrl.setPreferredSize(new Dimension(50, 20));
			 
			 JPanel row1 = new JPanel(); 
		     BoxLayout boxLayout1 = new BoxLayout(row1, BoxLayout.X_AXIS);
		     row1.setLayout(boxLayout1);
			 row1.add(idLab);
			 row1.add(DBUrl);
			 add(row1);  
			 
		     JLabel nameLab = new JLabel("  用 户 名:",JLabel.RIGHT);   
			 JPanel row2 = new JPanel(); 
		     BoxLayout boxLayout2 = new BoxLayout(row2, BoxLayout.X_AXIS);    
			 UserName = new JTextField(30);
			 UserName.setPreferredSize(new Dimension(50, 20));
			 row2.setLayout(boxLayout2);
			 row2.add(nameLab);
			 row2.add(UserName);
			 add(row2);  
		
			 JPanel row3 = new JPanel(); 
		     JLabel passwordLab = new JLabel("  密       码:",JLabel.RIGHT);  
		     UserPass = new JPasswordField(30);
		     UserPass.setPreferredSize(new Dimension(50, 20));
		     BoxLayout boxLayout3 = new BoxLayout(row3, BoxLayout.X_AXIS);
		     row3.setLayout(boxLayout3);	 
			 row3.add(passwordLab);
			 row3.add(UserPass);
			 add(row3);  	 
			
		     if(appInfo !=null){
		    	 DBUrl.setText(appInfo.getConnURL());
		    	 UserName.setText(appInfo.getUserName());
		    	 UserPass.setText(appInfo.getPassWord());
		     }else{
		    	 DBUrl.setText("jdbc:oracle:thin:@10.22.238.235:1521:DEV1");
		    	 UserName.setText("WEBERP_GME_DEV");
		    	 UserPass.setText("WEBERP_GME_DEV");   	 
		     }
		     
		     test=new JButton("测试");
		     test.addActionListener(new ButtonListener());     
		     submit=new JButton("确认");
		     submit.addActionListener(new ButtonListener());
		     cancel=new JButton("取消");     
		     cancel.addActionListener(new ButtonListener());
		   
		     FlowLayout flowLayout2 = new FlowLayout(FlowLayout.CENTER);       
		     JPanel row4 = new JPanel(); 
		     row4.setLayout(flowLayout2); 
		     row4.add(test);
		     row4.add(submit);
		     row4.add(cancel);    
		     add(row4);  
		     
		    // pack();
	}

    private class ButtonListener implements ActionListener{
        public void actionPerformed (ActionEvent e){ 
        	if(e.getSource() == test){
        		try {
        			if(!"".equals(DBUrl.getText()) &&  !"".equals(UserName.getText()) &&  !"".equals(UserPass.getText())  ){ 
				        
				          dbUtil.dataSourceConf(DBUrl.getText().trim(),  UserName.getText().trim(), UserPass.getText().trim());
				    
				    }else{
				    	JOptionPane.showMessageDialog(null,"请输入配置信息");
				    }
				} catch (MappingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        	}else{
	            if(e.getSource()==submit){
	                ok=true;
	                if(ppUtil!=null){
	                	ppUtil.updateProperties("DB.ConnURL", DBUrl.getText().trim());
	                	ppUtil.updateProperties("DB.UserName", UserName.getText().trim());
	                	ppUtil.updateProperties("DB.PassWord", UserPass.getText().trim());
	                }
	                MainForm.showLog("数据库连接配置正确.");
	            }else  if(e.getSource()==cancel){
	                ok=false;
	            }
	            dispose();
            }
        }
    } 

}
