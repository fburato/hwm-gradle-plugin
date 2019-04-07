# Highwheel-modules gradle plugin

Highwheel-modules gradle plugin is a gradle plugin that allow to run 
[highwheel-modules](https://github.com/fburato/highwheel-modules) analysis
on a gradle project as part of the build.

The plugin define a gradle task called `hwmAnalyse` which executes the highwheel-modules analysis using the following
settings for the configuration:

* `analysisMode: String ( = "strict")`: either `strict` or `loose`,  sets the analysis mode for the module analysis.
* `specFiles: List<File> ( = List(project.projectDir / "spec.hwm"))`: sets the files containing
the specifications of the project to be used in the analysis.
* `evidenceLimit: Optional<Integer> ( = Optional.of(0))`: sets the limit to the pieces of evidence showed by the plugin to prove that a rule in the 
specification has been violated
* `analysisPaths: List<File> ( = List(project.buildDir / "classes"))`: sets the files and directories that will
 be added to the analysis.

The task will succeed if the analysis passes, it will fail otherwise. By default, the only information which is printed
at the end of every build are the metrics. If you want to see more debugging information, run the task with the `-i`
logging flag.

If the build fails, the rules that are violated will be printed out in the error logger.

## Installation

Add to your `build.gradle` buildscript closure the following details:

```groovy
buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath 'com.github.fburato:hwm-gradle-plugin:1.2'
    }
}

import com.github.fburato.hwmgradle.HwmGradlePlugin

apply plugin: HwmGradlePlugin
```

## Tips

### Make the analysis task dependent on the compile task

In order to reduce the coupling of the plugin to other plugins, the `hwmAnalyse` task is completely independent
from any build task. It is strongly recommended to introduce a dependency on the compilation task of the language of
choice in order to have Gradle compiling the project before launching the analysis.

An example on how this can be achieved in a Java project is:

```groovy
// preamble with the buildscript above

plugins {
    id 'java'
}

import com.github.fburato.hwmgradle.HwmGradlePlugin

apply plugin: HwmGradlePlugin

hwmAnalyse.dependsOn(compileJava)
```

For Scala, Kotlin or other projects, simply change the `compileJava` task with the specific compilation task.

### Multi-module build

If you want to run an analysis in a multi-project build and include all children projects build directories in the
analysis path, you simply have to add these directories to the `analysisPaths` property of the `hwmAnalyse` task.

For example, let's assume you have a multi-project build with the following `settings.gradle`:

```groovy
rootProject.name = "parent"

include 'child1'
include 'child2'
include 'child3'
``` 

In the `build.gradle` file, you simply have to set up the `hwmAnalyse` task as follows:

```groovy
// preamble to set up hwm-gradle-plugin

import java.nio.file.Paths
import java.util.Arrays


File classDirectory(String projectName)  {
    return Paths.get(project(projectName).buildDir.absolutePath,"classes").toFile()
}

hwmAnalyse {
    analysisPaths = Arrays.asList(classDirectory(":child1"),classDirectory(":child2"),classDirectory(":child3"))
}
```
