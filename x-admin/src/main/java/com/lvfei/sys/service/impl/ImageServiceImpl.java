package com.lvfei.sys.service.impl;

import com.lvfei.sys.entity.Image;
import com.lvfei.sys.mapper.ImageMapper;
import com.lvfei.sys.service.IImageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Lvfei
 * @since 2023-03-10
 */
@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements IImageService {

}
