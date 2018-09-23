package stock.locator

class Product {
    static mapping = {
        id generator: 'identity'
        version false
    }

    static constraints = {
        productId blank: false, nullable: false, maxSize: 255, unique: true
        name blank: false, nullable: true, maxSize: 255
        brand blank: true, nullable: true, maxSize: 255
        description blank: true, nullable: true, maxSize: 2000
        imageUrl nullable: true, url: true
        price min: new BigDecimal('0'), max: new BigDecimal('9999999999999.99'), nullable: true
        currencyId blank: true, maxSize: 5, nullable: true
        salesSize min: new BigDecimal('0'), max: new BigDecimal('9999999999999.99'), nullable: true
        salesUnitId blank: false, nullable: true
        quantity min: Integer.valueOf(0), max: Integer.valueOf(Integer.MAX_VALUE), nullable: false
    }

    /**
     * External product id which is unique per brand
     */
    String productId
    /**
     * Commercial product name
     */
    String name
    /**
     * Brand or a manufacturer name
     */
    String brand
    /**
     * Description
     */
    String description
    /**
     * Image URL
     */
    String imageUrl
    /**
     * Net price
     */
    BigDecimal price
    /**
     * Currency code
     */
    String currencyId
    /**
     * Sales size that could be 50 (cc), 10 (meters) and etc.
     */
    BigDecimal salesSize
    /**
     * Sales Unit Id like l,ml,m and etc.
     */
    String salesUnitId
    /**
     * Available stock quantity
     */
    Integer quantity
}
