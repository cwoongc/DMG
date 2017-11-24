package cwoongc.dmg.datasource

import cwoongc.dmg.DomainModuleGeneratorExtension.DataSource
import cwoongc.dmg.task.message.GeneratingTaskMessage
import cwoongc.dmg.task.validator.GeneratingTaskValidator
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.util.logging.Slf4j
import org.gradle.api.GradleException
import org.gradle.api.Project

@Slf4j
class OracleEntityGenerator extends AbstractEntityGenerator{

    private static final String DB_NAME = "ORACLE"
    private static final String JDBC_URL_CONTAINS_STR_1 = "oracle"
    private static final String JDBC_URL_CONTAINS_STR_2 = "ojdbc"
    private DataSource dataSource
    private Project project
    private String domainModuleRootDir
    private String javaDomainModuleRootDir



    OracleEntityGenerator(DataSource dataSource, Project project) {
        this.dataSource = dataSource
        this.project = project
    }

    @Override
    protected DataSource getDataSource() {
        return dataSource
    }

    @Override
    protected String getDbName() {
        return DB_NAME
    }

    @Override
    protected String getJdbcUrlContainsStr1() {
        return JDBC_URL_CONTAINS_STR_1
    }

    @Override
    protected String getJdbcUrlContainsStr2() {
        return JDBC_URL_CONTAINS_STR_2
    }

    @Override
    protected getTableMetaData(String tableName, Sql sql) {

        tableName = tableName.toUpperCase().trim()


        List metaData = new ArrayList()

        List<GroovyRowResult> tableComments = null
        List<GroovyRowResult> columnsInfo = null


        try {

            log.info("Fetch '${tableName}' table's meta data.")

            tableComments = sql.rows("""SELECT A.COMMENTS
                                            FROM USER_TAB_COMMENTS A
                                            WHERE 1=1
                                                AND A.TABLE_NAME = ${tableName}""")



            if(tableComments == null || tableComments.size() == 0)
                throw GradleException("There is no table you specified. [ ${tableName} ]")



            columnsInfo = sql.rows(
                    """SELECT A.*, B.COMMENTS
                        FROM USER_TAB_COLUMNS A, USER_COL_COMMENTS B
                        WHERE 1=1
                            AND A.TABLE_NAME = B.TABLE_NAME (+)
                            AND A.COLUMN_NAME = B.COLUMN_NAME (+)
                            AND A.TABLE_NAME = ${tableName}
                            ORDER BY A.COLUMN_ID"""
            )



            if(columnsInfo == null || columnsInfo.size() == 0)
                throw GradleException("There is no table you specified. [ ${tableName} ]")

            log.info("'${tableName}' table's meta data fetching complete !!")

            metaData << tableComments << columnsInfo


        } finally {
            sql.close()
        }

        return metaData
    }


    @Override
    protected generateEntity(String tableName, String dir, String dd, String df, Object metaData) {

        tableName = tableName.toUpperCase().trim()

        String tableComments = ((List<GroovyRowResult>)metaData[0])[0].COMMENTS

        List<GroovyRowResult> rows = (List<GroovyRowResult>)metaData[1]

        domainModuleRootDir = project.DMG.domainModuleRootPackage.replace('.','/')
        javaDomainModuleRootDir = "${project.sourceSets.main.java.srcDirs[0]}/${domainModuleRootDir}"
        String javaDomainModuleDir = "${javaDomainModuleRootDir}/${dir}"

        switch(dd) {
            case "abort":
                GeneratingTaskValidator.validateFileNotExists(javaDomainModuleDir)
                break
            case "use":
                GeneratingTaskValidator.validateIsUsableDirectory(javaDomainModuleDir)
                break
        }


        mkDomainModuleDir(javaDomainModuleDir)

        String camelledTableName = convertUcamelledString(tableName)

        mkEntityRoleModuledir(javaDomainModuleDir, dd)


        String columns = generateColumnsString(rows)

        copyEntity(dir, df, tableName, camelledTableName, tableComments, columns)


    }




    private mkDomainModuleDir(String domainModuleDir) {

        File dmDir = project.file(domainModuleDir)

        if(dmDir.exists()) {
            println String.format(GeneratingTaskMessage.DIR_WILL_BE_USED, dmDir.getCanonicalPath())
        } else {
            if(dmDir.mkdirs()) {
                println String.format(GeneratingTaskMessage.DIR_IS_GENERATED, dmDir.getCanonicalPath())
            }
        }
    }

