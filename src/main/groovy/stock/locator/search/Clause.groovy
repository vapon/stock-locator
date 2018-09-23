package stock.locator.search

/**
 * A simple enum with supported query clauses
 *
 * @author Valentin Ponochevniy
 */
enum Clause {
    AND('and'),
    OR('or'),
    LIKE('like'),
    I_LIKE('ilike'),
    EQ('eq'),
    GR('ge'),
    GT('gt'),
    LE('le'),
    LT('lt'),
    IN('in'),
    IS_NULL('isNull')

    private final String clause

    Clause(String clause) {
        this.clause = clause
    }

    @Override
    String toString() {
        return this.clause
    }
}
