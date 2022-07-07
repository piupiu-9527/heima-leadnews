package com.heima.minio.test;

import com.heima.file.service.FileStorageService;
import com.heima.minio.MinIOApplication;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
* @description:
* @ClassName MinIOTest
* @author Zle
* @date 2022-06-24 17:51
* @version 1.0
*/
@SpringBootTest(classes = MinIOApplication.class)
@RunWith(SpringRunner.class)
public class MinIOTest {


    

    @Autowired
    private FileStorageService fileStorageService;

    /**
    * @description: 把list.html上传到minio中，并且可以通过浏览器访问
    * @param: []
    * @return: void
    * @author Zle
    * @date: 2022-06-24 22:14
    */
/*    @Test
    public void test() throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("D:\\template\\list.html");
        String path = fileStorageService.uploadHtmlFile("", "list.html", fileInputStream);
        System.out.println(path);
    }*/











    /**
    * @description: 把list.html上传到minion中，并且可以通过浏览器访问
    * @param: [args]
    * @return: void
    * @author Zle
    * @date: 2022-06-24 17:53
    */
    public static void main(String[] args) {

        try {
            FileInputStream fileInputStream = new FileInputStream("D:\\template\\js\\index.js");

            //1. 获取minio的连接信息，创建一个minio的客户端 用户名 密码 连接地址
            MinioClient minioClient = MinioClient.builder().credentials("minio", "minio123").endpoint("http://192.168.200.130:9000").build();
            //2.上传
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object("plugins/js/index.js")  //文件名称
                    .contentType("text/js")  //文件类型
                    .bucket("leadnews")  //桶名称  与minio管理界面创建的桶一致即可
                    .stream(fileInputStream,fileInputStream.available(),-1).build();
            minioClient.putObject(putObjectArgs);
            //访问路径
//            System.out.println("http://192.168.200.130:9000/leadnews/list.html");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
