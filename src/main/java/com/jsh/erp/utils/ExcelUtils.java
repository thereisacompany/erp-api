package com.jsh.erp.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.datasource.vo.DepotHeadVo4List;
import com.jsh.erp.datasource.vo.MaterialsListVo;
import jxl.*;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.format.*;
import jxl.write.*;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.lang.Boolean;

import static org.apache.poi.ss.usermodel.Workbook.PICTURE_TYPE_PNG;

public class ExcelUtils {

	public static WritableFont arial14font = null;

	public static final int EMU_PER_PIXEL = 9525;
	public static final int EMU_PER_POINT = 12700;

	public static File exportPicking(List<DepotHeadVo4List> items, String name) {
		File excelFile = null;
		try{
			String filePath = "./excelFile/配送單-撿貨總表.xlsx";
			String outputName = "%s配送單-撿貨總表.xlsx";

			LocalDateTime now = LocalDateTime.now();

			// 匯出檔名
			outputName = String.format(outputName, now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

			FileInputStream templateFile = new FileInputStream(filePath);
			XSSFWorkbook workbook = new XSSFWorkbook(templateFile);

			XSSFSheet sheet = workbook.getSheetAt(0);
			Row row1 = sheet.getRow(1);
			row1.getCell(1).setCellValue(now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
			Row row2 = sheet.getRow(2);
			row2.getCell(1).setCellValue(now.format(DateTimeFormatter.ofPattern("HH:mm")));

			Row row;
			int i = 0;
			for(DepotHeadVo4List depotHeadVo : items) {
				row = sheet.getRow(5+i);

				row.getCell(1).setCellValue(depotHeadVo.getCustomNumber());
				row.getCell(2).setCellValue(depotHeadVo.getSourceNumber());
				String showId = String.format("%03d", depotHeadVo.getOrganId());
//				System.out.println(">>"+showId+" "+depotHeadVo.getOrganName());
				row.getCell(3).setCellValue(showId+" "+depotHeadVo.getOrganName());
				row.getCell(4).setCellValue(depotHeadVo.getMaterialNumber());
				if(depotHeadVo.getMaterialsList()!=null && !depotHeadVo.getMaterialsList().isEmpty()
						&& !depotHeadVo.getMaterialsList().equals("@")) {
					String[] tmpName = depotHeadVo.getMaterialsList().split("@");
					row.getCell(5).setCellValue(tmpName[0]);
					if (tmpName.length > 1) {
						row.getCell(6).setCellValue(tmpName[1]);
					} else {
						row.getCell(6).setCellValue("");
					}
				} else {
					row.getCell(5).setCellValue("");
					row.getCell(6).setCellValue("");
				}
				row.getCell(7).setCellValue(String.valueOf(depotHeadVo.getMaterialCount().intValue()));
				row.getCell(8).setCellValue(depotHeadVo.getCounterName());
				JSONObject remarkJson = JSONObject.parseObject(depotHeadVo.getRemark());
				row.getCell(9).setCellValue(remarkJson.getString("memo"));

				i++;
			}
			Row rowName = sheet.getRow(26);
			rowName.getCell(0).setCellValue("製表人："+name);
			rowName.getCell(7).setCellValue("揀貨人員：");

			FileOutputStream outputStream = new FileOutputStream(outputName);
			workbook.write(outputStream);

			excelFile = new File(outputName);
			System.out.println("excel file name >>>"+excelFile.getName());
			outputStream.close();

			templateFile.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("exportPicking >>>"+e);
		}
		return excelFile;
	}

	/**
	 * @param item
	 * @return
	 */
	public static File exportHAConfirm(DepotHeadVo4List item, MaterialsListVo material) {
		File excelFile = null;

//		System.out.println("exportHAConfirm item >>>"+item);
		try {
			JSONObject remarkJson = JSONObject.parseObject(item.getRemark());

			String filePath = "./excelFile/2023-12-05-家電-安裝確認書.xlsx";
			String outputName = "%s家電-安裝確認書.xlsx";
			if(remarkJson != null) {
				if(remarkJson.containsKey("confirm")) {
					if (remarkJson.getString("confirm").contains("冷氣")) { // 冷氣
						filePath = "./excelFile/2023-12-05-冷氣-安裝確認書.xlsx";
						outputName = "%s冷氣-安裝確認書.xlsx";
					}
				}
			}
			int startRow = 0;
			boolean isPickup1 = item.getSubType().equals(BusinessConstants.DEPOTHEAD_SUBTYPE_PICKUP1);
			if(isPickup1) {
				filePath = "./excelFile/門市取貨安裝-空白確認書.xlsx";
				outputName = "%s門市取貨安裝確認書.xlsx";
				startRow = 1;
			}

			FileInputStream templateFile = new FileInputStream(filePath);
			XSSFWorkbook workbook = new XSSFWorkbook(templateFile);
			XSSFSheet sheet = workbook.getSheetAt(0);

			Row row1 = sheet.getRow(1+startRow);
			row1.getCell(1).setCellValue(item.getReceiveName());	// 收貨人
			row1.getCell(3).setCellValue(item.getCellphone());	// 電話
			row1.getCell(5).setCellValue(item.getCreateTime());	// 發單日

			StringBuilder receiveNumber = new StringBuilder();
			receiveNumber.append(item.getNumber());
			String subNumber = "";
			if(item.getCustomNumber()!=null && !item.getCustomNumber().isEmpty()) {
				subNumber += item.getCustomNumber();
			}
			if(item.getSourceNumber()!=null && !item.getSourceNumber().isEmpty()) {
				if(!subNumber.isEmpty()) {
					subNumber += " / ";
				}
				subNumber += item.getSourceNumber();
			}
			if(!subNumber.isEmpty()) {
				receiveNumber.append("\n");
				receiveNumber.append(subNumber);
			}
//			System.out.println("receiveNumber  >>>"+receiveNumber.toString());
			row1.getCell(7).setCellValue(receiveNumber.toString());		// 客單編號

			// 匯出檔名
			if(material != null) {
				outputName = String.format(outputName, item.getNumber()+"-"+material.getId());
			} else {
				outputName = String.format(outputName, item.getNumber());
			}

			Row row2 = sheet.getRow(2+startRow);
			row2.getCell(1).setCellValue(item.getAddress()); // 裝機地址
			// 商品貨態
//			if(item.getMainArrival() == null ||item.getMainArrival().isEmpty()) {
//				row2.getCell(7).setCellValue("\u2611 扣庫存  \u2610_____到貨");
//			} else {
//				row2.getCell(7).setCellValue("\u2610 扣庫存  \u2611 "+item.getMainArrival()+" 到貨");
//			}

			Row row4 = sheet.getRow(4+startRow);
			if(isPickup1) {
				// 商品貨態
//				row2.getCell(7).setCellValue();
				// 商品型號
				row4.getCell(0).setCellValue(item.getMaterialsList());
				if (item.getMaterialCount() != null) {
					BigDecimal amount = new BigDecimal(String.valueOf(item.getMaterialCount()));
					row4.getCell(4).setCellValue(amount.intValue());
				} else {
					row4.getCell(4).setCellValue(0);
				}

			} else {
				if (material == null) {
					// 出貨倉別
					row2.getCell(7).setCellValue(item.getDepotList());
					//品號
					row4.getCell(0).setCellValue(item.getMaterialNumber());
					// 商品型號
					row4.getCell(1).setCellValue(item.getMaterialsList());
					if (item.getMaterialCount() != null) {
						BigDecimal amount = new BigDecimal(String.valueOf(item.getMaterialCount()));
						row4.getCell(4).setCellValue(amount.intValue());
					} else {
						row4.getCell(4).setCellValue(0);
					}
				} else {
					if(material.getDepotList() != null) {
						row2.getCell(7).setCellValue(material.getDepotList());
					}

					if(material.getMaterialNumber() != null) {
						row4.getCell(0).setCellValue(material.getMaterialNumber());
					}
					if(material.getMaterialsList() != null) {
						row4.getCell(1).setCellValue(material.getMaterialsList());
					}

					if (material.getMaterialCount() != null) {
						BigDecimal amount = new BigDecimal(String.valueOf(material.getMaterialCount()));
						row4.getCell(4).setCellValue(amount.intValue());
					} else {
						row4.getCell(4).setCellValue(0);
					}
				}
			}

			// 安裝方式
			if(remarkJson != null) {
				row4.getCell(5).setCellValue(remarkJson.getString("install"));

				if(remarkJson.getString("recycle").equals("是")) {
					row4.getCell(6).setCellValue("\u2611 是 \u2610 否");	// 舊機回收
//				row4.getCell(7).setCellValue("LG");		// 舊機品牌
				} else {
					row4.getCell(6).setCellValue("\u2610 是 \u2611 否");	// 舊機回收
				}
			}

			// TODO QRCode
			String FILE_MIME_TYPE = "PNG";
			int QRCODE_IMAGE_WIDTH = isPickup1?50:80;
			int QRCODE_IMAGE_HEIGHT = isPickup1?50:80;
			byte[] qrcode = generateQRCodeImage(QRCODE_IMAGE_WIDTH, QRCODE_IMAGE_HEIGHT, FILE_MIME_TYPE, item.getNumber());
			// Set the asset no in the second cell and brand in the third cell

			XSSFDrawing drawing = sheet.createDrawingPatriarch();

			// Create an anchor to position the image in the first cell
			ClientAnchor qrCodeAnchor = workbook.getCreationHelper().createClientAnchor();
//                    ClientAnchor qrCodeAnchor = drawing.createAnchor(1000, 1000, 0, 0, 0 ,index ,1 ,index);
//                    qrCodeAnchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
			qrCodeAnchor.setCol1(7+startRow);
//                    qrCodeAnchor.setCol2(1);
			qrCodeAnchor.setRow1(4+startRow);
//                    qrCodeAnchor.setRow2(index);
			qrCodeAnchor.setDx1(75);
//                    qrCodeAnchor.setDx2(500);
			qrCodeAnchor.setDy1(60);
//                    qrCodeAnchor.setDy2(500);

			// Insert the QR Code image into the first cell
			Picture qrCodePicture = drawing.createPicture(qrCodeAnchor,
					workbook.addPicture(qrcode, PICTURE_TYPE_PNG));
			qrCodePicture.resize();

//			row4.getCell(7).setCellValue("QRCode");	// QRCode

			Row row7 = sheet.getRow(7+startRow);
			if(remarkJson != null) {
				row7.getCell(1).setCellValue(remarkJson.getString("memo"));    // 配送備註

				if(isPickup1) {
					JSONObject store = remarkJson.getJSONObject("store");

					if(store!=null) {
						Row row0 = sheet.getRow(1);
						row0.getCell(1).setCellValue(store.getString("man"));    // 取貨人
						row0.getCell(3).setCellValue(store.getString("phone"));    // 電話
						row0.getCell(5).setCellValue(store.getString("name"));    // 門市名稱
						row0.getCell(7).setCellValue(store.getString("address"));    // 門市地址
					}
				}
			}

			FileOutputStream outputStream = new FileOutputStream(outputName);
			workbook.write(outputStream);

			excelFile = new File(outputName);
			System.out.println("excel file name >>>"+excelFile.getName());
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

	private static byte[] generateQRCodeImage(int width, int height, String type, String contents) throws WriterException, IOException {

		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		Map<EncodeHintType, Object> hints = new HashMap<>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		BitMatrix bitMatrix = qrCodeWriter.encode(contents, BarcodeFormat.QR_CODE, width, height, hints);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, type, outputStream);

		byte[] qrcode = outputStream.toByteArray();
		outputStream.close();

		return qrcode;
//        qrcode = Base64.getEncoder().encodeToString(imageBytes);
//        return Base64.getEncoder().encodeToString(imageBytes);
	}

	public static void main(String[] args) throws Exception {
		String msg = "12345";
		System.out.println(msg.indexOf("@"));

		DepotHeadVo4List item = new DepotHeadVo4List();
		item.setId(9l);
		item.setReceiveName("aaa");
		item.setCellphone("09123456789");
		ZonedDateTime dateTime = ZonedDateTime.parse("2023-08-24T06:13:57.000+0000",
				DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
		LocalDate localDate = dateTime.toLocalDate();
		item.setCreateTime(java.sql.Date.valueOf(localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
		item.setNumber("S20231123160600");
		item.setAddress("");
		LocalDateTime aDateTime = LocalDateTime.parse("2023-08-24 00:00:00",
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//		item.setMainArrival(aDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
		item.setMaterialsList("大河 4-6坪一級變頻冷暖分離式空調 TAG-S28CYO/TAG-S28CYI     *1");
		item.setDepotList("台北倉");
		item.setMaterialNumber("25|00001");
		JSONObject json = new JSONObject();
		json.put("memo", "test");
		json.put("confirm", "冷氣確認書");
		json.put("install", "標準安裝");
		json.put("recycle", "是");
		item.setRemark(json.toJSONString());


		exportHAConfirm(item, null);
	}
}
