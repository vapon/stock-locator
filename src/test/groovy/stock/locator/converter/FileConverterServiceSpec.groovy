package stock.locator.converter

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(FileConverterService)
class FileConverterServiceSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test unchanged file"() {
        given:
        File target = new File("test.csv")
        target.text = "csv;csv"
        File result = service.convert(target, FileConverterService.CSV_FORMAT)

        expect: "fix me"
        target.length() == result.length()
    }
}
