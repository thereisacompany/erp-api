package com.jsh.erp.utils;

import com.jsh.erp.datasource.vo.DepotHeadVo4List;
import jxl.*;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.format.*;
import jxl.write.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.lang.Boolean;

public class ExcelUtils {

	public static WritableFont arial14font = null;

	/**
	 *
	 * @param type 1 家電 2 冷氣
	 * @param item
	 * @param isRecycle 舊機是否回收
	 * @param brandName 舊機品牌
	 * @return
	 */
	public static File exportHAConfirm(int type, DepotHeadVo4List item, boolean isRecycle, String brandName) {
		File excelFile = null;

		try {
			String filePath = "./excelFile/家電-空白確認書.xlsx";
			if(type == 2) { // 冷氣
				filePath = "./excelFile/冷氣-安裝確認書.xlsx";
			}
			FileInputStream templateFile = new FileInputStream(filePath);
			XSSFWorkbook workbook = new XSSFWorkbook(templateFile);
			XSSFSheet sheet = workbook.getSheetAt(0);

			Row row1 = sheet.getRow(1);
			row1.getCell(1).setCellValue(item.getReceiveName());	// 收貨人
			row1.getCell(3).setCellValue(item.getCellphone());	// 電話
			row1.getCell(5).setCellValue(item.getCreateTime());	// 發單日
			row1.getCell(7).setCellValue(item.getNumber());		// 客單編號

			Row row2 = sheet.getRow(2);
			row2.getCell(1).setCellValue(item.getAddress()); // 裝機地址
			// 商品貨態
			if(item.getMainArrival() == null ||item.getMainArrival().isEmpty()) {
				row2.getCell(7).setCellValue("\u2611 扣庫存  \u2610_____到貨");
			} else {
				row2.getCell(7).setCellValue("\u2610 扣庫存  \u2611 "+item.getMainArrival()+" 到貨");
			}

			Row row4 = sheet.getRow(4);
			String[] list = item.getMaterialsList().split(",");
			if(list.length >= 1) {
				String[] detail = list[0].split("[*]");
				row4.getCell(0).setCellValue(detail[0]); // 商品型號
				row4.getCell(4).setCellValue(detail[1]);        // 數量
			}

			if(list.length >= 2) {
				Row row5 = sheet.getRow(5);
				String[] detail = list[1].split("[*]");
				row5.getCell(0).setCellValue(detail[0]);
				row5.getCell(4).setCellValue(detail[1]);
			}

			if(list.length >= 3) {
				Row row6 = sheet.getRow(6);
				String[] detail = list[2].split("[*]");
				row6.getCell(0).setCellValue(detail[0]);
				row6.getCell(4).setCellValue(detail[1]);
			}

			if(isRecycle) {
				row4.getCell(6).setCellValue("\u2611 是 \u2610 否");	// 舊機回收
				row4.getCell(7).setCellValue("LG");		// 舊機品牌
			} else {
				row4.getCell(6).setCellValue("\u2610 是 \u2611 否");	// 舊機回收
			}

			row4.getCell(8).setCellValue("QRCode");	// QRCode

			Row row7 = sheet.getRow(7);
			row7.getCell(1).setCellValue(item.getRemark());	// 配送備註

			FileOutputStream outputStream = new FileOutputStream("filled_excel.xlsx");
			workbook.write(outputStream);

			excelFile = new File("filled_excel.xlsx");

			outputStream.close();

			templateFile.close();
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

		DepotHeadVo4List item = new DepotHeadVo4List();
		item.setId(9l);
		item.setReceiveName("");
		item.setCellphone("");
		ZonedDateTime dateTime = ZonedDateTime.parse("2023-08-24T06:13:57.000+0000",
				DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
		LocalDate localDate = dateTime.toLocalDate();
		item.setCreateTime(java.sql.Date.valueOf(localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
		item.setNumber("QTCK00000001387");
		item.setAddress("");
		LocalDateTime aDateTime = LocalDateTime.parse("2023-08-24 00:00:00.0",
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
//		item.setMainArrival(aDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
		item.setMaterialsList("大河 4-6坪一級變頻冷暖分離式空調 TAG-S28CYO/TAG-S28CYI     *1");
		item.setRemark("test");


		exportHAConfirm(2, item, Boolean.TRUE, "日立");
	}
}
