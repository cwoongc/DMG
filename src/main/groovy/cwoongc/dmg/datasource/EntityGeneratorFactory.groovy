package cwoongc.dmg.datasource

import cwoongc.dmg.DomainModuleGeneratorExtension.DataSource
import org.gradle.api.GradleException

class EntityGeneratorFactory {

    static EntityGenerator create(DataSource dataSource) {

        EntityGenerator entityGenerator = null

        String driverClassName = dataSource.getDriverClassName()

        if(driverClassName.contains("oracle")) {
            entityGenerator = new OracleEntityGenerator(dataSource)
        } else {
            throw new GradleException("'${dataSource.driverClassName}' is not supported.")
        }

        return entityGenerator

    }


}
