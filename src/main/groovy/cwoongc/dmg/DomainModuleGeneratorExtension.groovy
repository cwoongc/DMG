package cwoongc.dmg

import org.gradle.api.GradleException
import org.gradle.api.Project

class DomainModuleGeneratorExtension {

    class DataSource {
        private String id
        private String url
        private String username
        private String password
        private String driverClassName

        String id(String id) { this.id = id }

        String url(String url) { this.url = url }

        String username(String username) { this.username = username }

        String password(String password) { this.password = password }

        String driverClassName(String driverClassName) { this.driverClassName = driverClassName }

        String getId() { id }

        String getUrl() { url }

        String getUsername() { username }

        String getPassword() { password }

        String getDriverClassName() { driverClassName }
    }

    DomainModuleGeneratorExtension(Project project) {
        this.project = project
    }

    DomainModuleGeneratorExtension() {}



    private Project project
    private String domainModuleRootPackage
    private String dd = "abort" //abort, use
    private String df = "abort"  //abort, overwrite, skip

    private List<String> roleModuleNames = ["code", "controller", "entity", "exception", "mapper","message","service","validate", "vo"]
    private List<String> resourcesRoleModuleNames = ["mapper"]
    private List<String> testRoleModuleNames = ["mapper","service","controller"]
    private List<String> testResourcesRoleModuleNames = ["mapper"]

    private String baseException
    private String exceptionImports
    private Map<String,DataSource> dataSources = new LinkedHashMap<>()



    String getDomainModuleRootPackage() { return this.domainModuleRootPackage}
    void setDomainModuleRootPackage(String domainModuleRootPackage) { this.domainModuleRootPackage = domainModuleRootPackage}

    List<String> getRoleModuleNames() { return this.roleModuleNames }
    List<String> getResourcesRoleModuleNames() { return this.resourcesRoleModuleNames }
    List<String> getTestRoleModuleNames() { return  this.testRoleModuleNames }
    List<String> getTestResourcesRoleModuleNames() { return this.testResourcesRoleModuleNames}

    String getBaseException() { return baseException}
    void setBaseException(String baseException) {this.baseException = baseException}

    String getExceptionImports() { return exceptionImports }
    void setExceptionImports(String exceptionImports) { this.exceptionImports = exceptionImports}

    String getDd() {return this.dd}
    void setDd(String dd) { this.dd = dd}

    String getDf() {return this.df}
    void setDf(String df) {this.df = df }

    DataSource dataSource(Closure cl) {
        DataSource dataSource = project.configure(new DataSource(), cl)

        DomainModuleGeneratorExtensionValidator.validateDataSource(dataSource)

        this.dataSources.put(dataSource.getId(), dataSource)

        return dataSource

    }

    Map<String,DataSource> getDataSources() { this.dataSources }



    class DomainModuleGeneratorExtensionValidator {
        static void validateDataSource(DataSource dataSource) {

            Collection<String> msgs = new ArrayList<>()

            msgs.add(validateEmpty("id", dataSource.getId()))
            msgs.add(validateEmpty("url", dataSource.getUrl()))
            msgs.add(validateEmpty("username", dataSource.getUsername()))
            msgs.add(validateEmpty("password", dataSource.getPassword()))
            msgs.add(validateEmpty("driverClassName", dataSource.getDriverClassName()))

            String msg = null
            msgs.each {
                if(it != null) {
                    if(msg == null) msg = it
                    else msg += ", " + it
                }
            }

            if(msg != null)
                throw new GradleException(String.format("Following properties values must be specified in the DMG.dataSource script block. [ %s ]", msg))

        }

        private static String validateEmpty(String name, String value) {
            String ret = null

            if(value == null || value.isEmpty()) {
                ret = name
            }

            return ret
        }
    }




}