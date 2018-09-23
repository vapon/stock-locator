package stock.locator

import grails.core.support.GrailsConfigurationAware
import org.springframework.batch.core.JobExecution
import org.springframework.core.io.ClassPathResource
import org.springframework.web.multipart.MultipartFile
import stock.locator.converter.FileConverterService
import stock.locator.search.Clause
import grails.config.Config


/**
 * A simple {@Product} controller for performing CRUD and export/import operations
 *
 * @author Valentin Ponchevniy
 */
class ProductController implements GrailsConfigurationAware {
    def productService
    def fileConverterService
    String csvMimeType
    String xlsMimeType
    String encoding

    @Override
    void setConfiguration(Config co) {
        csvMimeType = co.getProperty('grails.mime.types.csv', String, 'text/csv')
        xlsMimeType = co.getProperty('grails.mime.types.cls', String, 'application/vnd.ms-excel')
        encoding = co.getProperty('grails.converters.encoding', String, 'UTF-8')
    }

    def index() {
        Map model = getModel(false)
        render(model: model, view: 'index')
    }

    def showLimited() {
        Map model = getModel(true)
        render(model: model, view: 'limited')
    }

    def show(String id) {
        if (!id) {
            return redirect(action: "index")
        }
        respond(productService.get(id), [view: "edit", model: [searchParams: productService.filterParams(params),
                                                               isViewOnly  : true]])
    }

    def create() {
        respond(new Product(params), [view: "edit", model: [searchParams: productService.filterParams(params)]])
    }

    def edit(String id) {
        if (!id) {
            return redirect(action: "index")
        }
        respond(productService.get(id), [view: "edit", model: [searchParams: productService.filterParams(params)]])
    }

    def importProducts() {
        render(view: 'import', model: [status: params.status, searchParams: productService.filterParams(params)])
    }

    def upload() {
        MultipartFile mf = request.getFile('upload')

        if (!mf?.getName()) {
            log.error("No file was uploaded.")
            redirect action: "importProducts", model: [status: jobExecution?.status]
            return
        }
        File upload = fileConverterService.convert(productService.multipartToTempFile(mf),
                FileConverterService.CSV_FORMAT)

        JobExecution jobExecution = productService.importProducts(upload)
        redirect action: "importProducts", params: [status: jobExecution?.status]
    }

    def exportProducts() {
        File tmpFile = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") +
                "export-${System.currentTimeMillis()}.csv")

        JobExecution jobExecution = productService.exportProducts(tmpFile)

        if (tmpFile.exists() && jobExecution) {
            File result = fileConverterService.convert(tmpFile,
                    FileConverterService.XLS_FORMAT)
            render(file: result, fileName: result.name,
                    contentType: "${csvMimeType};charset=${encoding}")
            return
        }
        flash.error = "Export failed."
        redirect action: "index", method: "GET"
    }

    def getImportSample(String format) {
        File sample
        String mimeType = '*/*'
        if (format.equalsIgnoreCase(FileConverterService.XLS_FORMAT)) {
            sample = new ClassPathResource("samples/xls_import_sample.XLS").getFile()
            mimeType = csvMimeType
        } else if (format.equalsIgnoreCase(FileConverterService.CSV_FORMAT)) {
            sample = new ClassPathResource("samples/csv_import_sample.csv").getFile()
            mimeType = xlsMimeType
        }
        render(file: sample, fileName: "${sample?.name}",
                contentType: "${mimeType};charset=${encoding}")
    }

    def save(String productId) {
        Product product = productService.get(productId) ?: new Product()
        bindData(product, params, [exclude: ['price', 'salesSize']])
        bindNumeric(product, "price", params.price as String)
        bindNumeric(product, "salesSize", params.salesSize as String)
        productService.save(product)

        redirect action: "index", method: "GET", params: productService.filterParams(params)
    }

    private void bindNumeric(Product product, String fieldName, String fieldValue) {
        if (fieldValue) {
            BigDecimal value = productService.convertNumericStringToBigDecimal(fieldValue, request.locale)
            if (value) {
                product."${fieldName}" = value
            } else {
                log.error('Failed to convert ${fieldName} ${fieldValue}')
                flash.error = "Failed to convert ${fieldName} ${fieldValue}"
            }
        }
    }


    def delete(String id) {
        productService.delete(id)
        redirect action: "index", method: "GET"
    }

    private Map getModel(isForLimited = false) {
        Integer max = params.getInt('max') ?: productService.MAX_DEFAULT
        Integer offset = params.getInt('offset') ?: productService.OFFSET_DEFAULT

        if (isForLimited) {
            if (!params.quantity) {
                params.quantity = productService.MIN_QUANTITY_DEFAULT as String
            }
        } else {
            params.remove('quantity')
        }

        List searchParameters = productService.getSearchParameters(params)

        List searchResult = productService.searchProducts(max, offset, searchParameters, Clause.AND)
        Integer totalCount = 0
        if (searchResult) {
            totalCount = searchResult.totalCount
        }
        Map currentSearchParams = [max: max, offset: offset]
        searchParameters.each { currentSearchParams.put(it.name, it.value?.toString()) }
        Map model = [
                searchResult: searchResult,
                searchParams: currentSearchParams,
                total       : totalCount,
                max         : max,
                offset      : offset
        ]
        return model
    }
}
