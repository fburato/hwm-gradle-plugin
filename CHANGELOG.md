# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/).
## [0.2] - 2018-09-23

As the wise use to say: if it ain't broke, don't fix it. Similarly, if you don't read the manual and don't realise that the @Option you thought could be used to improve the product has some consequences, you find your addition to cause an exception to be thrown at runtime.

With version 0.2 you can define multiple `HwmAnalyseTask` in your build, so that you can verify multiple structural properties with different level of details. Everything without causing the build to explode, which is a nice touch.

### Changed

Removed Option annotation from the Task fields to allow creation of multiple tasks in the same build without cast exception.

## [0.1] - 2018-09-23

Initial implementation of the plugin. Maven sucks, AM I RITE? Gradle rulez! 
Should we limit ourselves to use a build tool that is updated every 5 years? Of course not! `hwm-gradle-plugin`
to the rescue for modern developers. 

