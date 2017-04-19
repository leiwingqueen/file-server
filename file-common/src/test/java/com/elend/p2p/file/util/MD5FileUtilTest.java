package com.elend.p2p.file.util;

import java.io.IOException;

import org.junit.Test;

public class MD5FileUtilTest {

    @Test
    public void test() {
        String path="D:/data1/file_manage/partition2/p2p_web/2015/3/";
        //String fileName="5051908e-280a-462b-9f88-ba06972181e2.png";
        String fileName="d95e02ea-644f-4b87-9c49-0f3218e46d10.png";
        try {
            String md5=MD5FileUtil.getFileMD5String(path+fileName);
            System.out.println(md5);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
