package com.elend.p2p.file.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.elend.p2p.util.DateUtil;
import com.elend.p2p.util.ObjectMapper;

/**cvs工具类
 * @author tanzl
 *
 */
public class CsvUtil<T> {
	  /** CSV文件列分隔符 */  
    private static final String CSV_COLUMN_SEPARATOR = ",";  
  
    /** CSV文件列分隔符 */  
    private static final String CSV_RN = "\r\n";  
  
    /** 
     *  
    * 将检索数据输出的对应的csv列中 
     * */  
    public static<T> String formatCsvData( List<T> objects,  
            String displayColNames, String matchColNames) {  
    	List<Map<String, Object>> data=new ArrayList<Map<String,Object>>();
    	
    	for (Object object : objects) {
    	        Map<String, Object> o = ObjectMapper.toMap(object, true, null);
    		data.add(o);
		}
    	
        StringBuffer buf = new StringBuffer();  
  
        String[] displayColNamesArr = null;  
        String[] matchColNamesMapArr = null;  
  
        displayColNamesArr = displayColNames.split(",");  
        matchColNamesMapArr = matchColNames.split(",");  
  
       // 输出列头  
       for (int i = 0; i < displayColNamesArr.length; i++) {  
            buf.append(displayColNamesArr[i]).append(CSV_COLUMN_SEPARATOR);  
       }  
       buf.append(CSV_RN);  
 
        if (null != data) {  
            // 输出数据  
           for (int i = 0; i < data.size(); i++) {  
  
                for (int j =0 ; j < matchColNamesMapArr.length; j++) {  
                   buf.append(transfer(data.get(i).get(matchColNamesMapArr[j]))).append(  
                           CSV_COLUMN_SEPARATOR);  
               }  
                buf.append(CSV_RN);  
            }  
        }  
        return buf.toString();  
    }  
    private static String transfer(Object o) {
        if (o instanceof Date) {
            return DateUtil.timeToStr((Date)o, DateUtil.DATE_FORMAT_PATTEN);
        }
        return o == null ? "" : o.toString();
    }
}
