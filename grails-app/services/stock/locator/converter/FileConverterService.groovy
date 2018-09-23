package stock.locator.converter

import org.apache.commons.io.FilenameUtils
import javax.annotation.PostConstruct

/**
 * A service that converts csv to xls and vice versa
 * TODO: Refactor in order to make this a part of Spring Batch Jobs!
 *
 * @author Valentin Ponchevniy
 */
class FileConverterService {

    final static String CSV_FORMAT = 'csv'
    final static String XLS_FORMAT = 'xls'


    Converter csvToExcelConverter
    Converter excelToCsvConverter

    @PostConstruct
    void init() throws Exception {
        csvToExcelConverter = new CsvToExcelConverter()
        excelToCsvConverter = new ExcelToCsvConverter()
    }

    File convert(File source, String targetExtension) {
        String extension = FilenameUtils.getExtension(source.path)
        if (extension.equalsIgnoreCase(CSV_FORMAT) && targetExtension.equalsIgnoreCase(XLS_FORMAT)) {
            return csvToExcelConverter.convert(source)
        } else if (extension.equalsIgnoreCase(XLS_FORMAT) && targetExtension.equalsIgnoreCase(CSV_FORMAT)) {
            return excelToCsvConverter.convert(source)
        } else {
            log.warn("No proper converter were found for ${source?.name} and target extension ${targetExtension}.")
            return source
        }
    }
}
