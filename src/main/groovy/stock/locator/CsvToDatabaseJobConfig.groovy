package stock.locator

import org.springframework.batch.core.Job
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.listener.JobExecutionListenerSupport
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider
import org.springframework.batch.core.Step
import org.springframework.batch.item.database.JdbcBatchItemWriter
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.LineMapper
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper
import org.springframework.batch.item.file.mapping.DefaultLineMapper
import org.springframework.batch.item.file.mapping.FieldSetMapper
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer
import org.springframework.batch.item.file.transform.LineTokenizer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

import javax.sql.DataSource


/**
 * CSV to DB batch job
 *
 * @author Valentin Ponochevniy.
 */
@Configuration
@EnableBatchProcessing
@PropertySource("classpath:batch.properties")
class CsvToDatabaseJobConfig {

    @Autowired
    public JobBuilderFactory jobBuilderFactory
    @Autowired
    public StepBuilderFactory stepBuilderFactory
    @Autowired
    public DataSource dataSource

    private static final Integer DEFAULT_CHUNK_SIZE = 10

    @Bean
    @StepScope
    FlatFileItemReader<Product> csvItemReader(@Value("#{jobParameters['upload.file.path']}") String csvFilePath) {
        FlatFileItemReader<Product> csvFileReader = new FlatFileItemReader<>()
        //demo products
        Resource resource = new ClassPathResource("products.csv")

        if (csvFilePath) {
            resource = new FileSystemResource(csvFilePath)
        }

        csvFileReader.resource = resource
        csvFileReader.linesToSkip = 1

        LineMapper<Product> productLineMapper = createLineMapper()
        csvFileReader.lineMapper = productLineMapper

        return csvFileReader
    }

    @Bean
    ItemProcessor<Product, Product> databaseCsvItemProcessor() {
        return new LoggingProductProcessor()
    }

    @Bean
    JobExecutionListenerSupport listener() {
        JobExecutionListenerSupport listener = new JobExecutionListenerSupport()
        return listener
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    ItemWriter<Product> csvFileDatabaseItemWriter(NamedParameterJdbcTemplate jdbcTemplate) {
        JdbcBatchItemWriter<Product> databaseItemWriter = new JdbcBatchItemWriter<>()
        databaseItemWriter.itemSqlParameterSourceProvider = new BeanPropertyItemSqlParameterSourceProvider<>()
        databaseItemWriter.setDataSource(dataSource)
        databaseItemWriter.setJdbcTemplate(jdbcTemplate)
        databaseItemWriter.setSql("INSERT INTO product (product_id, name, brand, image_url, quantity)" +
                " VALUES (:productId, :name, :brand, :imageUrl, :quantity)")

        return databaseItemWriter
    }

    @Bean
    NamedParameterJdbcTemplate jdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSource)
    }

    @Bean
    Job importProductsJob(JobExecutionListenerSupport listener, Step stepOne) {
        return jobBuilderFactory.get("importProductsJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(stepOne)
                .end()
                .build()
    }

    @Bean
    Step stepOne(ItemWriter csvFileDatabaseItemWriter) {
        return stepBuilderFactory.get("stepOne")
                .<Product, Product> chunk(DEFAULT_CHUNK_SIZE)
                .reader(csvItemReader())
                .processor(databaseCsvItemProcessor())
                .writer(csvFileDatabaseItemWriter)
                .build()
    }

    private LineMapper<Product> createLineMapper() {
        DefaultLineMapper<Product> productLineMapper = new DefaultLineMapper<>()

        LineTokenizer productLineTokenizer = createProductLineTokenizer()
        productLineMapper.lineTokenizer = productLineTokenizer

        FieldSetMapper<Product> productFieldSetMapper = createProductFieldSetMapper()
        productLineMapper.fieldSetMapper = productFieldSetMapper

        return productLineMapper
    }

    FieldSetMapper<Product> createProductFieldSetMapper() {
        BeanWrapperFieldSetMapper<Product> productFieldSetMapper = new BeanWrapperFieldSetMapper<>()
        productFieldSetMapper.targetType = Product.class

        return productFieldSetMapper
    }

    LineTokenizer createProductLineTokenizer() {
        DelimitedLineTokenizer productLineTokenizer = new DelimitedLineTokenizer()
        productLineTokenizer.delimiter = ";"
        productLineTokenizer.names = ["productId", "name", "brand", "imageUrl", "quantity"] as String[]
        return productLineTokenizer
    }
}
