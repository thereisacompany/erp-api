package com.jsh.erp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import jxl.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;
import jxl.format.*;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelUtils {

	public static WritableFont arial14font = null;

	public static File exportHAConfirm() {
		File excelFile = null;

		try {
			FileInputStream templateFile = new FileInputStream("./excelFile/家電-空白確認書.xlsx");
			XSSFWorkbook workbook = new XSSFWorkbook(templateFile);
			XSSFSheet sheet = workbook.getSheetAt(0);

			Row row1 = sheet.getRow(1);
			row1.getCell(1).setCellValue("陳先生");
			row1.getCell(3).setCellValue("0988123456");
			row1.getCell(5).setCellValue("2023-09-10");
			row1.getCell(7).setCellValue(1234567);

			Row row2 = sheet.getRow(2);
			row2.getCell(1).setCellValue("台中市南屯區");
			row2.getCell(7).setCellValue("\u2713 扣庫存  \u25A1_____到貨");

			Row row4 = sheet.getRow(4);
			row4.getCell(0).setCellValue("一台大冰箱");
			row4.getCell(4).setCellValue("1.0");
			row4.getCell(6).setCellValue("\u2713 是 \u25A1 否");
			row4.getCell(7).setCellValue("LG");
			row4.getCell(8).setCellValue("QRCode");

//			Row row5 = sheet.getRow(5);
//			row5.getCell(0).setCellValue("一台大冰箱");
//			row5.getCell(4).setCellValue("1.0");

//			Row row6 = sheet.getRow(6);
//			row6.getCell(0).setCellValue("一台大冰箱");
//			row6.getCell(4).setCellValue("1.0");

			Row row7 = sheet.getRow(7);
			row7.getCell(1).setCellValue("memo");

//			for (int i =1 ;i < 8;i++) {
//				Row row = sheet.getRow(i);
//				for(org.apache.poi.ss.usermodel.Cell cell : row) {
//					System.out.println(cell.getRowIndex()+"--"+cell.getColumnIndex()+">>"+cell.toString());
//				}
//			}

			FileOutputStream outputStream = new FileOutputStream("filled_excel1.xlsx");
			workbook.write(outputStream);

			excelFile = new File("filled_excel.xlsx");

			outputStream.close();

			templateFile.close();

//			workbook.write();
//			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
//			throw new RuntimeException(e);
		}
		return excelFile;
	}

	public static File exportObjects(String fileName, String[] names,
			String title, List<String[]> objects) throws Exception {
		File excelFile = new File("fileName.xls");
		WritableWorkbook wtwb = Workbook.createWorkbook(excelFile);
		WritableSheet sheet = wtwb.createSheet(title, 0);
		sheet.getSettings().setDefaultColumnWidth(20);
		WritableFont wfont = new WritableFont(WritableFont.createFont("楷书"), 15);
		WritableCellFormat format = new WritableCellFormat(wfont);
		WritableFont wfc = new WritableFont(WritableFont.ARIAL, 20,
				WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
				jxl.format.Colour.BLACK);
		WritableCellFormat wcfFC = new WritableCellFormat(wfc);
		wcfFC.setAlignment(Alignment.CENTRE);
		wcfFC.setVerticalAlignment(VerticalAlignment.CENTRE);
		// CellView cellView = new CellView();
		// cellView.setAutosize(true); //设置自动大小
		format.setAlignment(Alignment.LEFT);
		format.setVerticalAlignment(VerticalAlignment.TOP);
		sheet.mergeCells(0, 0, names.length - 1, 0);
		sheet.addCell(new Label(0, 0, title, wcfFC));
		int rowNum = 2;
		for (int i = 0; i < names.length; i++) {
			sheet.addCell(new Label(i, 1, names[i], format));
		}
		for (int j = 0; j < objects.size(); j++) {
			String[] obj = objects.get(j);
			for (int h = 0; h < obj.length; h++) {
				sheet.addCell(new Label(h, rowNum, obj[h], format));
			}
			rowNum = rowNum + 1;

		}
		wtwb.write();
		wtwb.close();
		return excelFile;
	}

	/**
	 * 导出excel，不需要第一行的title
	 *
	 * @param fileName
	 * @param names
	 * @param title
	 * @param objects
	 * @return
	 * @throws Exception
	 */
	public static File exportObjectsWithoutTitle(String fileName,
			String[] names, String title, List<String[]> objects)
			throws Exception {
		File excelFile = new File(fileName);
		WritableWorkbook wtwb = Workbook.createWorkbook(excelFile);
		WritableSheet sheet = wtwb.createSheet(title, 0);
		sheet.getSettings().setDefaultColumnWidth(20);

		// 第一行的格式
		WritableFont wfc = new WritableFont(WritableFont.ARIAL, 15,
				WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
				jxl.format.Colour.BLACK);
		WritableCellFormat wcfFC = new WritableCellFormat(wfc);
		wcfFC.setVerticalAlignment(VerticalAlignment.CENTRE);

		// 设置字体以及单元格格式
		WritableFont wfont = new WritableFont(WritableFont.createFont("楷书"), 15);
		WritableCellFormat format = new WritableCellFormat(wfont);
		format.setAlignment(Alignment.LEFT);
		format.setVerticalAlignment(VerticalAlignment.TOP);

		// 第一行写入标题
		for (int i = 0; i < names.length; i++) {
			sheet.addCell(new Label(i, 0, names[i], wcfFC));
		}

		// 其余行依次写入数据
		int rowNum = 1;
		for (int j = 0; j < objects.size(); j++) {
			String[] obj = objects.get(j);
			for (int h = 0; h < obj.length; h++) {
				sheet.addCell(new Label(h, rowNum, obj[h], format));
			}
			rowNum = rowNum + 1;
		}
		wtwb.write();
		wtwb.close();
		return excelFile;
	}

	public static String createTempFile(String[] names, String title, List<String[]> objects) throws Exception {
		File excelFile = File.createTempFile(System.currentTimeMillis() + "", ".xls");
		WritableWorkbook wtwb = Workbook.createWorkbook(excelFile);
		WritableSheet sheet = wtwb.createSheet(title, 0);
		sheet.getSettings().setDefaultColumnWidth(20);
		WritableFont wfont = new WritableFont(WritableFont.createFont("楷书"), 15);
		WritableCellFormat format = new WritableCellFormat(wfont);
		WritableFont wfc = new WritableFont(WritableFont.ARIAL, 20,
				WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
				jxl.format.Colour.BLACK);
		WritableCellFormat wcfFC = new WritableCellFormat(wfc);
		wcfFC.setAlignment(Alignment.CENTRE);
		wcfFC.setVerticalAlignment(VerticalAlignment.CENTRE);
		// CellView cellView = new CellView();
		// cellView.setAutosize(true); //设置自动大小
		format.setAlignment(Alignment.LEFT);
		format.setVerticalAlignment(VerticalAlignment.TOP);
		sheet.mergeCells(0, 0, names.length - 1, 0);
		sheet.addCell(new Label(0, 0, title, wcfFC));
		int rowNum = 2;
		for (int i = 0; i < names.length; i++) {
			sheet.addCell(new Label(i, 1, names[i], format));
		}
		for (int j = 0; j < objects.size(); j++) {
			String[] obj = objects.get(j);
			for (int h = 0; h < obj.length; h++) {
				sheet.addCell(new Label(h, rowNum, obj[h], format));
			}
			rowNum = rowNum + 1;
		}
		wtwb.write();
		wtwb.close();
		return excelFile.getName();
	}

	public static String createCheckRandomTempFile(String[] names, String title, List<String[]> objects,Map<String,String> infoMap) throws Exception {
		File excelFile = File.createTempFile(System.currentTimeMillis() + "", ".xls");
		WritableWorkbook wtwb = Workbook.createWorkbook(excelFile);
		WritableSheet sheet = wtwb.createSheet(title, 0);
		sheet.getSettings().setDefaultColumnWidth(20);
		WritableFont wfont = new WritableFont(WritableFont.createFont("楷书"), 14);

		WritableCellFormat format = new WritableCellFormat(wfont);
		format.setBorder(Border.ALL, BorderLineStyle.THIN);
		format.setAlignment(Alignment.CENTRE);
		format.setVerticalAlignment(VerticalAlignment.CENTRE);

		WritableFont wfc = new WritableFont(WritableFont.ARIAL, 20,
				WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
				jxl.format.Colour.BLACK);
		WritableCellFormat wcfFC = new WritableCellFormat(wfc);
		wcfFC.setAlignment(Alignment.LEFT);
		wcfFC.setVerticalAlignment(VerticalAlignment.CENTRE);

		WritableFont nameWfc = new WritableFont(WritableFont.ARIAL, 14,
				WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
				jxl.format.Colour.BLACK);
		WritableCellFormat nameFormat = new WritableCellFormat(nameWfc);
		nameFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
		nameFormat.setAlignment(Alignment.CENTRE);
		nameFormat.setVerticalAlignment(VerticalAlignment.CENTRE);

		WritableCellFormat infoFormat = new WritableCellFormat(wfont);
		infoFormat.setAlignment(Alignment.LEFT);
		infoFormat.setVerticalAlignment(VerticalAlignment.CENTRE);


		sheet.mergeCells(0, 0, names.length - 1, 0);
		sheet.addCell(new Label(0, 0, infoMap.get("title"), wcfFC));

		sheet.addCell(new Label(0, 2, infoMap.get("info"), infoFormat));
		sheet.addCell(new Label(2, 2, infoMap.get("dvrnvr"), infoFormat));
		sheet.addCell(new Label(4, 2, infoMap.get("char"), infoFormat));
		sheet.addCell(new Label(0, 3, infoMap.get("infoPercent"), infoFormat));
		sheet.addCell(new Label(2, 3, infoMap.get("dvrnvrPercent"), infoFormat));
		sheet.addCell(new Label(4, 3, infoMap.get("charPercent"), infoFormat));

		int rowNum = 5;
		for (int i = 0; i < names.length; i++) {
			sheet.addCell(new Label(i, 4, names[i], nameFormat));
		}
		for (int j = 0; j < objects.size(); j++) {
			String[] obj = objects.get(j);
			for (int h = 0; h < obj.length; h++) {
				sheet.addCell(new Label(h, rowNum, obj[h], format));
			}
			rowNum = rowNum + 1;
		}
		wtwb.write();
		wtwb.close();
		return excelFile.getName();
	}



	public static String getContent(Sheet src, int rowNum, int colNum) {
		if(colNum < src.getRow(rowNum).length) {
			return src.getRow(rowNum)[colNum].getContents().trim();
		} else {
			return null;
		}
	}

	public static String getDateContent(Sheet src, int rowNum, int colNum) {
		// 日期 类型的处理
		Cell c = src.getRow(rowNum)[colNum];
		if(CellType.DATE.equals(c.getType())) {
			DateCell dc = (DateCell) c;
			Date jxlDate = dc.getDate();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			return sdf.format(jxlDate);
		} else {
			String dateStr =  c.getContents().trim();
			return dateStr;
		}
	}

	/**
	 * 从第i行开始到最后检测指定列的唯一性
	 *
	 * @param src
	 * @param colNum
	 * @param fromRow
	 *            起始行
	 * @return
	 */
	public static Boolean checkUnique(Sheet src, int colNum, int fromRow) {
		Cell[] colCells = src.getColumn(colNum);
		Set<String> set = new HashSet<String>();
		for (int i = fromRow; i < colCells.length; i++) {
			if (!StringUtils.isEmpty(colCells[i].getContents())
					&& !set.add(colCells[i].getContents())) {
				return false;
			}
		}
		return true;
	}

	public static File getTempFile(String fileName) {
		String dir = System.getProperty("java.io.tmpdir"); // 获取系统临时目录
		return new File(dir + File.separator + fileName);
	}

	public static void main(String[] args) throws Exception {
		String msg = "12345";
		System.out.println(msg.indexOf("@"));


		exportHAConfirm();
	}
}
