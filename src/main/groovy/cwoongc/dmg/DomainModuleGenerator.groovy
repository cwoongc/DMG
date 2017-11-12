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
                if(project.tasks.genAllDir.dir != null) {
                    dir = project.tasks.genAllDir.dir
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
                if(project.tasks.genAllDir.dir != null) {
                    dir = project.tasks.genAllDir.dir
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

    }



}