package stock.locator.converter

import org.apache.commons.io.FilenameUtils
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Workbook

/**
 *An utility class that makes csv-> xls conversions
 *
 * @author Valentin Ponchevniy
 */
class CsvToExcelConverter implements Converter {

    @Override
    File convert(File file) {
        File result = new File("${FilenameUtils.removeExtension(file.path)}.xls")
        Workbook hwb = new HSSFWorkbook()
        HSSFSheet sheet = hwb.createSheet("products")


        try {
            Integer index = 0
            Integer columnSize = 0
            file.eachLine { String line ->
                HSSFRow row = sheet.createRow(index)
                String[] values = line.split(";")
                if (!columnSize || columnSize < values.size()) {
                    columnSize = values.size()
                }
                line.split(";").eachWithIndex { String cellValue, Integer cellIndex ->
                    HSSFCell cell = row.createCell(cellIndex)
                    cell.setCellValue(cellValue)
                }
                index++
            }

            (0..columnSize).each {
                sheet.autoSizeColumn(it)
            }

            FileOutputStream fileOut = new FileOutputStream(result)
            hwb.write(fileOut)
            fileOut.flush()
            fileOut.close()
        } catch (IOException ex) {
            ex.printStackTrace()
        }

        return result
    }
}