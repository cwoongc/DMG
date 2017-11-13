package cwoongc.dmg

class DomainModuleGeneratorExtension {

    private String domainModuleRootPackage
    private List<String> roleModuleNames = ["code","entity", "exception", "mapper","message","service","validate", "vo"]
    private List<String> resourcesRoleModuleNames = ["mapper"]
    private List<String> testRoleModuleNames = ["mapper","service"]
    private List<String> testResourcesRoleModuleNames = ["mapper"]
    private String baseException
    private String exceptionImports




    public String getDomainModuleRootPackage() { return this.domainModuleRootPackage}
    public void setDomainModuleRootPackage(String domainModuleRootPackage) { this.domainModuleRootPackage = domainModuleRootPackage}

    public List<String> getRoleModuleNames() { return this.roleModuleNames }
    public List<String> getResourcesRoleModuleNames() { return this.resourcesRoleModuleNames }
    public List<String> getTestRoleModuleNames() { return  this.testRoleModuleNames }
    public List<String> getTestResourcesRoleModuleNames() { return this.testResourcesRoleModuleNames}

    public String getBaseException() { return baseException}
    public void setBaseException(String baseException) {this.baseException = baseException}

    public String getExceptionImports() { return exceptionImports }
    public void setExceptionImports(String exceptionImports) { this.exceptionImports = exceptionImports}

}