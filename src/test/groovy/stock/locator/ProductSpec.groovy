package stock.locator

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Product)
class ProductSpec extends Specification {

    void "test invalid product saving"() {
        given: 'A product is not saved with invalid data'
        Product product = new Product(data)
        product.save()

        expect:
        product.hasErrors() == hasErrors
        product.errors.fieldErrorCount == count
        product.errors.fieldErrors*.field as SortedSet == errorFields
        Product.count() == savedCount

        where:
        savedCount || data                                                                       || hasErrors || count || errorFields
        0          || [:]                                                                        || true      || 2     || ['productId', 'quantity'] as SortedSet
        0          || ['productId': 'A218', 'name': 'SI EAU', 'brand': 'Armani', 'quantity': -1] || true      || 1     || ['quantity'] as Set
    }

    void "test product saving"() {
        given: 'A product is saved with valid data'
        Product product = new Product(data)
        product.save()

        expect:
        product.hasErrors() == hasErrors
        product.errors.fieldErrorCount == count
        product.errors.fieldErrors*.field as SortedSet == errorFields

        where:
        savedCount || data                                                                          || hasErrors || count || errorFields
        1          || ['productId': 'A218', 'name': 'SI EAU', 'brand': 'Armani', 'quantity': 0]     || false     || 0     || [] as Set
        1          || ['productId': 'A218', 'name': 'SI EAU', 'brand': 'Armani', 'quantity': 10000] || false     || 0     || [] as Set
    }
}
