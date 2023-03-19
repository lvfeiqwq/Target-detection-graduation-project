package com.lvfei.sys.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lvfei.common.vo.Result;
import com.lvfei.sys.entity.Incident;
import com.lvfei.sys.entity.User;
import com.lvfei.sys.service.IIncidentService;
import com.lvfei.sys.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.Base64;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Lvfei
 * @since 2023-02-27
 */
@RestController
@RequestMapping("/incident")
@CrossOrigin
public class IncidentController {
    @Autowired
    IIncidentService incidentService;
    @Autowired
    IUserService userService;
    private static final String filePath="D:/Desktop/myproject/x-admin/images/incident/";
    @GetMapping("/list")
    public Result<Map<String,Object>> getIncidentList(@RequestParam(value = "category",required = false) String category,
                                                      @RequestParam(value = "beginDate",required = false) String beginDate,
                                                      @RequestParam(value = "endDate",required = false) String endDate,
                                                      @RequestParam(value = "place",required = false) String place,
                                                      @RequestParam(value = "pageNo") Long pageNo,
                                                      @RequestParam(value = "pageSize") Long pageSize) throws ParseException {
//        System.out.println(category+place);
//        System.out.println(beginDate);
        LambdaQueryWrapper<Incident> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasLength(category),Incident::getCategory,category);
        wrapper.like(StringUtils.hasLength(place),Incident::getIncidentPlace,place);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        if(beginDate.length()>0 && endDate.length()>0){
            wrapper.gt(StringUtils.hasLength(beginDate),Incident::getIncidentDate,df.parse(beginDate));
            wrapper.lt(StringUtils.hasLength(endDate),Incident::getIncidentDate,df.parse(endDate));
        }
//        Date begin = df.parse(beginDate);
//        Date end = df.parse(endDate);
////        System.out.println(begin);
//        if (begin!=null) {
//            wrapper.gt(Incident::getIncidentDate, begin);
//        }
//        if (end!=null) {
//            wrapper.lt(Incident::getIncidentDate,end);
//        }
        wrapper.orderByDesc(Incident::getId);

        Page<Incident> page = new Page<>(pageNo,pageSize);
        incidentService.page(page,wrapper);

        Map<String,Object> data = new HashMap<>();
        data.put("total",page.getTotal());
        data.put("rows",page.getRecords());
//        System.out.println(data);
        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<?> getIncidentById(@PathVariable("id") Integer id) throws IOException {
        Incident incident = incidentService.getById(id);
        String path = incident.getImgUrl();
        String base64Image;
        File file = new File(filePath+path);
        if(file.exists()){
            FileInputStream imageInFile = new FileInputStream(file);
            // 读取文件内容并将其存储为字节数组
            byte[] imageData = new byte[(int) file.length()];
            imageInFile.read(imageData);

            // 对字节数组进行Base64编码
            base64Image = Base64.getEncoder().encodeToString(imageData);
            String[] parts = path.split("\\.");
            String base64 = "data:image/"+ parts[1]+";base64,"+ base64Image;
            imageInFile.close();
            JSONObject json = new JSONObject();
            json.put("incident", incident);
            json.put("base64",base64);
            return Result.success(json);
        } else {
            JSONObject json = new JSONObject();
            json.put("incident", incident);
            json.put("base64","");
            return Result.success(json);
        }
    }

    @PostMapping("/addIncident")
//    @DateTimeFormat(pattern=“yyyy-MM-dd HH:mm:ss”)
    public Result<?> addIncident(Incident incident, @RequestParam(value ="file",required = false) MultipartFile file) throws IOException {
        System.out.println(incident);
//        System.out.println(incident+"--------------"+file);
//        incidentService.save(incident);
//        System.out.println(incident.getId());
        if(file == null ){
            incidentService.save(incident);
            System.out.println("----------------------------------------------");
        } else{
            //文件文字--->每一个事件的id
            incidentService.save(incident);
            String OriginalFilename = file.getOriginalFilename();
            String newFilename = String.valueOf(incident.getId());
            //文件格式
            String fileFormat = Objects.requireNonNull(OriginalFilename). substring(OriginalFilename.lastIndexOf("."));
            //存路径信息
            incident.setImgUrl(newFilename + fileFormat);
            incidentService.updateById(incident);
            //存文件到本地
            String newFilePath = filePath + newFilename + fileFormat;
            try {
                file.transferTo(new File(newFilePath));  //将传来的文件写入新建的文件
                System.out.println("文件存成功");
            }catch (IllegalStateException | IOException e ) {
                //处理异常
                System.out.println("文件存失败！！！！！！！！！！！！");
            }
        }
        return Result.success("新增事件成功");
    }
//    @PostMapping("/addIncident")
//    public Result<?> addIncident(@RequestBody Map<String,String>map){
//        System.out.println(map);
////        System.out.println(incident+"--------------"+file);
////        if(file == null){
////            System.out.println("----------------------------------------------");
////        }
////        incidentService.save(incident);
//        return Result.success("新增事件成功");
//    }
    @DeleteMapping("/{id}")
    public Result<Incident> deleteIncidentById(@PathVariable("id") Integer id){
        incidentService.removeById(id);
        return Result.success("删除事件成功");
    }

    @PostMapping("/updateIncident")
    public Result<?> updateIncident(Incident incident,@RequestParam(value ="file",required = false) MultipartFile file){
        System.out.println(incident);
        if(file == null ){
            incidentService.updateById(incident);
            System.out.println("----------------------------------------------");
        } else{
            //包括已存在和新增的图片均需要重新判断文件类型，设置文件名
//            incidentService.updateById(incident);
            String OriginalFilename = file.getOriginalFilename();
            String Filename = String.valueOf(incident.getId());
            //文件格式
            String fileFormat = Objects.requireNonNull(OriginalFilename). substring(OriginalFilename.lastIndexOf("."));
            //存路径信息
            incident.setImgUrl(Filename + fileFormat);
            incidentService.updateById(incident);
            //存文件到本地
            String newFilePath = filePath + Filename + fileFormat;

            //删除原有文件，存新文件 filepath是不变的
            File newFile = new File(newFilePath);
            if(newFile.exists()){
                Boolean result=newFile.delete();
                System.out.println(result + "删除成功");
            }
            try {
                file.transferTo(new File(newFilePath));  //将传来的文件写入新建的文件
                System.out.println("文件更新成功");
            }catch (IllegalStateException | IOException e ) {
                //处理异常
                System.out.println("文件更新失败！！！！！！！！！！！！");
            }
        }
        return Result.success("修改事件成功");
    }
}
