package cwoongc.dmg.task

import org.gradle.api.DefaultTask
import org.gradle.api.internal.tasks.options.Option
import org.gradle.api.tasks.TaskAction

class AllPackageGeneratingTask extends DefaultTask {



    @Option(option = "dir",
            description ="The name of the domain module package directory of 'main' and 'test' source sets to make. (Mandatory)" ,
            order = 1)
    String dir

    @TaskAction
    def generate() {
    }
}
