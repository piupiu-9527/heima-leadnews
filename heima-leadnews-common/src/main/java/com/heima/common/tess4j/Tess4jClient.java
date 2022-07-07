package com.heima.common.tess4j;

import lombok.Getter;
import lombok.Setter;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

/**
 * @description: TODO 工具类 封装tess4j
 * @ClassName: Tess4jClient
 * @author: Zle
 * @date: 2022-06-28 21:13
 * @version 1.0
*/
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "tess4j")
public class Tess4jClient {

    //tess4j.dataPath
    private String dataPath;
    private String language;

    public String doOCR(BufferedImage image) throws TesseractException {
        
        //创建Tesseract对象
        ITesseract tesseract = new Tesseract();
        //设置字体库路径
        tesseract.setDatapath(dataPath);
        //中文识别
        tesseract.setLanguage(language);
        //执行OCR识别
        String result = tesseract.doOCR(image);
        //替换回车和tal键  使结果为一行
        result = result.replace("\\r|\\n", "-").replaceAll(" ", "");
        return result;

    }
}