    private mkEntityRoleModuledir(String domainModuleDir, String dd) {

        File dmDir = project.file(domainModuleDir)

        File roleModuleDir = project.file("${dmDir.getCanonicalPath()}/entity")

        if(roleModuleDir.exists()) {
            if(dd.equals("use")) {
                GeneratingTaskValidator.validateIsUsableDirectory(roleModuleDir)

                println String.format(GeneratingTaskMessage.DIR_WILL_BE_USED, roleModuleDir.getCanonicalPath())
            }
        } else {
            if(roleModuleDir.mkdirs()) {
                println String.format(GeneratingTaskMessage.DIR_IS_GENERATED, roleModuleDir.getCanonicalPath())
            }
        }

    }


    private String convertUcamelledString(String tableName) {

        tableName = tableName.toLowerCase().trim()

        StringBuilder uCamelledString = new StringBuilder()

        boolean capitalizeNext = false

        for(int i=0; i<tableName.length();i++) {
            char c = tableName[i]

            if(i==0) uCamelledString.append(c.toUpperCase())
            else if(c == '_') capitalizeNext = true
            else {
                switch (capitalizeNext) {
                    case true: uCamelledString.append(c.toUpperCase())
                        capitalizeNext = false
                        break
                    case false: uCamelledString.append(c)
                        break
                }
            }
        }

        return uCamelledString.toString().trim()
    }

    private String convertLcamelledString(String tableName) {

        tableName = tableName.toLowerCase().trim()

        StringBuilder uCamelledString = new StringBuilder()

        boolean capitalizeNext = false

        for(int i=0; i<tableName.length();i++) {
            char c = tableName[i]

            if(c == '_') capitalizeNext = true
            else {
                switch (capitalizeNext) {
                    case true: uCamelledString.append(c.toUpperCase())
                        capitalizeNext = false
                        break
                    case false: uCamelledString.append(c)
                        break
                }
            }
        }

        return uCamelledString.toString().trim()
    }



    private generateColumnsString(List<GroovyRowResult> rows) {

        String columns = ""
        String n = System.getProperty("line.separator")

        rows.each { r ->

            columns += "\t/**${n}\t* ${r.COMMENTS}${n}\t*/${n}"
            columns += "\tprivate ${convertType(r.DATA_TYPE)} ${convertLcamelledString(r.COLUMN_NAME)};${n}${n}"

        }
        return columns
    }

    /**
     * FixMe
     * @param dbType
     * @param length
     * @param precision
     * @param scale
     * @return
     */
    private String convertType(String dbType, int length = 0, int precision = 0, int scale = 0) {

        String javaType = null

        switch (dbType) {
            case "VARCHAR2": javaType = "String"
                break
            case "NVARCHAR2": javaType = "String"
                break
            case "CHAR": javaType = "String"
                break
            case "CLOB": javaType = "String"
                break
            case "NCLOB": javaType = "String"
                break
            case "DATE": javaType = "String"
                break
            case "NUMBER": javaType = "long"
                break
            default: javaType = "String"
                break;
        }

        return javaType
    }




    private copyEntity(String dir, String df, String tableName, String camelledTableName, String tableComments, String columns) {

        File dest = new File("${javaDomainModuleRootDir}/${dir}/entity/${camelledTableName}.java")

        if(dest.exists()) {
            switch (df) {
                case "abort":
                    String msg = "(Duplicated file - abort)  File '${dest.getAbsolutePath()}' already exists."
                    throwException(msg)
                    break
                case "overwrite":
                    println "(Duplicated file - overwrite) File '${dest.getAbsolutePath()}' already exists. The file will be replaced with new file."
                    break
                case "skip":
                    String msg = "(Duplicated file - skip)  File '${dest.getAbsolutePath()}' already exists. It's been skipped to generate."
                    throwException(msg)
                    break

            }
        }


        BufferedReader source =  new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("cwoongc/dmg/template/main/entity")))


        dest.withWriter { w ->

            source.eachLine { line ->
                String l = line

                l = l.replaceAll("@domainModuleFullName@","${project.DMG.domainModuleRootPackage}.${dir}")

                l = l.replaceAll("@tableName@","${tableName}")

                l = l.replaceAll("@tableComments@","${tableComments}")

                l = l.replaceAll("@camelledTableName@","${camelledTableName}")

                l = l.replaceAll("@columns@","${columns}")

                w << l + System.getProperty("line.separator")
            }

            println "File '${dest.getAbsolutePath()}' is generated!!!"
        }

    }


    def throwException(String msg) {
        println msg
        throw new GradleException(msg)
    }


}
