package com.heima.wemedia.test;

import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenTextScan;
import com.heima.file.service.FileStorageService;
import com.heima.wemedia.WemediaApplication;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Map;

/**
 * @description: 图片 文本测试
 * @ClassName: AliyunTest
 * @author: Zle
 * @date: 2022-06-27 15:44
 * @version 1.0
*/
@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
public class AliyunTest {

    @Autowired
    private GreenTextScan greenTextScan;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private GreenImageScan greenImageScan;

    /**
     * @description: 测试文本内容审核
     * @author Zle
     * @date 2022/6/27 15:50
     */
    @Test
    public void testScanText() throws Exception {
        Map map = greenTextScan.greeTextScan("傻逼");
        System.out.println(map);
    }

    /**
     * @description: 测试图片内容审核
     * @author Zle
     * @date 2022/6/27 15:51
     */
    @Test
    public void testScanImage() throws Exception {
        byte[] bytes = fileStorageService.downLoadFile("http://192.168.200.130:9000/leadnews/2022/06/26/1a608e5961924252ba8bb1971fa5a210.jpg");

        ArrayList<byte []> list = new ArrayList<byte []>();
        list.add(bytes);

        Map map = greenImageScan.imageScan(list);
        System.out.println(map);

    }
}
