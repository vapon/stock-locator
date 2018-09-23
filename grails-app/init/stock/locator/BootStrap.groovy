package stock.locator

import groovy.sql.Sql
import grails.util.Holders
import java.sql.Connection
import java.sql.Statement
import org.springframework.context.ApplicationContext

class BootStrap {

    def init = { servletContext ->
        def adminRole = new Role(authority: 'ROLE_ADMIN').save()
        def userRole =  new Role(authority: 'ROLE_USER').save()

        def testUserAdmin = new User(username: 'admin', password: 'admin').save()
        def testUser = new User(username: 'user', password: 'user').save()

        UserRole.create testUserAdmin, adminRole
        UserRole.create testUser, userRole

        UserRole.withSession {
            it.flush()
            it.clear()
        }

        assert User.count() == 2
        assert Role.count() == 2
        assert UserRole.count() == 2


        ApplicationContext applicationContext = Holders.applicationContext

        String dataSourceName = "dataSource"
        def dataSource = applicationContext.getBean(dataSourceName)
        def url = dataSource.targetDataSource.targetDataSource.poolProperties.url
        def dbName = url.split(":")[1]
        def database = dbName

        def sql = new Sql(dataSource)
        sql.withTransaction { Connection conn ->
            Statement statement = conn.createStatement()
            def script = "org/springframework/batch/core/schema-drop-${database}.sql"
            def text = applicationContext.classLoader.getResourceAsStream(script).text
            text.split(";").each { line ->
                if (line.trim()) {
                    statement.execute(line.trim())
                }
            }

            script = "org/springframework/batch/core/schema-${database}.sql"
            text = applicationContext.classLoader.getResourceAsStream(script).text
            text.split(";").each { line ->
                if (line.trim()) {
                    statement.execute(line.trim())
                }
            }
            statement.close()
            conn.commit()
        }
        sql.close()
    }
    def destroy = {
    }
}
