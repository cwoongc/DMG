package cwoongc.dmg.task

import cwoongc.dmg.DomainModuleGenerator
import cwoongc.dmg.task.validator.GeneratingTaskValidator
import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.internal.tasks.options.Option
import org.gradle.api.tasks.TaskAction

@Slf4j
class MainClassGeneratingTask extends DefaultTask {

    @Option(option = "dir",
            description ="The name of the domain module package directory of 'main' source set to make. (Mandatory)" ,
            order = 1)
    String dir

    @Option(option = "prefix",
            description ="Files prefix to use. (Optional)",
            order = 2)
    String prefix

    @Option(option = 'dd',
            description = """(Duplicated Directory)
               Set type of way to use when the directory already exists.
               Available values are:
                   abort (default)
                   use""",
            order = 3)
    String dd

    @Option(option = 'df',
            description = """(Duplicated File)
               Set type of way to use when the class/resource file already exists.
               Available values are:
                   abort (default)
                   overwrite
                   skip""",
            order = 4)
    String df


    boolean usePrefix

    String uPrefix // first char uppercase
    String lPrefix // first char lowerCase
    String cPrefix // Capitalized prefix

    String domainModuleRootDir
    String javaDomainModuleRootDir
    String resourcesDomainModuleRootDir


    List<String> roleModuleNames
    List<String> resourcesRoleModuleNames



    @TaskAction
    def generate() {


        GeneratingTaskValidator.validate(project, this, dir, dd, df)

        domainModuleRootDir = project.DMG.domainModuleRootPackage.replace('.','/')
        javaDomainModuleRootDir = "${project.sourceSets.main.java.srcDirs[0]}/${domainModuleRootDir}"
        resourcesDomainModuleRootDir = "${project.sourceSets.main.resources.srcDirs[0]}/${domainModuleRootDir}"

        usePrefix = GeneratingTaskValidator.validatePrefix(prefix)

        generatePrefix()

        copyFiles()


    }

    def generatePrefix() {

        if(usePrefix) {
            println "\n*** '--prefix' option is supplied. User specified prefix will be used... ***"
            prefix = prefix.getAt(0).toUpperCase() + prefix.substring(1)
            uPrefix = prefix
            lPrefix = prefix.getAt(0).toLowerCase() + prefix.substring(1)
            cPrefix = uPrefix.toUpperCase()
        } else {
            println "\n*** '--prefix' option is not supplied. The prefix will be made by using '--dir' option value... ***"

            // dir명을 활용하여 prefix 제작
            prefix = dir;

            List<Integer> indexes = prefix.findIndexValues {
                it == '_'
            }

            indexes = indexes.reverse();

            indexes.each {
                if (it + 1 > prefix.length()) { // '_' 가 마지막이었다 > 삭제
                    prefix = prefix.substring(0, (int)it)

                } else { // '_' 는 삭제 다음은 대문자
                    prefix = prefix.substring(0, (int)it) + prefix.getAt((int)it + 1).toUpperCase() + prefix.substring((int)it + 2, prefix.length())
                }
            }

            prefix = prefix.getAt(0).toUpperCase() + prefix.substring(1)
            uPrefix = prefix
            lPrefix = prefix.getAt(0).toLowerCase() + prefix.substring(1)
            cPrefix = uPrefix.toUpperCase()

        }

    }


    def copyFiles() {

        println "\n*** Start class stub generation... ***\n"


        roleModuleNames = project.DMG.roleModuleNames

        roleModuleNames.each { name ->
            copyRoleModuleFile(name)
        }

        resourcesRoleModuleNames = project.DMG.resourcesRoleModuleNames

        resourcesRoleModuleNames.each { name ->
            copyResourcesRoleModuleFile(name)
        }
    }

