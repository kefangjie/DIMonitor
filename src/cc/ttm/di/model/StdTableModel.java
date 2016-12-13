package cc.ttm.di.model;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.table.AbstractTableModel;



@SuppressWarnings("serial")
public class StdTableModel<T> extends AbstractTableModel {
    private List<T> objs;
    private BeanInfo beanInfo;
    private Map<Integer, String> columnInfo = null;
    private Map<Integer, Integer> propertyInfo = null;
    private PropertyDescriptor[] pd = null;
    private int columnCount;
    @SuppressWarnings("unused")
    private Class<T> clazz;

    public StdTableModel() {
        try {
            columnInfo = new TreeMap<Integer, String>();
            propertyInfo = new HashMap<Integer, Integer>();
            Field[] fields = getClz().getDeclaredFields();
            beanInfo = Introspector.getBeanInfo(getClz());
            pd = beanInfo.getPropertyDescriptors();
            for (Field f : fields) {
                if (f.isAnnotationPresent(BeanColumn.class)) {
                    // 杩欓噷娌℃湁鐩存帴鍐欐垚columnCOunt = fileds.length鏄洜涓哄彲鑳芥煇浜涘瓧娈典笉鐢ㄦ潵鏄剧ず
                    columnCount++;
                    // 鑾峰彇鍒癆nnotation
                    BeanColumn bc = f.getAnnotation(BeanColumn.class);
                    // 鑾峰彇鍒拌灞炴�瀵瑰簲鐨勫垪鍚嶇О
                    String columnName = bc.name();
                    // 鑾峰彇璇ュ悕绉板湪Table涓殑绱㈠紩鍊�
                   int index = bc.index();
                    // 閫氳繃TreeMap灏嗗垪鍚嶇О浠ュ強瀹冪殑绱㈠紩瀛樺偍璧锋潵,鐢ㄦ潵鏄剧ず琛ㄥご淇℃伅
                    columnInfo.put(index, columnName);
                    /*
                     * 鍒ゆ柇璇ュ睘鎬у湪beanInfo涓殑绱㈠紩
                     * 鏈�悗鏄剧ず鏄�杩嘽olumnIndex--PropertyDescriptor鏁扮粍涓殑绱㈠紩锛�
                     * 鐒跺悗鑾峰彇鍒癙ropertyDescriptor鏉ヨ幏鍙栧埌鍏蜂綋鐨勬暟鎹�
                     */
                    for (int i = 0; i < pd.length; i++) {
                        String fieldName = null;
                        if (f.getName().startsWith("is")) {
                            fieldName = f
                                    .getName()
                                    .substring(
                                            f.getName().indexOf("is")
                                                    + "is".length())
                                    .toLowerCase();
                       } else {
                           fieldName = f.getName();
                        }
                        if (fieldName.equals(pd[i].getName())) {
                            propertyInfo.put(index, i);
                       }
                    }
               }
           }
       } catch (IntrospectionException e) {
           e.printStackTrace();
       }
   }

  public StdTableModel(List<T> list) {
        this();
        this.objs = list;   
        this.fireTableRowsInserted(1,getRowCount());
    }

    
    /**
     * 鑾峰彇鍒版硾鍨嬩腑鐨凜lass瀵硅薄
     * 杩欓噷杩樻湭瑙ｅ喅锛屾殏鏃跺厛鍐欐
    * @return
    */
    @SuppressWarnings("unchecked")
    private Class<T> getClz() {
       return (Class<T>) MachineInfo.class;
   	  /**getClass().getGenericSuperclass()杩斿洖琛ㄧず姝�Class 鎵�〃绀虹殑瀹炰綋锛堢被銆佹帴鍙ｃ�鍩烘湰绫诲瀷鎴�void锛�
         * 鐨勭洿鎺ヨ秴绫荤殑 Type(Class<T>娉涘瀷涓殑绫诲瀷)锛岀劧鍚庡皢鍏惰浆鎹arameterizedType銆傘� 
         *  getActualTypeArguments()杩斿洖琛ㄧず姝ょ被鍨嬪疄闄呯被鍨嬪弬鏁扮殑 Type 瀵硅薄鐨勬暟缁勩� 
         *  [0]灏辨槸杩欎釜鏁扮粍涓涓�釜浜嗐�銆�
         *  绠��瑷�箣灏辨槸鑾峰緱瓒呯被鐨勬硾鍨嬪弬鏁扮殑瀹為檯绫诲瀷銆傘�*/  
     
   	// return (Class<T>) ((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]; 
    }

