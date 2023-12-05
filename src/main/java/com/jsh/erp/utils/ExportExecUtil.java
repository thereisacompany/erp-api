package com.jsh.erp.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ExportExecUtil {

	public static void showExec(File excelFile, String fileName, HttpServletResponse response) throws Exception{
		response.setContentType("application/octet-stream");
//	       fileName = new String(fileName.getBytes("gbk"),"ISO8859_1");
		fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
		response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\""); //".xls" +
		FileInputStream fis = new FileInputStream(excelFile);
		OutputStream out = response.getOutputStream();
		int SIZE = 1024 * 1024;
		byte[] bytes = new byte[SIZE];
		int LENGTH = -1;
		while((LENGTH = fis.read(bytes)) != -1){
			out.write(bytes,0,LENGTH);
		}

		out.flush();
		fis.close();

	}

	public static void showExecs(List<File> list, HttpServletResponse response) throws IOException {
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment;filename=files.zip");

		//創建ZipOutputStream來將多個文件打包為一個ZIP文件
		try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
			for (File file : list) {
				addFileToZip(zipOut, file);
			}

			zipOut.finish();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void addFileToZip(ZipOutputStream zipOut, File file) throws IOException {
		byte[] buffer = new byte[1024];
		try (FileInputStream fis = new FileInputStream(file)) {
			ZipEntry zipEntry = new ZipEntry(file.getName());
			zipOut.putNextEntry(zipEntry);
			int length;
			while ((length = fis.read(buffer)) > 0) {
				zipOut.write(buffer, 0, length);
			}
			zipOut.closeEntry();
		}
	}
}
