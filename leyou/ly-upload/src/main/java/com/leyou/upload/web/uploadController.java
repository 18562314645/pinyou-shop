package com.leyou.upload.web;

import com.leyou.upload.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 图片上传
 */
@RestController
@RequestMapping("upload")
@Slf4j
public class uploadController {

    @Autowired
    private UploadService uploadService;

    @PostMapping("image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
          //1.获取下载地址
          String url=uploadService.upload(file);
          System.out.println(url);
        //判断地址是否为空
        if(StringUtils.isBlank(url)){
            //url为空，上传失败
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }

        //否则返回200，携带url
            return ResponseEntity.ok(url);
    }
}
