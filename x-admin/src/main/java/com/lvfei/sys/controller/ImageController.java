package com.lvfei.sys.controller;

import com.alibaba.fastjson2.JSONObject;
import com.lvfei.common.vo.Result;
import com.lvfei.sys.entity.Image;
import com.lvfei.sys.service.IImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Objects;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Lvfei
 * @since 2023-03-10
 */
@RestController
@RequestMapping("/image")
public class ImageController {
    @Autowired
    IImageService iImageService;

    private static final String filePath="D:/Desktop/myproject/x-admin/images/detect/";
    @PostMapping("getImage")
    Result<?> beforeDetect(@RequestParam(value = "file") MultipartFile file) throws IOException {
        if(file == null){
            return Result.fail("请重新上传文件");
        }
        String OriginalFilename = file.getOriginalFilename();
        //文件格式
        String fileFormat = Objects.requireNonNull(OriginalFilename).substring(OriginalFilename.lastIndexOf("."));
        Image image = new Image();
        image.setName(OriginalFilename);
        iImageService.save(image);
        String newFilename = String.valueOf(image.getId());
        image.setUrl(newFilename + fileFormat);
        image.setTime(LocalDateTime.now());
        iImageService.updateById(image);
        String path = filePath + newFilename + fileFormat;
        try {
            file.transferTo(new File(path));  //将传来的文件写入新建的文件
        }catch (IllegalStateException | IOException e ) {
            //处理异常
            System.out.println("文件存失败！！！！！！！！！！！！");
            return Result.fail();
        }
        //返回一个base64编码的文件
        File base64File = new File(path);
        FileInputStream imageInFile = new FileInputStream(base64File);
        // 读取文件内容并将其存储为字节数组
        byte[] imageData = new byte[(int) base64File.length()];
        imageInFile.read(imageData);
        // 对字节数组进行Base64编码
        String base64Image = Base64.getEncoder().encodeToString(imageData);
        String[] parts = path.split("\\.");
        String base64 = "data:image/"+ parts[1]+";base64,"+ base64Image;
        imageInFile.close();
        JSONObject json = new JSONObject();
        json.put("base64",base64);
        json.put("image",image);
        return Result.success(json,"成功");
    }
}
