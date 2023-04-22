package com.lvfei.sys.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lvfei.common.dto.Label;
import com.lvfei.common.vo.Result;
import com.lvfei.sys.entity.Image;
import com.lvfei.sys.service.IImageService;
import com.lvfei.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 前端控制器
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

    private static final String filePath = "D:/Desktop/myproject/x-admin/images/detect/";

    private static final String newFilePath = "D:/Desktop/yolov5-5.0/VOC2028/images/train/";

    private static final String newLabelPath = "D:/Desktop/yolov5-5.0/VOC2028/labels/train/";

    @PostMapping("getImage")
    Result<?> beforeDetect(@RequestParam(value = "file") MultipartFile file) throws IOException {
        if (file == null) {
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
        image.setEdited(0);
        iImageService.updateById(image);
        String path = filePath + newFilename + fileFormat;
        try {
            file.transferTo(new File(path));  //将传来的文件写入新建的文件
        } catch (IllegalStateException | IOException e) {
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
        String base64 = "data:image/" + parts[1] + ";base64," + base64Image;
        imageInFile.close();
        JSONObject json = new JSONObject();
        json.put("base64", base64);
        json.put("image", image);
        return Result.success(json, "成功");
    }

    @GetMapping("/getHistory")
    Result<?> getDetectHistory(@RequestParam(value = "beginDate", required = false) String beginDate,
                               @RequestParam(value = "endDate", required = false) String endDate,
                               @RequestParam(value = "name", required = false) String name,
                               @RequestParam(value = "sortType") Integer sortType,
                               @RequestParam(value = "editedType",required = false) Integer editedType,
                               @RequestParam(value = "pageNo") Long pageNo,
                               @RequestParam(value = "pageSize") Long pageSize) throws ParseException, IOException {

        LambdaQueryWrapper<Image> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasLength(name), Image::getName, name);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//设置日期格式
        if (!Objects.equals(beginDate, "") && !Objects.equals(endDate, "")) {
            wrapper.gt(StringUtils.hasLength(beginDate), Image::getTime, df.parse(beginDate));
            wrapper.lt(StringUtils.hasLength(endDate), Image::getTime, df.parse(endDate));
        }
        if (editedType != null) {
            if (editedType.equals(0)) {
                wrapper.eq(Image::getEdited, editedType);
            } else if (editedType.equals(1)) {
                wrapper.eq(Image::getEdited, editedType);
            }
        }
        if (sortType.equals(1)) {
            wrapper.orderByDesc(Image::getTime);
        } else {
            wrapper.orderByAsc(Image::getTime);
        }
        wrapper.ne(Image::getResult, "");
        Page<Image> page = new Page<>(pageNo, pageSize);
        iImageService.page(page, wrapper);
        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("rows", page.getRecords());
        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<?> getImageById(@PathVariable("id") Integer id) {
        Image image = iImageService.getById(id);
        return Result.success(image);
    }
    @DeleteMapping("/{id}")
    public Result<?>deleteById(@PathVariable("id") Integer id) {
        iImageService.removeById(id);
        return Result.success("成功删除检测记录");
    }
    @PostMapping("/edit")
    public Result<?> editLabel(@RequestBody List<Label> labels, @RequestParam(value = "url") String url) throws IOException {
//        System.out.println(labels);
        /*
            制作新数据和标签信息
        */
        // url带着图片的格式后缀
//        System.out.println(url);
        String imagePath = filePath + url;// 图片的路径
        BufferedImage image = ImageIO.read(new File(imagePath));
        int width = image.getWidth(); // 获取图片宽度
        int height = image.getHeight(); // 获取图片高度
        // 将图片存储到其他地方
        String newImagePath = newFilePath + "Z_edited" + url; // 新的图片路径
        ImageIO.write(image, "jpg", new File(newImagePath));
        // 创建txt文件并写入预测框信息
        String[] parts = url.split("\\.");
        String txtPath = newLabelPath + "Z_edited" + parts[0] + ".txt"; // txt文件路径
        File txtFile = new File(txtPath);
        if (!txtFile.exists()) {
            txtFile.createNewFile();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(txtFile));
        for (Label label : labels) {
            //种类信息
            int category = label.getCategory();
            // 剔除无效信息, 非安全帽、非工装
            if (category==3){
                continue;
            }else if(category == 4){
                continue;
            }else if(category == 5){
                continue;
            }
            // 坐标信息
            double x1 = (double) label.getCoordinate().get(0) / width;
            double y1 = (double) label.getCoordinate().get(1) / height;
            double x2 = (double) label.getCoordinate().get(2) / width;
            double y2 = (double) label.getCoordinate().get(3) / height;
            writer.write(category + " " + x1 + " " + y1 + " " + x2 + " " + y2 + "\n"); // 写入一行信息
        }
        writer.close();

        /*
            更新页面数据
         */
        Integer id = Integer.parseInt(parts[0]);
        Image image1 = iImageService.getById(id);
        //修改编辑状态
        image1.setEdited(1);
        String jsonString = image1.getResult();
//        System.out.println(jsonString);
        List<JSONObject> jsonArray = JSONArray.parseArray(jsonString, JSONObject.class);
        int length = labels.size();
        for (int i = 0; i < length; i++) {
            int category = labels.get(i).getCategory();
            int x1 = labels.get(i).getCoordinate().get(0);
            int y1 = labels.get(i).getCoordinate().get(1);
            int x2 = labels.get(i).getCoordinate().get(2);
            int y2 = labels.get(i).getCoordinate().get(3);
            JSONObject obj = jsonArray.get(i);
            com.alibaba.fastjson2.JSONArray coordinate = obj.getJSONArray("coordinate");
            coordinate.set(0, x1);
            coordinate.set(1, y1);
            coordinate.set(2, x2);
            coordinate.set(3, y2);
            obj.put("category", category);
        }
        String newJsonString = JSONArray.toJSONString(jsonArray);
        System.out.println(newJsonString);
        image1.setResult(newJsonString);
        // 将前端需要解析的data前缀给舍去
        String base64Image = image1.getBase64();
        String[] parts1 = base64Image.split(",");
        String imageString = parts1[1];
        iImageService.updateById(image1);
        System.out.println("--------------------------------------------------------------");
        return Result.success("成功编辑，等待重新训练");
    }
}
