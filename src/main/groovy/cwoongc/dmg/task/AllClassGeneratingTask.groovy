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


    @TaskAction
    def generate() {

    }




}
