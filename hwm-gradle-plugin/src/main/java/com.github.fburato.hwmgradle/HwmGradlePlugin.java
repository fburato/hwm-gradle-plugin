package com.github.fburato.hwmgradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class HwmGradlePlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.getTasks().create("hwmAnalyse", HwmAnalyseTask.class, HwmAnalyseTask::setDefaults);
  }
}
