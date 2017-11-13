package cwoongc.dmg.task

import org.gradle.api.DefaultTask
import org.gradle.api.internal.tasks.options.Option
import org.gradle.api.tasks.TaskAction

class AllClassGeneratingTask extends DefaultTask {

    @Option(option = "dir",
            description ="The name of the domain module package directory of 'main' and 'test' source set to make. (Mandatory)" ,
            order = 1)
    String dir

    @Option(option = "prefix",
            description ="Files prefix to use. If provided, default role classes and resource files will be automatically generated. (Optional)",
            order = 2)
    String prefix


    @TaskAction
    def generate() {

    }




}
