package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.file.service.FileStorageService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.utils.thread.WmThreadLocalUtils;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * @description:
 * @ClassName: WmMaterialServiceImpl
 * @author: Zle
 * @date: 2022-06-25 20:13
 * @version 1.0
*/
@Slf4j
@Service
@Transactional
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * @description: 图片上传
     * @author Zle
     * @date 2022-06-25  20:33
     * @version 1.0
    */
    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        //检查参数，为空则返回错误信息
        if (multipartFile == null || multipartFile.getSize() == 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //2. 上传图片到minIO中
        //2.1 uuid生成文件名，将-替换成掉
        String fileName = UUID.randomUUID().toString().replace("-", "");
        //原始文件名 getOriginalFilename
        String originalFilename = multipartFile.getOriginalFilename();
        //文件后缀 lastIndexOf
        String postfix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //2.4 上传

        String fileId=null;
        try {
            fileId = fileStorageService.uploadImgFile("", fileName + postfix, multipartFile.getInputStream());
            log.info("上传图片到MinIO中，fileId:{}",fileId);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("WmMaterialServiceImpl-上传文件失败");
        }

        //3.保存图片在minIO中的地址到数据库中
        WmMaterial wmMaterial = new WmMaterial();

        wmMaterial.setUserId(WmThreadLocalUtils.getUser().getId());
        wmMaterial.setUrl(fileId);
        wmMaterial.setIsCollection((short)0);
        wmMaterial.setType((short)0);
        wmMaterial.setCreatedTime(new Date());
        save(wmMaterial);
        //4.返回结果

        return ResponseResult.okResult(wmMaterial);
    }

    /**
     * 素材列表查询
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findList(WmMaterialDto dto) {
        //1.检查参数
        dto.checkParam();
        //2.分页查询
        IPage page =new Page(dto.getPage(),dto.getSize());
        //2.1构建查询条件
        LambdaQueryWrapper<WmMaterial> lambdaQueryWrapper = new LambdaQueryWrapper<WmMaterial>();
        //2.2判断是否只查询收藏的
        //2.3只查询当前登录用户的素材
        //2.4按照时间倒序
        lambdaQueryWrapper.eq(dto.getIsCollection() != null && dto.getIsCollection()==1
                ,WmMaterial::getIsCollection,dto.getIsCollection());
        lambdaQueryWrapper.eq(WmMaterial::getUserId,WmThreadLocalUtils.getUser().getId())
                .orderByDesc(WmMaterial::getCreatedTime);
        //3.结果返回
        page(page,lambdaQueryWrapper);

        PageResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());

        responseResult.setData(page.getRecords());


        /*
        //2.分页查询
        IPage page = new Page(dto.getPage(), dto.getSize());
        //2.1构建查询条件
        LambdaQueryWrapper<WmMaterial> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //2.2判断是否只查询收藏的
        if (dto.getIsCollection() != null && dto.getIsCollection() == 1) {
            lambdaQueryWrapper.eq(WmMaterial::getIsCollection, dto.getIsCollection());
        }

        //lambdaQueryWrapper.eq(
        //        dto.getIsCollection() != null && dto.getIsCollection() == 1,
        //        WmMaterial::getIsCollection,
        //        dto.getIsCollection());

        //2.3只查询当前登录用户的素材
        lambdaQueryWrapper.eq(WmMaterial::getUserId, WmThreadLocalUtils.getUser().getId());

        //2.4按照时间倒序
        lambdaQueryWrapper.orderByDesc(WmMaterial::getCreatedTime);

        page(page, lambdaQueryWrapper);
        //3.结果返回
        ResponseResult responseResult = new PageResponseResult(dto.getPage(),
                dto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());*/
        return responseResult;

    }
}
