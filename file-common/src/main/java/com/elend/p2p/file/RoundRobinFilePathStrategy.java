package com.elend.p2p.file;

import java.io.File;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.elend.p2p.file.service.FileService.FileType;

/**
 * 轮询写策略
 * @author liyongquan 2016年11月24日
 *
 */
@Component
public class RoundRobinFilePathStrategy implements FileServerPathStrategy{
    @Autowired
    private FilePartition filePartition;
    /**
     * 最后写入的分区
     */
    private int lastWrite=0;

    @Override
    public String getPath(String appId,FileType fileType){
        Calendar cal = Calendar.getInstance();
        StringBuffer path = new StringBuffer(filePartition.getBasePath()
                + File.separator);
        path.append(fileType.name().toLowerCase()+File.separator);
        //得到下一个要写入的分区
        lastWrite++;
        lastWrite=lastWrite%filePartition.getPartitionList().length;
        path.append(filePartition.getPartitionList()[lastWrite]+File.separator);
        /*path.append(appId);
        path.append(File.separator);*/
        path.append(cal.get(Calendar.YEAR));
        path.append(File.separator);
        path.append(cal.get(Calendar.MONTH) + 1);
        path.append(File.separator);
        return path.toString();
    }
}
