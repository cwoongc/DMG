package cwoongc.dmg.datasource

import cwoongc.dmg.DomainModuleGeneratorExtension.DataSource
import groovy.sql.Sql
import groovy.util.logging.Slf4j
import org.gradle.api.GradleException

@Slf4j
abstract class AbstractEntityGenerator implements EntityGenerator {

    protected abstract DataSource getDataSource()
    protected abstract String getDbName()
    protected abstract String getJdbcUrlContainsStr1()
    protected abstract String getJdbcUrlContainsStr2()

    @Override
    void generate(String tableName, String dir, String dd, String df) throws GradleException {

        def sql = connectDataSource()

        def metaData = getTableMetaData(tableName, sql)

        generateEntity(tableName, dir, dd, df, metaData)

    }

    protected def connectDataSource() {
        log.info("Load ${getDbName()} jdbc driver...")

        URLClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader()

        int urlIdx = contextClassLoader.getURLs().findIndexOf { URL url ->
            url.toString().contains(getJdbcUrlContainsStr1()) &&
                    url.toString().contains(getJdbcUrlContainsStr2())
        }

        URL jdbcUrl = contextClassLoader.getURLs()[urlIdx]

        log.debug("jdbcUrl: ${jdbcUrl}")

        URLClassLoader loader = Sql.classLoader
        loader.addURL(jdbcUrl)

        Class clazz = loader.loadClass(getDataSource().getDriverClassName())

        Sql.loadDriver(getDataSource().getDriverClassName())


        log.info("${getDbName()} jdbc driver initialization completed !!")

        log.info("Create ${getDbName()} jdbc connection...")

        def sql = Sql.newInstance(
                getDataSource().getUrl(),
                getDataSource().getUsername(),
                getDataSource().getPassword()
        )

        log.info "${getDbName()} jdbc connection creation completed !!"

        return sql

    }

    protected abstract def getTableMetaData(String tableName, Sql sql)

    protected abstract def generateEntity(String tableName, String dir, String dd, String df, def metaData)



}
