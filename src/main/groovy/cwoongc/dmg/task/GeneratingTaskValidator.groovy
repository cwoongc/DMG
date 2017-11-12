package cwoongc.dmg.task

import org.gradle.api.GradleException
import org.gradle.api.Project
import cwoongc.dmg.DomainModuleGeneratorExtension

class GeneratingTaskValidator {
    static void validate(Project project, String dir) {

        validateDomainModuleRootPackage(project.extensions.getByName("DMG"))

        validateDir(dir)

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

    static void validateFileNotExists(File file) {

        if(file.exists()) {
            String msg = null
            if(file.isDirectory()) {
                msg = "Directory '${file.getCanonicalPath()}' already exists."
            } else {
                msg = "File '${file.getCanonicalPath()}' already exists."
            }
            throw new GradleException(msg)
        }

    }


}