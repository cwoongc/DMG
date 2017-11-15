// genModule
// genAllDir --dir=
// genAllClass --dir= --prefix
// genClass --dir= --prefix
// genDir --dir
// genTestDir --dir 
// genTestClass --dir --prefix
// genEntity --table --module
//
package cwoongc.dmg.task
import org.gradle.api.DefaultTask
import org.gradle.api.PathValidation
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.internal.tasks.options.Option

class MainPackageGeneratingTask extends DefaultTask {


    @Option(option = "dir",
    description ="The name of the domain module package directory of 'test' source set to make. (Mandatory)" ,
    order = 1)
    String dir

    String domainModuleRootDir
    String javaDomainModuleRootDir
    String resourcesDomainModuleRootDir
    File javaDomainModuleDir
    File resourcesDomainModuleDir





    @TaskAction
    def generate() {



        GeneratingTaskValidator.validate(project, dir)

        domainModuleRootDir = project.DMG.domainModuleRootPackage.replace('.','/')
        javaDomainModuleRootDir = "${project.sourceSets.main.java.srcDirs[0]}/${domainModuleRootDir}"
        resourcesDomainModuleRootDir = "${project.sourceSets.main.resources.srcDirs[0]}/${domainModuleRootDir}"

        javaDomainModuleDir = project.file("${javaDomainModuleRootDir}/${dir}")
        GeneratingTaskValidator.validateFileNotExists(javaDomainModuleDir)

        resourcesDomainModuleDir = project.file("${resourcesDomainModuleRootDir}/${dir}")
        GeneratingTaskValidator.validateFileNotExists(resourcesDomainModuleDir)


        if(javaDomainModuleDir.mkdirs()) {
            println String.format(GeneratingTaskMessage.DIR_IS_GENERATED, javaDomainModuleDir.getCanonicalPath())


            mkDomainModuleDir(javaDomainModuleDir)
            mkRoleModuledirs(javaDomainModuleDir, project.DMG.roleModuleNames)

            mkDomainModuleDir(resourcesDomainModuleDir)
            mkRoleModuledirs(resourcesDomainModuleDir, project.DMG.resourcesRoleModuleNames)

        }


    }

    def mkDomainModuleDir(File domainModuleDir) {
        if(domainModuleDir.mkdirs()) {
            println String.format(GeneratingTaskMessage.DIR_IS_GENERATED, domainModuleDir.getCanonicalPath())
        }
    }

    def mkRoleModuledirs(File domainModuleDir, List<String> roleModuleNames) {

        roleModuleNames.each { name ->
            File roleModuleDir = project.file("${domainModuleDir.getCanonicalPath()}/${name}")
            if(roleModuleDir.mkdirs()) {
                println String.format(GeneratingTaskMessage.DIR_IS_GENERATED, roleModuleDir.getCanonicalPath())
            }

        }

    }
}