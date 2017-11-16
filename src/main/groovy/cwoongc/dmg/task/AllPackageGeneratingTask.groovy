package cwoongc.dmg.task

import org.gradle.api.DefaultTask
import org.gradle.api.internal.tasks.options.Option
import org.gradle.api.tasks.TaskAction

class AllPackageGeneratingTask extends DefaultTask {



    @Option(option = "dir",
            description ="The name of the domain module package directory of 'main' and 'test' source sets to make. (Mandatory)" ,
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

    @TaskAction
    def generate() {
    }
}
