package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.LyException;
import com.leyou.upload.config.UplaodProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
@Slf4j
@EnableConfigurationProperties(UplaodProperties.class)
public class UploadService {

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private UplaodProperties prop;
    //1.定义文件类型
    //private static final List<String> types= Arrays.asList("image/jpeg","image/png","image/jpg");
    public String upload(MultipartFile file) throws IOException {
        //2.校验文件类型
        String type = file.getContentType();
        System.out.println("======================================================="+type);
        if(!prop.getAllowTypes().contains(type)){
            log.info("上传文件失败！，文件类型不匹配：{}",type);
            throw  new LyException(ExceptionEnums.FILE_TYPE_NOT_PITCH);
        }
        //3.校验文件内容
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            System.out.println("===============================================image========"+image);
            if(image==null){
                log.info("上传文件失败！，上传文件内容不匹配");
                throw new LyException(ExceptionEnums.FILE_CONTENT_NOT_PITCH);
            }
        } catch (IOException e) {
           log.error("上传文件失败！",e);
        }

       /* File dest=new File("F:\\picture\\upload");
        if(!dest.exists()){
            dest.mkdirs();
        }
        //5.上传文件
        try {
            file.transferTo(new File(dest,file.getOriginalFilename()));
            //6.拼接url地址返回
            String url="http://image.leyou.com/upload/"+file.getOriginalFilename();
            return url;
        } catch (IOException e) {
           log.error("上传失败",e);
           throw new LyException(ExceptionEnums.UPLOAD_FILE_ERROR);
        }*/

        //4.将图片上传到fast
        //4.1 获取文件的扩展名
        String extend = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
        //5.执行上传操作
        StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), extend, null);
        System.out.println(storePath.getFullPath());
        //6.返回完整路径
        return  prop.getBaseUrl()+storePath.getFullPath();
    }
}
