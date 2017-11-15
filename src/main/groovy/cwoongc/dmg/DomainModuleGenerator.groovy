package cwoongc.dmg
import org.gradle.api.Project
import org.gradle.api.Plugin
import cwoongc.dmg.task.*


class DomainModuleGenerator implements Plugin<Project> {

    void apply(Project project) {

        PluginApplyValidator.validate(project);

        project.extensions.create("DMG", DomainModuleGeneratorExtension)

        registerTasks(project)

    }


    private void registerTasks(Project project) {

        Map<String, ?> option = [
                "type":MainPackageGeneratingTask
                ,"group":"DMG(Domain Module Generator)"
                ,"description":"Generates main domain module packages."
        ]

        project.task(option, "genDir") {

            doFirst {
                if(project.tasks.genAllClass.dir != null) {
                    dir = project.tasks.genAllClass.dir
                }
                else if(project.tasks.genClass.dir != null) {
                    dir = project.tasks.genClass.dir
                }
                else if(project.tasks.genAllDir.dir != null) {
                    dir = project.tasks.genAllDir.dir
                }

                if(project.tasks.genAllClass.dd != null) {
                    dd = project.tasks.genAllClass.dd
                }
                else if(project.tasks.genClass.dd != null) {
                    dd = project.tasks.genClass.dd
                }
                else if(project.tasks.genAllDir.dd != null) {
                    dd = project.tasks.genAllDir.dd
                }

            }
        }

        option = [
                "type":TestPackageGeneratingTask
                ,"group":"DMG(Domain Module Generator)"
                ,"description":"Generates test domain module packages."
        ]

        project.task(option, "genTestDir") {
            doFirst {
                if(project.tasks.genAllClass.dir != null) {
                    dir = project.tasks.genAllClass.dir
                }
                else if(project.tasks.genTestClass.dir != null) {
                    dir = project.tasks.genTestClass.dir
                }
                else if(project.tasks.genAllDir.dir != null) {
                    dir = project.tasks.genAllDir.dir
                }

                if(project.tasks.genAllClass.dd != null) {
                    dd = project.tasks.genAllClass.dd
                }
                else if(project.tasks.genTestClass.dd != null) {
                    dd = project.tasks.genTestClass.dd
                }
                else if(project.tasks.genAllDir.dd != null) {
                    dd = project.tasks.genAllDir.dd
                }
            }
        }

        option = [
                "type":AllPackageGeneratingTask
                ,"group":"DMG(Domain Module Generator)"
                ,"description":"Generates main and test domain module packages."
                ,"dependsOn":["genDir", "genTestDir"]
        ]

        project.task(option, "genAllDir")


        option = [
                "type":MainClassGeneratingTask
                ,"group":"DMG(Domain Module Generator)"
                ,"description":"Generates main role module's default source and resource files."
                ,"dependsOn":["genDir"]
        ]

        project.task(option, "genClass") {
            doFirst {
                if(project.tasks.genAllClass.dir != null) {
                    dir = project.tasks.genAllClass.dir
                }
                if(project.tasks.genAllClass.prefix != null) {
                    prefix = project.tasks.genAllClass.prefix
                }
                if(project.tasks.genAllClass.dd != null) {
                    dd = project.tasks.genAllClass.dd
                }
            }
        }

        option = [
                "type":TestClassGeneratingTask
                ,"group":"DMG(Domain Module Generator)"
                ,"description":"Generates test role module's default source and resource files."
                ,"dependsOn":["genTestDir"]
        ]

        project.task(option, "genTestClass") {
            doFirst {
                if(project.tasks.genAllClass.dir != null) {
                    dir = project.tasks.genAllClass.dir
                }
                if(project.tasks.genAllClass.prefix != null) {
                    prefix = project.tasks.genAllClass.prefix
                }
                if(project.tasks.genAllClass.dd != null) {
                    dd = project.tasks.genAllClass.dd
                }
            }
        }

        option = [
                "type":AllClassGeneratingTask
                ,"group":"DMG(Domain Module Generator)"
                ,"description":"Generates main and test role module's default source and resource files."
                ,"dependsOn":["genClass", "genTestClass"]
        ]

        project.task(option, "genAllClass")





    }



}