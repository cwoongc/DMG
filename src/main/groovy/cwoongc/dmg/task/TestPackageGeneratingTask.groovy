package cwoongc.dmg.task

import cwoongc.dmg.task.message.GeneratingTaskMessage
import cwoongc.dmg.task.validator.GeneratingTaskValidator
import org.gradle.api.DefaultTask
import org.gradle.api.internal.tasks.options.Option
import org.gradle.api.tasks.TaskAction

class TestPackageGeneratingTask extends DefaultTask{

    @Option(option = "dir",
            description ="The name of the domain module package directory of 'test' source set to make. (Mandatory)" ,
            order = 1)
    String dir

    @Option(option = 'dd',
            description = """(Duplicated Directory)
               Set type of way to use when the directory already exists.
               Available values are:
                   abort (default)
                   use""",
            order = 2)
    String dd

    String domainModuleRootDir
    String javaDomainModuleRootDir
    String resourcesDomainModuleRootDir
    File javaDomainModuleDir
    File resourcesDomainModuleDir

    @TaskAction
    def generate() {

        //validate project extension, task options
        GeneratingTaskValidator.validate(project, this, dir, dd)

        domainModuleRootDir = project.DMG.domainModuleRootPackage.replace('.','/')
        javaDomainModuleRootDir = "${project.sourceSets.test.java.srcDirs[0]}/${domainModuleRootDir}"
        resourcesDomainModuleRootDir = "${project.sourceSets.test.resources.srcDirs[0]}/${domainModuleRootDir}"

        javaDomainModuleDir = project.file("${javaDomainModuleRootDir}/${dir}")
        resourcesDomainModuleDir = project.file("${resourcesDomainModuleRootDir}/${dir}")


        switch(dd) {
            case "abort":
                GeneratingTaskValidator.validateFileNotExists(javaDomainModuleDir)
                GeneratingTaskValidator.validateFileNotExists(resourcesDomainModuleDir)
                break
            case "use":
                GeneratingTaskValidator.validateIsUsableDirectory(javaDomainModuleDir)
                GeneratingTaskValidator.validateIsUsableDirectory(resourcesDomainModuleDir)
                break
        }

        mkDomainModuleDir(javaDomainModuleDir)
        mkRoleModuledirs(javaDomainModuleDir, project.DMG.testRoleModuleNames)

        mkDomainModuleDir(resourcesDomainModuleDir)
        mkRoleModuledirs(resourcesDomainModuleDir, project.DMG.testResourcesRoleModuleNames)
    }

    def mkDomainModuleDir(File domainModuleDir) {

        if(domainModuleDir.exists()) {
            println String.format(GeneratingTaskMessage.DIR_WILL_BE_USED, domainModuleDir.getCanonicalPath())
        } else {
            if(domainModuleDir.mkdirs()) {
                println String.format(GeneratingTaskMessage.DIR_IS_GENERATED, domainModuleDir.getCanonicalPath())
            }
        }
    }

    def mkRoleModuledirs(File domainModuleDir, List<String> roleModuleNames) {

        roleModuleNames.each { name ->
            File roleModuleDir = project.file("${domainModuleDir.getCanonicalPath()}/${name}")

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

    }


}
