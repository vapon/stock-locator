package stock.locator.search

import grails.transaction.Transactional
import org.grails.datastore.mapping.query.api.Criteria

@Transactional
class SearchService {

    def grailsApplication

    /**
     * Method provides basic search capabilities
     *
     * @param max
     * @param offset
     * @param searchParams a list of {@link SearchParam} parameters
     * @param className a target GORM class
     * @param relationClause a clause which defines relationships between search parameters
     * @return a search result that allows to get a total count or an empty list
     */
    List searchEntities(Integer max, Integer offset, List<SearchParam> searchParams = [], String className, Clause relationClause) {
        log.info("Performing search for ${className} ")

        List items = []
        Class clazz = grailsApplication.getDomainClass(className)?.clazz
        if (!clazz) {
            return items
        }
        Criteria criteria = clazz.createCriteria()
        List<SearchParam> filteredSearchParams = cleanSearchParameters(searchParams, clazz)

        return search(max, offset, filteredSearchParams, criteria, relationClause)
    }

    private List search(max, offset, List<SearchParam> searchParams, Criteria criteria, Clause relationClause) {
        log.info("Performing search by ${searchParams} (${relationClause}, ${max}, ${offset})")

        List items = criteria.list([max: max, offset: offset]) {
            if (searchParams) {
                "${relationClause}" {
                    for (SearchParam searchParam in searchParams) {
                        if (searchParam.searchValue) {
                            "${searchParam.clause}"(searchParam.name, searchParam.searchValue)
                        } else {
                            "${Clause.IS_NULL}"(searchParam.name)
                        }
                    }
                }
            }
        }
        log.info("Found ${items?.size()} entities.")
        return items
    }

    private List<SearchParam> cleanSearchParameters(List<SearchParam> searchParams, Class clazz) {
        List<SearchParam> filteredSearchParams = []
        searchParams.each { SearchParam param ->
            def property = clazz.metaClass.hasProperty(clazz, param.name)
            if (property) {
                filteredSearchParams.add(param)
            }
        }
        return filteredSearchParams
    }

}
