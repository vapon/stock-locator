package stock.locator

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JdbcPagingItemReader
import org.springframework.batch.item.database.PagingQueryProvider
import org.springframework.batch.item.database.support.H2PagingQueryProvider
import org.springframework.batch.item.file.FlatFileHeaderCallback
import org.springframework.batch.item.file.FlatFileItemWriter
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor
import org.springframework.batch.item.file.transform.DelimitedLineAggregator
import org.springframework.batch.item.file.transform.FieldExtractor
import org.springframework.batch.item.file.transform.LineAggregator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.io.FileSystemResource
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.batch.item.database.Order

import javax.sql.DataSource

/**
 * @author Valentin Ponochevniy
 */
@Configuration
@PropertySource("classpath:batch.properties")
class DatabaseToCsvJobConfig {
    @Autowired
    public JobBuilderFactory jobBuilderFactory
    @Autowired
    public StepBuilderFactory stepBuilderFactory
    @Autowired
    public DataSource dataSource

    private static final String DEFAULT_PAGE_SIZE = 1
    private static final Integer DEFAULT_CHUNK_SIZE = 1


    @Bean
    ItemReader<Product> databaseCsvItemReader() {
        JdbcPagingItemReader<Product> databaseReader = new JdbcPagingItemReader()

        databaseReader.dataSource = dataSource
        databaseReader.pageSize = DEFAULT_PAGE_SIZE
        databaseReader.rowMapper = new BeanPropertyRowMapper<>(Product.class)

        PagingQueryProvider queryProvider = createQueryProvider()
        databaseReader.setQueryProvider(queryProvider)

        return databaseReader
    }

    private Map<String, Order> sortByProductIdAsc() {
        Map<String, Order> sortConfiguration = [:]
        sortConfiguration.put("product_id", Order.ASCENDING)
        return sortConfiguration
    }

    @Bean
    @StepScope
    FlatFileItemWriter<Product> databaseCsvItemWriter(@Value("#{jobParameters['export.file.path']}") String exportFilePath) {
        FlatFileItemWriter<Product> csvFileWriter = new FlatFileItemWriter()

        //TODO: Refactor me! auto (default) export is not needed (disabled in batch properties)
        if (!exportFilePath) {
            File tmpFile = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") +
                    "export-${System.currentTimeMillis()}.csv")
            exportFilePath = tmpFile.path
        }

        FlatFileHeaderCallback headerWriter = headerWriter
        csvFileWriter.setHeaderCallback(headerWriter)

        csvFileWriter.resource = new FileSystemResource(exportFilePath)

        LineAggregator<Product> lineAggregator = createProductLineAggregator()
        csvFileWriter.setLineAggregator(lineAggregator)

        return csvFileWriter
    }

    private PagingQueryProvider createQueryProvider() {
        H2PagingQueryProvider queryProvider = new H2PagingQueryProvider()

        queryProvider.selectClause = "SELECT product_id, name, brand, image_url, quantity"
        queryProvider.fromClause = "FROM product"
        queryProvider.sortKeys = sortByProductIdAsc()

        return queryProvider
    }

    private FlatFileHeaderCallback getHeaderWriter() {
        return new FlatFileHeaderCallback() {

            @Override
            void writeHeader(Writer writer) throws IOException {
                writer.write("PRODUCT_ID;NAME;BRAND;IMAGE_URL;QUANTITY")
            }
        }
    }

    private LineAggregator<Product> createProductLineAggregator() {
        DelimitedLineAggregator<Product> lineAggregator = new DelimitedLineAggregator<>()
        lineAggregator.setDelimiter(";")

        FieldExtractor<Product> fieldExtractor = createProductFieldExtractor()
        lineAggregator.fieldExtractor = fieldExtractor

        return lineAggregator
    }

    private FieldExtractor<Product> createProductFieldExtractor() {
        BeanWrapperFieldExtractor<Product> extractor = new BeanWrapperFieldExtractor()
        extractor.names = ["productId", "name", "brand", "imageUrl", "quantity"] as String[]
        return extractor
    }

    @Bean
    ItemProcessor<Product, Product> databaseCsvItemProcessor() {
        return new LoggingProductProcessor()
    }

    @Bean
    Step databaseToCsvFileStep(ItemReader<Product> databaseCsvItemReader,
                               ItemProcessor<Product, Product> databaseCsvItemProcessor,
                               ItemWriter<Product> databaseCsvItemWriter) {
        return stepBuilderFactory.get("databaseToCsvFileStep")
                .<Product, Product> chunk(DEFAULT_CHUNK_SIZE)
                .reader(databaseCsvItemReader)
                .processor(databaseCsvItemProcessor)
                .writer(databaseCsvItemWriter)
                .build()
    }

    @Bean
    Job exportProductsJob(@Qualifier("databaseToCsvFileStep") Step csvProductStep) {
        return jobBuilderFactory.get("exportProductsJob")
                .incrementer(new RunIdIncrementer())
                .flow(csvProductStep)
                .end()
                .build()
    }

}
