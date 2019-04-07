# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/).

## [1.2] - 2019-04-07
HWM 1.5.0 comes full with new features: blacklist support, whitelist support and a new rule definition syntax
that allows to make specification skinnier. 1.2 just switches to the latest version of the coolest architecture
analysis tool ever.

## [1.1] - 2018-10-03
No need to provide a fully qualified package for the modules anymore! With the update of the core
hwm to 1.4.0, now you can provide a prefix in the preamble of the specification that will be
added to all regex in the module specification. Isn't that cool?

## [1.0] - 2018-10-01

The plugin is now a big boy. It has had a few ride in Android code-bases, it has proved it is quite capable, and merits to get its driving license.
Moreover, as a graduation gift, the latest version of the core `highwheel-module` has been integrated (now `1.3.0`) and the plugin can perform
many analysis with one pass of byte-code. Faster is better.

### Changed

- The task option `specFile` has been renamed to `specFiles` as now it accepts a list of files as input
- The task option `evidenceLimit` has be retyped to `Optional<Integer>` to allow a more precise semantics of the limitation.
- The indentation of the event sinks has been improved to ease the read.

## [0.2] - 2018-09-23

As the wise use to say: if it ain't broke, don't fix it. Similarly, if you don't read the manual and don't realise that the @Option you thought could be used to improve the product has some consequences, you find your addition to cause an exception to be thrown at runtime.

With version 0.2 you can define multiple `HwmAnalyseTask` in your build, so that you can verify multiple structural properties with different level of details. Everything without causing the build to explode, which is a nice touch.

### Changed

Removed Option annotation from the Task fields to allow creation of multiple tasks in the same build without cast exception.

## [0.1] - 2018-09-23

Initial implementation of the plugin. Maven sucks, AM I RITE? Gradle rulez! 
Should we limit ourselves to use a build tool that is updated every 5 years? Of course not! `hwm-gradle-plugin`
to the rescue for modern developers. 

