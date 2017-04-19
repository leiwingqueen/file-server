package com.elend.p2p.file;

import java.io.File;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.elend.p2p.file.exception.PatitionNotFoundException;

@Component
public class SimpleFilePathStrategy implements FilePathStrategy {
    @Autowired
    private Partition partition;

    @Override
    public String getPath(String appId, int partitionId)
            throws PatitionNotFoundException {
        if (partitionId < 0
                || partitionId > partition.getPartitionList().size() - 1) {
            throw new PatitionNotFoundException();
        }
        String par = partition.getPartitionList().get(partitionId);
        Calendar cal = Calendar.getInstance();
        StringBuffer path=new StringBuffer();
        path.append(pathFormat(par));
        path.append(appId);
        path.append(File.separator);
        path.append(cal.get(Calendar.YEAR));
        path.append(File.separator);
        path.append(cal.get(Calendar.MONTH)+1);
        path.append(File.separator);
        return path.toString();
    }

    /**
     * 路径格式化，保证以'/'结尾
     * 
     * @param path
     * @return
     */
    private String pathFormat(String path) {
        String lastChar = String.valueOf(path.charAt(path.length() - 1));
        if (!lastChar.equals(File.separator)) {
            return path + File.separator;
        }
        return path;
    }

    @Override
    public String getDelPath(String appId) {
        Calendar cal = Calendar.getInstance();
        StringBuffer path=new StringBuffer();
        path.append(pathFormat(partition.getDelPath()));
        path.append(appId);
        path.append(File.separator);
        path.append(cal.get(Calendar.YEAR));
        path.append(File.separator);
        path.append(cal.get(Calendar.MONTH)+1);
        path.append(File.separator);
        return path.toString();
    }

}
