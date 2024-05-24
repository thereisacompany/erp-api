package com.jsh.erp.utils;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class CustomMultipartFile implements MultipartFile {

    private final FileItem fileItem;

    public CustomMultipartFile(File file) throws IOException {
        this.fileItem = new DiskFileItem("file",
                "multipart/form-data", true, file.getName(), (int) file.length(), file.getParentFile());

        try (InputStream input = new FileInputStream(file);
             OutputStream os = fileItem.getOutputStream()) {
            IOUtils.copy(input, os);
        }
    }

    @Override
    public String getName() {
        return fileItem.getFieldName();
    }

    @Override
    public String getOriginalFilename() {
        return fileItem.getName();
    }

    @Override
    public String getContentType() {
        return fileItem.getContentType();
    }

    @Override
    public boolean isEmpty() {
        return fileItem.getSize() == 0;
    }

    @Override
    public long getSize() {
        return fileItem.getSize();
    }

    @Override
    public byte[] getBytes() throws IOException {
        return fileItem.get();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return fileItem.getInputStream();
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        try (InputStream input = fileItem.getInputStream();
             OutputStream os = new FileOutputStream(dest)) {
            IOUtils.copy(input, os);
        }
    }
}
