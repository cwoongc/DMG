package cwoongc.dmg.datasource

//@Grab('org.apache.commons:commons-dbcp2:2.1.1')
//@GrabConfig(systemClassLoader=true)
import groovy.sql.*

import cwoongc.dmg.DomainModuleGeneratorExtension.DataSource
import org.apache.commons.dbcp2.BasicDataSource
import org.gradle.api.GradleException



import java.sql.Driver
import java.sql.DriverManager

class OracleEntityGenerator implements EntityGenerator{

    private DataSource dataSource
    private String tableName

    OracleEntityGenerator(DataSource dataSource) {
        this.dataSource = dataSource
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

//        Class clazz = Thread.currentThread().getContextClassLoader().loadClass(dataSource.getDriverClassName())




//        URLClassLoader loader = Thread.currentThread().getContextClassLoader()

//        InputStream is = loader.getResourceAsStream('cwoongc/dmg/lib/ojdbc14-10.2.0.4.0.jar')




//        println url.toString()


//        Sql.classLoader.addURL(url)


        def ds = new BasicDataSource(
                driverClassName: dataSource.getDriverClassName(),
                url: dataSource.getUrl(),
                username: dataSource.getUsername(),
                password: dataSource.getPassword()
        )


//
//
//        Class clazz = Sql.classLoader.loadClass(dataSource.getDriverClassName())
//
//        Driver driver = clazz.newInstance()
//        println driver
//        DriverManager.registerDriver(driver)


        println "ORACLE jdbc driver initialization completed !!"

        println "Create ORACLE jdbc connection..."

        def sql = new Sql(ds)

//        def sql = Sql.newInstance(
//            dataSource.getUrl(),
//            dataSource.getUsername(),
//            dataSource.getPassword()
//        )

//        Class.forName(dataSource.getDriverClassName(),true, Sql.classLoader )
//        Class.forName(dataSource.getDriverClassName())



//        Connection conn = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword())



        println "ORACLE jdbc connection creation completed !!"


        return sql
//        return conn
    }


}
