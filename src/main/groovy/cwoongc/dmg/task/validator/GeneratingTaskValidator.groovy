package cwoongc.dmg.task.validator

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import cwoongc.dmg.DomainModuleGeneratorExtension

import java.security.GeneralSecurityException

class GeneratingTaskValidator {
    static void validate(Project project, DefaultTask task, String dir, String dd) {

        validateDMGScriptBlock(project.extensions.findByName("DMG"))

        DomainModuleGeneratorExtension dmgExt = project.extensions.getByName("DMG")

        validateDomainModuleRootPackage(dmgExt)

        validateDir(dir)

        validateDd(task, dd, dmgExt)

    }

    private static void validateDMGScriptBlock(DomainModuleGeneratorExtension dmg) {

        if(dmg == null) {
            String msg = "'DMG' script block must be specified in the build script to configure DMG plugin."
            throw new GradleException(msg)
        }
    }


    private static void validateDomainModuleRootPackage(DomainModuleGeneratorExtension dmg) {

        String domainModuleRootPackage = dmg.getDomainModuleRootPackage()

        if(domainModuleRootPackage == null || domainModuleRootPackage.isEmpty()) {
            String msg = "'domainModuleRootPackage' property must be specified in the 'DMG' script block."
            throw new GradleException(msg)
        }


        String pattern = '^[a-z][a-z0-9_]*([\\.][a-z][a-z0-9_]*)*$'
        if(!domainModuleRootPackage.matches(pattern)) {
            String msg = "'domainModuleRootPackage' property must form the java package format. The specified string is invalid format."
            throw new GradleException(msg)
        }
    }


    private static void validateDir(String dir) {
        if(dir == null || dir.isEmpty()) {
            String msg = "'--dir' option must be provided. It's mandatory option."
            throw new GradleException(msg)
        }

        String pattern = '^[a-z][a-z0-9_]*$'
        if(!dir.matches(pattern)) {
            String msg = "'--dir' option's value must form a java package format."
            throw new GradleException(msg)
        }

    }


    private static def validateDd(DefaultTask task, String dd, DomainModuleGeneratorExtension dmgExt) {
        String msg = null

        if(dd == null || dd.isEmpty()) {
            String dmgDd = dmgExt.getDd()

            if(dmgDd == null || !(dmgDd.equals("abort") || dmgDd.equals("use"))) {
                msg = "Specified 'dd' property in the DMG script block has invalid value. 'dd' must be one of the two. [ abort | use ]"
                throw new GradleException(msg)
            } else {
                task.dd = dmgDd
            }
        } else if(dd.equals("abort") || dd.equals("use")) {
           return
        } else {
            msg = "Specified '--dd' option value is invalid. It must be one of the two. [ abort | use ]"
            throw new GradleException(msg)
        }

    }



    static void validateFileNotExists(File file) {

        if(file.exists()) {
            String msg = null
            if(file.isDirectory()) {
                msg = "(Duplicated directory - abort)  Directory '${file.getCanonicalPath()}' already exists."
            } else {
                msg = "File '${file.getCanonicalPath()}' already exists."
            }
            throw new GradleException(msg)
        }
    }

    static void validateIsUsableDirectory(File file) {

        if(file.exists() && !file.isDirectory()) {
            String msg = "File '${file.getCanonicalPath()}' already exists and it's not a directory. It's not usable."
            throw new GradleException(msg)
        }

    }




    static boolean validatePrefix(String prefix) {

        boolean usePrefix = false;

        usePrefix = (!(prefix == null) && !(prefix.isEmpty()))

        if(usePrefix) {

            if(prefix.length() < 2)
                throw new GradleException("the prefix's length is too short. It must be more than 2.")
        }

        return usePrefix;
    }


}