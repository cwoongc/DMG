package cwoongc.dmg

class DomainModuleGeneratorExtension {

    private String domainModuleRootPackage
    private List<String> roleModuleNames = ["code","entity", "exception", "mapper","message","service","validate","vo"]
    private List<String> resourcesRoleModuleNames = ["mapper"]
    private List<String> testRoleModuleNames = ["mapper","service"]
    private List<String> testResourcesRoleModuleNames = ["mapper"]



    public String getDomainModuleRootPackage() { return this.domainModuleRootPackage}
    public void setDomainModuleRootPackage(String domainModuleRootPackage) { this.domainModuleRootPackage = domainModuleRootPackage}

    public List<String> getRoleModuleNames() { return this.roleModuleNames }
    public List<String> getResourcesRoleModuleNames() { return this.resourcesRoleModuleNames }
    public List<String> getTestRoleModuleNames() { return  this.testRoleModuleNames }
    public List<String> getTestResourcesRoleModuleNames() { return this.testResourcesRoleModuleNames}
}