    def copyRoleModuleFile(String roleModuleName) {

        def filenameNsuffix = [:]

        switch (roleModuleName) {
            case "code":
                filenameNsuffix.put("example_code", "ExampleCd.java")
                break

            case "controller":
                filenameNsuffix.put("controller", "Controller.java")
                break
            case "entity":
                filenameNsuffix.put("example_entity","ExampleEntity.java")
                break
            case "exception":
                if(roleModuleNames.contains("controller")) {
                    filenameNsuffix.put("controller_exception", "ControllerException.java")
                }
                if(roleModuleNames.contains("service")) {
                    filenameNsuffix.put("service_exception", "ServiceException.java")
                }
                if(roleModuleNames.contains("validate")) {
                    filenameNsuffix.put("controller_validate_exception", "ControllerValidateException.java")
                    filenameNsuffix.put("validate_exception", "ValidateException.java")
                }
                break
            case "mapper":
                filenameNsuffix.put("mapper_interface","Mapper.java")
                break
            case "message":
                if(roleModuleNames.contains("controller") && roleModuleNames.contains("exception")) {
                    filenameNsuffix.put("controller_exception_message", "ControllerExceptionMessage.java")
                }
                if(roleModuleNames.contains("service") && roleModuleNames.contains("exception")) {
                    filenameNsuffix.put("service_exception_message", "ServiceExceptionMessage.java")
                }
                if(roleModuleNames.contains("validate") && roleModuleNames.contains("exception")) {

                    filenameNsuffix.put("validate_exception_message", "ValidateExceptionMessage.java")
                    filenameNsuffix.put("controller_validate_exception_message", "ControllerValidateExceptionMessage.java")
                }
                break
            case "service":
                filenameNsuffix.put("service", "ServiceImpl.java")
                break
            case "validate":
                filenameNsuffix.put("validate", "Validate.java")
                filenameNsuffix.put("controller_validate", "ControllerValidate.java")
                break
            case "vo":
                filenameNsuffix.put("vo", "VO.java")
                break
            default:
                throwException("'${roleModuleName}' is a unauthorized role module name. Can't generate files.")
                break
        }

        doCopy(roleModuleName, filenameNsuffix, false)

    }

    def copyResourcesRoleModuleFile(String resourcesRoleModuleName) {

        def filenameNsuffix = [:]

        switch (resourcesRoleModuleName) {

            case "mapper":
                filenameNsuffix.put("mapper_xml","Mapper.xml")
                break
            default:
                throwException("'${resourcesRoleModuleName}' is a unauthorized resources role module name. Can't generate files.")
                break
        }

        doCopy(resourcesRoleModuleName, filenameNsuffix, true)

    }


    def doCopy(String roleModuleName, Map<String, String> filenameNsuffix, boolean isResource) {

        for(Map.Entry<String,String> fns : filenameNsuffix) {
            String filename = fns.getKey()
            String suffix = fns.getValue()

            File dest = null

            if(isResource) {
                dest = new File("${resourcesDomainModuleRootDir}/${dir}/${roleModuleName}/${lPrefix}${suffix}")
            } else {
                dest = new File("${javaDomainModuleRootDir}/${dir}/${roleModuleName}/${uPrefix}${suffix}")
            }

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
                        println "(Duplicated file - skip)  File '${dest.getAbsolutePath()}' already exists. It's been skipped to generate."
                        continue
                        break

                }
            }


            log.debug(DomainModuleGenerator.class.getClassLoader().getResource("cwoongc/dmg/template/main/${filename}"))


            BufferedReader source =  new BufferedReader(new InputStreamReader(DomainModuleGenerator.class.getClassLoader().getResourceAsStream("cwoongc/dmg/template/main/${filename}")))


            dest.withWriter { w ->
                source.eachLine { line ->
                    String l = line
                    l = l.replaceAll("@domainModuleFullName@","${project.DMG.domainModuleRootPackage}.${dir}");

                    l = l.replaceAll("@uPrefix@","${uPrefix}")

                    l = l.replaceAll("@lPrefix@","${lPrefix}")

                    l = l.replaceAll("@cPrefix@","${cPrefix}")

                    if(roleModuleName.equals("exception")) {

                        l = l.replaceAll("@exceptionImports@", "${project.DMG.exceptionImports}")

                        l = l.replaceAll("@baseException@", "${project.DMG.baseException}")

                    }

                    if(roleModuleName.equals("controller")) {
                        l = l.replaceAll("@snakedDir@", dir.replace('_','-'))
                    }


                    w << l + System.getProperty("line.separator")
                }

                println dest.getAbsolutePath() + " is generated!!!"
            }


        }

    }


    def throwException(String msg) {
        println msg
        throw new GradleException(msg)
    }


}
