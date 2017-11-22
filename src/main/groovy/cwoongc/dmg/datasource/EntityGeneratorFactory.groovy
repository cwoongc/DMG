package cwoongc.dmg.datasource

import cwoongc.dmg.DomainModuleGeneratorExtension.DataSource
import org.gradle.api.GradleException
import org.gradle.api.Project

class EntityGeneratorFactory {

    static EntityGenerator create(DataSource dataSource, Project project) {

        EntityGenerator entityGenerator = null

        String driverClassName = dataSource.getDriverClassName()

        if(driverClassName.contains("oracle")) {
            entityGenerator = new OracleEntityGenerator(dataSource, project)
        } else {
            throw new GradleException("'${dataSource.driverClassName}' is not supported.")
        }

        return entityGenerator

    }


}
