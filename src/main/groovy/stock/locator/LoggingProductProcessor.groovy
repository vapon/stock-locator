package stock.locator

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemProcessor

/**
 * @author Valentin Ponochevniy
 */
class LoggingProductProcessor implements ItemProcessor<Product, Product> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingProductProcessor.class)

    @Override
    Product process(Product item) throws Exception {
        LOGGER.info("Processing product info: {}", item)
        return item
    }
}
