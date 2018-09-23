package stock.locator

import grails.transaction.NotTransactional
import grails.transaction.Transactional
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.JobParametersInvalidException
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException
import org.springframework.web.multipart.MultipartFile
import stock.locator.search.Clause
import stock.locator.search.SearchParam
import javax.batch.operations.JobRestartException
import java.text.NumberFormat
import java.text.ParseException

/**
 * A simple {@Product} service for performing CRUD and export/import operations
 *
 * @author Valentin Ponchevniy
 */
@Transactional
class ProductService {

    private static List SEARCHABLE_STRING_PARAMS = ['brand', 'name'].asImmutable()
    private static List PAGING_PARAMS = ['max', 'offset'].asImmutable()
    private static String QUANTITY_PARAM = 'quantity'
    static Locale DEFAULT_LOCALE = Locale.ENGLISH
    static Integer MAX_DEFAULT = 2
    static Integer OFFSET_DEFAULT = 0
    static Integer MIN_QUANTITY_DEFAULT = 5

    def searchService
    Job importProductsJob
    Job exportProductsJob
    JobLauncher jobLauncher

    /**
     * Finds product by {@link String} productId
     *
     * @param id
     * @return a product
     */
    Product get(String id) {
        return Product.findByProductId(id)
    }

    /**
     * Performs product search
     *
     * @param max
     * @param offset
     * @param searchParams a list of ${link SearchParam} search parameters
     * @param relationClause a clause {@link Clause} that defines relations between search parameters
     * @return a search result
     */
    List searchProducts(Integer max, Integer offset, List<SearchParam> searchParams = [], Clause relationClause) {
        return searchService.searchEntities(max, offset, searchParams, Product.class.name, relationClause)
    }

    /**
     * Saves a product
     *
     * @param product
     * @return
     */
    Product save(Product product) {
        product.validate()
        log.debug("Saving ${product.productId}.")

        if (!product.hasErrors()) {
            product.save(failOnError: true)
        } else {
            log.error("Save failed.")
        }
        return product
    }

    /**
     * Deletes a product
     *
     * @param id
     * @return
     */
    Boolean delete(String id) {
        Product product = this.get(id)
        Boolean isDeleted = false
        if (product) {
            product.delete(failOnError: true, flush: true)
            isDeleted = true
        }
        return isDeleted
    }

    /**
     * Converts number like string to {@BigDecimal} representation
     *
     * @param numString
     * @param locale
     * @return converted value or null if failed
     */
    BigDecimal convertNumericStringToBigDecimal(String numString, Locale locale = null) {
        BigDecimal value
        NumberFormat nf = NumberFormat.getInstance(locale ?: DEFAULT_LOCALE)
        try {
            value = new BigDecimal(nf.parse(numString).toString())
            log.debug("Formatted ${numString} to ${value}.")
        } catch (ParseException e) {
            log.error(e)
        }
        return value
    }

    /**
     * Retrieves specific product search parameters from incoming params map
     *
     * @param params
     * @return
     */
    List<SearchParam> getSearchParameters(Map params) {
        List<SearchParam> searchParams = []
        params?.each { key, value ->
            if (value) {
                if (key in SEARCHABLE_STRING_PARAMS) {
                    searchParams << new SearchParam(name: key, value: value, clause: Clause.I_LIKE)
                } else if (key == QUANTITY_PARAM) {
                    searchParams << new SearchParam(name: key, value: value as Integer, clause: Clause.LE)
                }
            }
        }
        return searchParams
    }

    /**
     * Starts import batch job
     *
     * @param upload a file to import
     * @return {@JobExecution} result
     */
    @NotTransactional
    JobExecution importProducts(File upload) {
        JobExecution jobExecution
        JobParametersBuilder builder = new JobParametersBuilder()
        builder.addString("upload.file.path", upload.path)
        builder.addLong("time", System.currentTimeMillis())

        try {
            jobExecution = jobLauncher.run(importProductsJob,
                    builder.toJobParameters())
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
        JobInstanceAlreadyCompleteException | JobInstanceAlreadyCompleteException |
        JobParametersInvalidException e) {
            log.error("Import Error:", e)
            e.printStackTrace()
        }
        return jobExecution
    }

    /**
     * Exports product to temp file that cabe output to a consumer
     *
     * @param tmpFile a file to export in
     * @return {@JobExecution} result
     */
    @NotTransactional
    JobExecution exportProducts(File tmpFile) {
        JobExecution jobExecution
        JobParametersBuilder builder = new JobParametersBuilder()
        builder.addString("export.file.path", tmpFile.path)

        try {
            jobExecution = jobLauncher.run(exportProductsJob,
                    builder.toJobParameters())
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
        JobInstanceAlreadyCompleteException | JobInstanceAlreadyCompleteException |
        JobParametersInvalidException e) {
            log.error("Export Error:", e)
            e.printStackTrace()
        }

        return jobExecution
    }

    /**
     * Filters only valuable search params to preserve search results
     *
     * @param params
     * @return a map of cleaned params
     */
    Map filterParams(Map params) {
        Set filterKeys = []
        Map filteredParams = [:]
        filterKeys.add(QUANTITY_PARAM)
        filterKeys.addAll(SEARCHABLE_STRING_PARAMS)
        filterKeys.addAll(PAGING_PARAMS)
        filterKeys.each { key ->
            def value = params.get(key)
            if (value) {
                filteredParams.put(key, value)
            }
        }
        return filteredParams
    }

    /**
     * Creates a temp file in System's temp folder from {@MultipartFile}
     *
     * @param multipart
     * @return a temporary file
     */
    File multipartToTempFile(MultipartFile multipart) {
        File tmpFile = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") +
                multipart.getOriginalFilename())
        multipart.transferTo(tmpFile)
        return tmpFile
    }
}
