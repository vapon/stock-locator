package stock.locator.search

/**
 *A search parameter representation
 *
 * @author Valentin Ponochevniy
 */
class SearchParam {
    String name
    Object value
    Clause clause

    def getSearchValue() {
        if (clause && value && clause in [Clause.I_LIKE, Clause.LIKE]) {
            //TODO: db indexes will not work
            return '%' + value.toString() + '%'
        }
        return value
    }

    @Override
    String toString() {
        return "${name}:${value}:${clause}"
    }
}
