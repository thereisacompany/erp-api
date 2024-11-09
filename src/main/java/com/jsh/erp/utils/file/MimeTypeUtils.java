package com.jsh.erp.utils.file;

import jakarta.activation.MimetypesFileTypeMap;
import org.springframework.util.StringUtils;

import java.io.File;

/**
 * 媒體類型工具類
 * 
 * @author ruoyi
 */
public class MimeTypeUtils
{
    public static final String IMAGE_PNG = "image/png";

    public static final String IMAGE_JPG = "image/jpg";

    public static final String IMAGE_JPEG = "image/jpeg";

    public static final String IMAGE_BMP = "image/bmp";

    public static final String IMAGE_GIF = "image/gif";
    
    public static final String[] IMAGE_EXTENSION = { "bmp", "gif", "jpg", "jpeg", "png" };

    public static final String[] FLASH_EXTENSION = { "swf", "flv" };

    public static final String[] MEDIA_EXTENSION = { "swf", "flv", "mp3", "wav", "wma", "wmv", "mid", "avi", "mpg",
            "asf", "rm", "rmvb" };

    public static final String[] VIDEO_EXTENSION = { "mp4", "avi", "rmvb" };

    public static final String[] DEFAULT_ALLOWED_EXTENSION = {
            // 圖片
            "bmp", "gif", "jpg", "jpeg", "png",
            // word excel powerpoint
            "doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "htm", "txt",
            // 壓縮文件
            "rar", "zip", "gz", "bz2",
            // 視頻格式
            "mp4", "avi", "rmvb",
            // pdf
            "pdf" };

    public static String getResponseContentType(String type) {
        String contentType = null;
        switch (type) {
            case "xlsx":
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8";
                break;
            case "xls":
                contentType = "application/vnd.ms-excel;charset=utf-8";
                break;
            case "docx":
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document;charset=utf-8";
                break;
            case "doc":
                contentType = "application/msword;charset=utf-8";
                break;
            case "pptx":
                contentType = "application/vnd.openxmlformats-officedocument.presentationml.presentation;charset=utf-8";
                break;
            case "ppt":
                contentType = "application/vnd.ms-powerpoint;charset=utf-8";
                break;
            case "pdf":
                contentType = "application/pdf;charset=utf-8";
                break;
            case "csv":
                contentType = "text/csv;charset=utf-8";
                break;
        }

        return contentType;
    }

    public static String getContentType(String url) {
        if(StringUtils.isEmpty(url)) {
            return null;
        }
        try {
            return new MimetypesFileTypeMap().getContentType(new File(url));
        } catch (Exception e) {
            return null;
        }
    }

    public static String getExtension(String prefix)
    {
        switch (prefix)
        {
            case IMAGE_PNG:
                return "png";
            case IMAGE_JPG:
                return "jpg";
            case IMAGE_JPEG:
                return "jpeg";
            case IMAGE_BMP:
                return "bmp";
            case IMAGE_GIF:
                return "gif";
            default:
                return "";
        }
    }
}
