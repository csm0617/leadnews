package com.heima.minio;

import com.heima.file.service.FileStorageService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;

@SpringBootTest(classes = MinIOApplication.class)
@RunWith(SpringRunner.class)
public class MinIOTest {
    @Autowired
    private FileStorageService fileStorageService;

    @Test
    public void test() throws Exception {
        FileInputStream fileInputStream =  new FileInputStream("D:\\111.pdf");;
        String path = fileStorageService.uploadFile("", "111.pdf", fileInputStream);
        System.out.println(path);
    }



    public static void main(String[] args) {

        FileInputStream fileInputStream = null;
        try {

            fileInputStream =  new FileInputStream("D:\\plugins\\js\\axios.min.js");;

            //1.创建minio链接客户端
            MinioClient minioClient = MinioClient.builder().credentials("minio", "minio123").endpoint("http://192.168.200.130:9000").build();
            //2.上传
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object("plugins/js/axios.min.js")//文件名
//                    .contentType("application/octet-stream")//文件类型
                    .contentType("text/js")//文件类型
                    .bucket("leadnews")//桶名词  与minio创建的名词一致
                    .stream(fileInputStream, fileInputStream.available(), -1) //文件流
                    .build();
            minioClient.putObject(putObjectArgs);

//            System.out.println("http://192.168.200.130:9000/leadnews/111.pdf");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}