    /**
     * 杩斿洖瀵硅薄闆嗗悎
     * 
     * @return
     */
    public List<T> getObjs() {
        return objs;
    }

    /**
     * 璁剧疆瀵硅薄闆嗗悎
     *       * 
     * @param objs
    */
    public void setObjs(List<T> objs) {
   	 this.objs = objs;
    }

    /**
     *       * 鑾峰彇鎬荤殑琛屾暟
     */
  @Override
    public int getRowCount() {
        if (objs != null) {
            return objs.size();
        } else {
            return 0;
        }
    }

    /**
     * 鑾峰彇鎬荤殑鍒楁暟
     */
   @Override
    public int getColumnCount() {
        return columnCount;
    }

    /**
     * 杩斿洖鍗曞厓鏍肩殑鏁版嵁鍋氭樉绀�
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            if (objs != null) {
                // 鑾峰彇鍒拌鏁版嵁
                T t = objs.get(rowIndex);
                Integer propertyIndex = propertyInfo.get(columnIndex);
                return pd[propertyIndex].getReadMethod().invoke(t,
                        new Object[] {});
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 杩斿洖绫荤殑鍚嶇О
     */
    @Override
    public String getColumnName(int column) {
        return columnInfo.get(column);
    }

    /**
     * 杩斿洖TableCellRender娓叉煋鐨勭被鍨�
     */
   @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (pd != null) {
       	    //System.out.println("Col"+columnIndex+"=="+pd[propertyInfo.get(columnIndex)].getPropertyType());
            return pd[propertyInfo.get(columnIndex)].getPropertyType();
        }
        return Object.class;
    }

    /**
     * DefaultTableModel搴曞眰涔熸槸杩欐牱鍘诲畬鎴愮殑
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            T t = objs.get(rowIndex);
            int propIndex = propertyInfo.get(columnIndex);
            pd[propIndex].getWriteMethod().invoke(t, new Object[] { aValue });
            // 褰撴暟鎹洿鏂板畬鎴愪箣鍚庡畬鎴愭洿鏂拌鍥惧眰
            fireTableCellUpdated(rowIndex, columnIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 璁剧疆鏄惁鍙互缂栬緫
     */
   @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addRow(T t) {
        if (t == null) {
            throw new RuntimeException("娣诲姞澶辫触");
        }       
        objs.add(t);
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    /**
     * 鎻愪緵閲嶈浇鏂规硶锛岃鐢ㄦ埛鍘昏皟鐢�
     * 
     * @param data
     */
    public void addRow(List<Object> data) {
        if(data !=null){
       	 //
        }
    }

    public void addRow(Object[] data) {
           //
    }
    
    /**
     * 鏍规嵁瀵硅薄鏉ュ垹闄�
     * 姝ゆ椂闇�閲嶅啓瀵硅薄鐨別quals鍜宧ashCode鏂规硶,鍥犱负搴曞眰ArrayLiST鍒ゆ柇瀵硅薄鏄惁
     * 鐩哥瓑鏄�杩噀quals鏂规硶鏉ヨ繘琛屾瘮杈�
     * @param t
     */
    public void deleteRow(T t){
        this.objs.remove(t);
        fireTableRowsDeleted(this.getColumnCount(),this.getColumnCount());
    }     
    /**
     * 鏍规嵁琛屾潵鍒犻櫎
     * @param rowIndex
     */
    public void deleteRow(int rowIndex){
        this.objs.remove(rowIndex);
        fireTableRowsDeleted(this.getColumnCount(),this.getColumnCount());
    }

    public T getObjbyRowIndex(int rowIndex){
        return objs.get(rowIndex);
    }
    
    /**
     * 鏇存柊琛屾暟鎹�
     * @param rowIndex
     * @param t
    */
    public void update(int rowIndex,T t){
        this.objs.set(rowIndex, t);
        fireTableRowsUpdated(this.getColumnCount() - 1, this.getRowCount() - 1);
    }
    
}
