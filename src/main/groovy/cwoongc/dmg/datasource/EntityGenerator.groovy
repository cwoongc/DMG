package cwoongc.dmg.datasource

import org.gradle.api.GradleException

interface EntityGenerator {

    void generate(String tableName) throws GradleException

}