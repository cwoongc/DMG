package cwoongc.dmg.task

import cwoongc.dmg.DomainModuleGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.internal.tasks.options.Option
import org.gradle.api.tasks.TaskAction

class TestClassGeneratingTask extends DefaultTask{

    @Option(option = "dir",
            description ="The name of the domain module package directory of 'test' source set to make. (Mandatory)" ,
            order = 1)
    String dir

    @Option(option = "prefix",
            description ="Files prefix to use. If provided, default role classes and resource files will be automatically generated. (Optional)",
            order = 2)
    String prefix

    boolean usePrefix

    String uPrefix // first char uppercase
    String lPrefix // first char lowerCase
    String cPrefix // Capitalized prefix

    String domainModuleRootDir
    String javaDomainModuleRootDir
    String resourcesDomainModuleRootDir


    List<String> testRoleModuleNames
    List<String> testResourcesRoleModuleNames





    @TaskAction
    def generate() {


        domainModuleRootDir = project.DMG.domainModuleRootPackage.replace('.','/')
        javaDomainModuleRootDir = "${project.sourceSets.test.java.srcDirs[0]}/${domainModuleRootDir}"
        resourcesDomainModuleRootDir = "${project.sourceSets.test.resources.srcDirs[0]}/${domainModuleRootDir}"

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


        testRoleModuleNames = project.DMG.testRoleModuleNames

        testRoleModuleNames.each { name ->
            copyTestRoleModuleFile(name)
        }

        testResourcesRoleModuleNames = project.DMG.testResourcesRoleModuleNames

        testResourcesRoleModuleNames.each { name ->
            copyTestResourcesRoleModuleFile(name)
        }
    }

    def copyTestRoleModuleFile(String testRoleModuleName) {

        def filenameNsuffix = [:]

        switch (testRoleModuleName) {
            case "mapper":
                filenameNsuffix.put("test_mapper_interface","ServiceTestMapper.java")
                break
            case "service":
                filenameNsuffix.put("service_test", "ServiceTest.java")
                break
            default:
                throwException("'${testRoleModuleName}' is a unauthorized test role module name. Can't generate files.")
                break
        }

        doCopy(testRoleModuleName, filenameNsuffix, false)

    }

    def copyTestResourcesRoleModuleFile(String testResourcesRoleModuleName) {

        def filenameNsuffix = [:]

        switch (testResourcesRoleModuleName) {
            case "mapper":
                filenameNsuffix.put("test_mapper_xml","ServiceTestMapper.xml")
                break
            default:
                throwException("'${testResourcesRoleModuleName}' is a unauthorized test resources role module name. Can't generate files.")
                break
        }

        doCopy(testResourcesRoleModuleName, filenameNsuffix, true)

    }



    def doCopy(String roleModuleName, Map<String, String> filenameNsuffix, boolean isResource) {

        filenameNsuffix.each { filename, suffix ->

            BufferedReader source =  new BufferedReader(new InputStreamReader(DomainModuleGenerator.class.getClassLoader().getResourceAsStream("cwoongc/dmg/template/main/${filename}")))
            File dest = null

            if(isResource) {
                dest = new File("${resourcesDomainModuleRootDir}/${dir}/${roleModuleName}/${lPrefix}${suffix}")
            } else {
                dest = new File("${javaDomainModuleRootDir}/${dir}/${roleModuleName}/${uPrefix}${suffix}")
            }

            dest.withWriter { w ->
                source.eachLine { line ->
                    String l = line
                    l = l.replaceAll("@domainModuleFullName@","${project.DMG.domainModuleRootPackage}.${dir}");

                    l = l.replaceAll("@uPrefix@","${uPrefix}")

                    l = l.replaceAll("@lPrefix@","${lPrefix}")

                    l = l.replaceAll("@cPrefix@","${cPrefix}")

                    w << l + System.getProperty("line.separator")
                }
            }

            println dest.getAbsolutePath() + " is generated!!!"
        }

    }


    def throwException(String msg) {
        println msg
        throw new GradleException(msg)
    }



}
