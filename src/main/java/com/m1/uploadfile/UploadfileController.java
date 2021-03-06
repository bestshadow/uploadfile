package com.m1.uploadfile;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by qcl on 2019-06-11
 * desc: 文件上传
 */
@RestController
public class UploadfileController {

    @PostMapping("/upload")
    public String upload(MultipartFile uploadFile, HttpServletRequest request) {
        /*
         定义文件的存储路径,如下，是在linux和mac上定义的文件路径
        /private/var/folders/8x/4zvnbqmj1w33cqmzrpygzbth0000gn/T/tomcat-docbase.5206733816001100271.8080/uploadFile
         */

        String realPath = request.getSession().getServletContext().getRealPath("/uploadFile/");
        File dir = new File(realPath);
        if (!dir.isDirectory()) {//文件目录不存在，就创建一个
            dir.mkdirs();
        }

        try {
            String filename = uploadFile.getOriginalFilename();
            //服务端保存的文件对象
            File fileServer = new File(dir, filename);
            System.out.println("file文件真实路径:" + fileServer.getAbsolutePath());
            //2，实现上传
            uploadFile.transferTo(fileServer);
            String filePath = request.getScheme() + "://" +
                    request.getServerName() + ":"
                    + request.getServerPort()
                    + "/uploadFile/" + filename;
            //3，返回可供访问的网络路径
            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "上传失败";
    }

    @ControllerAdvice
    public class MyExceptionHandler{

        @ResponseBody
        @ResponseStatus
        @ExceptionHandler(Exception.class)
        public String handlerMethodArgumentException(Exception e){
            System.out.println(e.getClass().getName());
            if (e instanceof MaxUploadSizeExceededException) {
                return "文件过大";
            }
            return "错误信息" + e.toString();
        }
    }
}
