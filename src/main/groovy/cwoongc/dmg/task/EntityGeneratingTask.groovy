package cwoongc.dmg.task

import cwoongc.dmg.DomainModuleGeneratorExtension
import cwoongc.dmg.datasource.EntityGenerator
import cwoongc.dmg.datasource.EntityGeneratorFactory
import cwoongc.dmg.task.validator.GeneratingTaskValidator
import groovy.sql.Sql;
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException;
import org.gradle.api.internal.tasks.options.Option;
import org.gradle.api.tasks.TaskAction

import java.sql.Connection
import java.sql.DriverManager

class EntityGeneratingTask extends DefaultTask {

    @Option(option = "dir",
            description ="The name of the domain module package directory of 'main' source sets. (Mandatory)" ,
            order = 1)
    String dir

    @Option(option = "table",
            description = "The name of the table to use making entity class. (Mandatory)",
            order = 2)
    String table

    @Option(option = "id",
            description = "The 'id' of the 'dataSource' to use connecting the data source. If not provided, the top one described in DMG script block will be used.",
            order = 3)
    String dataSourceId

    @Option(option = 'dd',
            description = """(Duplicated Directory)
               Set type of way to use when the directory already exists.
               Available values are:
                   abort (default)
                   use""",
            order = 4)
    String dd

    @Option(option = 'df',
            description = """(Duplicated File)
               Set type of way to use when the class file already exists.
               Available values are:
                   abort (default)
                   overwrite
                   skip""",
            order = 5)
    String df

    DomainModuleGeneratorExtension.DataSource dataSource

    Sql sql


    @TaskAction
    def generate() {


        GeneratingTaskValidator.validate(project, this, dir, table, dataSourceId, dd, df)

        DomainModuleGeneratorExtension dmg = project.extensions.findByName("DMG")

        dataSource = dmg.dataSources.get(dataSourceId)

        EntityGenerator entityGenerator = EntityGeneratorFactory.create(dataSource)

        entityGenerator.generate(table)


    }


}
