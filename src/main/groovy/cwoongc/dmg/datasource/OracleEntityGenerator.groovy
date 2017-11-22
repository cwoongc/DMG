package cwoongc.dmg.datasource

//@Grab('org.apache.commons:commons-dbcp2:2.1.1')
//@GrabConfig(systemClassLoader=true)
import groovy.sql.*

import cwoongc.dmg.DomainModuleGeneratorExtension.DataSource
//import org.apache.commons.dbcp2.BasicDataSource
import org.gradle.api.GradleException



import java.sql.Driver
import java.sql.DriverManager

import org.gradle.api.Project

class OracleEntityGenerator implements EntityGenerator{

    private DataSource dataSource
    private String tableName
    private Project project

    OracleEntityGenerator(DataSource dataSource, Project project) {
        this.dataSource = dataSource
        this.project = project
    }

    @Override
    void generate(String tableName) throws GradleException {

        this.tableName = tableName

        def sql = connectDataSource()



        sql.eachRow('SELECT 1  FROM DUAL') { row ->
            def num = row[0]
            println "num: ${num}"
        }

        sql.close()



    }


    def connectDataSource() {
        println "Initialize ORACLE jdbc driver..."

        println "dependencies"


        URLClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader()

        int urlIdx = contextClassLoader.getURLs().findIndexOf { URL url ->
            url.toString().contains("oracle") && url.toString().contains("ojdbc")
        }

        URL jdbcUrl = contextClassLoader.getURLs()[urlIdx]

        println "jdbcUrl: ${jdbcUrl}"


        URLClassLoader loader = Sql.classLoader
        loader.addURL(jdbcUrl)

        Class clazz = loader.loadClass(dataSource.getDriverClassName())

        Sql.loadDriver(dataSource.getDriverClassName())


        println "ORACLE jdbc driver initialization completed !!"

        println "Create ORACLE jdbc connection..."

        def sql = Sql.newInstance(
                dataSource.getUrl(),
                dataSource.getUsername(),
                dataSource.getPassword()
        )

        println "ORACLE jdbc connection creation completed !!"

        return sql

    }


}
