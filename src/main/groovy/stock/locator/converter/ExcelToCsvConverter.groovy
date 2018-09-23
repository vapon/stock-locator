package stock.locator.converter

import grails.util.Holders
import org.apache.commons.io.FilenameUtils
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Workbook
//import org.springframework.beans.factory.annotation.Autowired

/**
 *An utility class that makes xls -> csv conversions
 *
 * @author Valentin Ponchevniy
 */
class ExcelToCsvConverter implements Converter {

    def grailsApplication

    ExcelToCsvConverter() {
        grailsApplication = Holders.grailsApplication
    }

    @Override
    File convert(File file) {
        String encoding = grailsApplication?.config.getProperty('converters.encoding') ?: "UTF-8"

        File result = new File("${FilenameUtils.removeExtension(file.path)}.csv")
        DataFormatter formatter = new DataFormatter()
        PrintStream out = new PrintStream(new FileOutputStream(result),
                true, encoding)

        file.withDataInputStream { inputStream ->
            Workbook wb = new HSSFWorkbook(inputStream)

            wb?.getSheets().eachWithIndex { sheet, index ->
                sheet.eachWithIndex { HSSFRow row, int rowIdx ->
                    StringBuffer line = new StringBuffer()
                    row.eachWithIndex { HSSFCell cell, int cellIdx ->
                        if (cellIdx != 0) {
                            line.append(';')
                        }
                        String text = formatter.formatCellValue(cell)
                        line.append(text)
                    }
                    if (line.length()) {
                        if (rowIdx != 0) {
                            out.println()
                        }
                        out.print(line)
                    } else {
                        return
                    }
                }
            }
            out.close()
            inputStream.close()
        }

        return result
    }
}
