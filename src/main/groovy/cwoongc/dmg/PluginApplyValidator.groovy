package cwoongc.dmg

import org.gradle.api.GradleException
import org.gradle.api.Project

class PluginApplyValidator {

    static void validate(Project project) {
        if(!project.plugins.hasPlugin('java')) {

            def msg = """'cwoongc.DMG' plugin depends on the 'java' plugin.
            Apply the 'java' plugin first."""

            throw new GradleException(msg)
        }

        
    }


}