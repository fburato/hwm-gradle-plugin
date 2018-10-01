package com.github.fburato.hwmgradle;

import com.github.fburato.highwheelmodules.core.AnalyserFacade;
import com.github.fburato.highwheelmodules.utils.Pair;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class HwmAnalyseTask extends DefaultTask {

  private final String DEFAULT_SPEC_FILE_PATH = "spec.hwm";
  private final String DEFAULT_ANALYSIS_MODE = "strict";
  private final Optional<Integer> DEFAULT_EVIDENCE_LIMIT = Optional.of(0);
  private List<File> specFiles;
  private String analysisMode;
  private List<File> analysisPaths;
  private Optional<Integer> evidenceLimit;

  private class GradlePrinter implements AnalyserFacade.Printer {

    @Override
    public void info(String msg) {
      getLogger().quiet(msg);
    }
  }

  private class GradlePathEventSink implements AnalyserFacade.EventSink.PathEventSink {

    @Override
    public void ignoredPaths(List<String> ignored) {
      final String ignoreString = "Ignored: " + String.join(", ", ignored);
      if (ignored.isEmpty()) {
        getLogger().info(ignoreString);
      } else {
        getLogger().warn(ignoreString);
      }
    }

    @Override
    public void directories(List<String> directories) {
      getLogger().info("Directories: " + String.join(", ", directories));
    }

    @Override
    public void jars(List<String> jars) {
      getLogger().info("Directories: " + String.join(", ", jars));
    }
  }

  private class GradleMeasureSink implements AnalyserFacade.EventSink.MeasureEventSink {

    @Override
    public void fanInOutMeasure(String module, int fanIn, int fanOut) {
      getLogger().quiet(String.format("  %20s --> fanIn: %5d, fanOut: %5d", module, fanIn, fanOut));
    }
  }

  private class GradleStrincAnalysisSink implements AnalyserFacade.EventSink.StrictAnalysisEventSink {

    @Override
    public void dependenciesCorrect() {
      getLogger().info("No dependency violation detected");
    }

    @Override
    public void directDependenciesCorrect() {
      getLogger().info("No direct dependency violation detected");
    }

    @Override
    public void dependencyViolationsPresent() {
      getLogger().error("The following dependencies violate the specification:");
    }

    @Override
    public void dependencyViolation(String sourceModule, String destModule, List<String> expectedPath, List<String> actualPath, List<List<Pair<String, String>>> evidences) {
      getLogger().error(String.format("  %s -> %s. Expected path: %s, Actual module path: %s",
          sourceModule,
          destModule,
          printGraphPath(expectedPath),
          printGraphPath(actualPath)
      ));

      if (!evidenceLimit.isPresent() || evidenceLimit.get() > 0) {
        getLogger().error("  Actual evidence paths:");
        printEvidences(actualPath, evidences);
      }
    }

    @Override
    public void noDirectDependenciesViolationPresent() {
      getLogger().error("The following direct dependencies violate the specification:");
    }

    @Override
    public void noDirectDependencyViolation(String sourceModule, String destModule) {
      getLogger().error(String.format("  %s -> %s",
          sourceModule,
          destModule
      ));
    }
  }

  private class GradleLooseAnalysisEventSink implements AnalyserFacade.EventSink.LooseAnalysisEventSink {

    @Override
    public void allDependenciesPresent() {
      getLogger().info("All dependencies specified exist");
    }

    @Override
    public void noUndesiredDependencies() {
      getLogger().info("No dependency violation detected");
    }

    @Override
    public void absentDependencyViolationsPresent() {
      getLogger().error("The following dependencies do not exist:");
    }

    @Override
    public void absentDependencyViolation(String sourceModule, String destModule) {
      getLogger().error(String.format("  %s -> %s",
          sourceModule,
          destModule
      ));
    }

    @Override
    public void undesiredDependencyViolationsPresent() {
      getLogger().error("The following dependencies violate the specification:");
    }

    @Override
    public void undesiredDependencyViolation(String sourceModule, String destModule, List<String> path, List<List<Pair<String, String>>> evidences) {
      getLogger().error(String.format("  %s -/-> %s. Actual module path: %s",
          sourceModule,
          destModule,
          printGraphPath(path)
      ));
      if (!evidenceLimit.isPresent() || evidenceLimit.get() > 0) {
        getLogger().error("  Actual evidence paths:");
        printEvidences(path, evidences);
      }
    }
  }

  private static String printGraphPath(List<String> pathComponents) {
    if (pathComponents.isEmpty()) {
      return "(empty)";
    } else {
      return String.join(" -> ", pathComponents);
    }
  }

  private void printEvidences(List<String> modules, List<List<Pair<String, String>>> evidences) {
    for (int i = 0; i < modules.size() - 1; ++i) {
      final String current = modules.get(i);
      final String next = modules.get(i + 1);
      final List<Pair<String, String>> currentToNextEvidences = evidences.get(i);
      getLogger().error(String.format("    %s -> %s:", current, next));
      for (Pair<String, String> evidence : currentToNextEvidences) {
        getLogger().error(String.format("      %s -> %s", evidence.first, evidence.second));
      }
    }
  }

  public void setDefaults() {
    if (specFiles == null) {
      specFiles = Collections.singletonList(Paths.get(
          getProject().getProjectDir().getAbsolutePath(),
          DEFAULT_SPEC_FILE_PATH).toFile());
    }
    if (analysisMode == null) {
      analysisMode = DEFAULT_ANALYSIS_MODE;
    }
    if (analysisPaths == null) {
      analysisPaths = Collections.singletonList(
          Paths.get(
              getProject().getBuildDir().getAbsolutePath(), "classes"
          ).toFile()
      );
    }
    if (evidenceLimit == null) {
      evidenceLimit = DEFAULT_EVIDENCE_LIMIT;
    }
    this.setDescription("Executes the highwheel-modules analysis on the current project");
  }

  @TaskAction()
  void perform() {
    getLogger().info("Start module analysis");
    final AnalyserFacade.Printer printer = new GradlePrinter();
    final AnalyserFacade facade = new AnalyserFacade(printer,
        new GradlePathEventSink(),
        new GradleMeasureSink(),
        new GradleStrincAnalysisSink(),
        new GradleLooseAnalysisEventSink()
    );
    try {
      facade.runAnalysis(
          getAnalysisPaths().stream().map(File::getAbsolutePath).collect(Collectors.toList()),
          specFiles.stream().map(File::getAbsolutePath).collect(Collectors.toList()),
          getExecutionMode(analysisMode),
          evidenceLimit);
    } catch (Exception e) {
      throw new GradleException(e.getMessage());
    }
    getLogger().info("Module analysis complete");
  }

  private AnalyserFacade.ExecutionMode getExecutionMode(String analysisMode) {
    if (Objects.equals(analysisMode, "strict")) {
      return AnalyserFacade.ExecutionMode.STRICT;
    } else if (Objects.equals(analysisMode, "loose")) {
      return AnalyserFacade.ExecutionMode.LOOSE;
    } else {
      throw new InvalidUserDataException("Parameter 'analysisMode' needs to be either 'strict' or 'loose''");
    }
  }

  @Input
  public List<File> getSpecFiles() {
    return specFiles;
  }

  public void setSpecFiles(List<File> specFiles) {
    this.specFiles = specFiles;
  }

  @Input
  public String getAnalysisMode() {
    return analysisMode;
  }

  public void setAnalysisMode(String analysisMode) {
    this.analysisMode = analysisMode;
  }

  @Input
  public List<File> getAnalysisPaths() {
    return analysisPaths;
  }

  public void setAnalysisPaths(List<File> analysisPaths) {
    this.analysisPaths = analysisPaths;
  }

  @Input
  public Optional<Integer> getEvidenceLimit() {
    return evidenceLimit;
  }

  public void setEvidenceLimit(Optional<Integer> evidenceLimit) {
    this.evidenceLimit = evidenceLimit;
  }
}
