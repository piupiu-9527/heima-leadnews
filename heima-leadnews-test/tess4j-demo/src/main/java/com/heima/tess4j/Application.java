package com.heima.tess4j;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

/**
 * @description: TODO 测试图片文字识别
 * @ClassName: Application
 * @author: Zle
 * @date: 2022-06-28 21:04
 * @version 1.0
*/
public class Application {

    public static void main(String[] args) {
        try {
            //获取本地图片
            File file = new File("E:\\asdc.png");
            //创建tessearact对象
            Tesseract tesseract = new Tesseract();
            //设置字体库路径
            tesseract.setDatapath("E:\\develop\\tess4j");
            //中文识别
            tesseract.setLanguage("chi_sim");
            //执行OCR识别
            String result = tesseract.doOCR(file);
            //替换回车键和tal键，使结果为一行
            result = result.replace("\\r|\\n", "-").replaceAll(" ", "");
            System.out.println("识别的结果为"+result);
        } catch (TesseractException e) {
            e.printStackTrace();
        }


    }
}
