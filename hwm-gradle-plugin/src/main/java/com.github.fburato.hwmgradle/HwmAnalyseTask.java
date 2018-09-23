package com.github.fburato.hwmgradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class HwmAnalyseTask extends DefaultTask {

    private final String DEFAULT_SPEC_FILE_PATH = "spec.hwm";
    private final String DEFAULT_ANALYSIS_MODE = "strict";
    private final int DEFAULT_EVIDENCE_LIMIT = 0;
    private File specFile;
    private String analysisMode;
    private List<File> analysisPaths;
    private Integer evidenceLimit;

    private void setDefaults() {
        if(specFile == null) {
            specFile = Paths.get(
                    getProject().getProjectDir().getAbsolutePath(),
                    DEFAULT_SPEC_FILE_PATH).toFile();
        }
        if(analysisMode == null) {
            analysisMode = DEFAULT_ANALYSIS_MODE;
        }
        if(analysisPaths == null) {
            analysisPaths = Collections.singletonList(
                    Paths.get(
                            getProject().getBuildDir().getAbsolutePath(), "classes"
                    ).toFile()
            );
        }
        if(evidenceLimit == null) {
            evidenceLimit = DEFAULT_EVIDENCE_LIMIT;
        }
    }

    @TaskAction
    void perform() {
        setDefaults();
        System.out.println(specFile);
        System.out.println(analysisMode);
        System.out.println(analysisPaths);
        System.out.println(evidenceLimit);
    }

    @Input
    public File getSpecFile() {
        return specFile;
    }

    public void setSpecFile(File specFile) {
        this.specFile = specFile;
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
    public int getEvidenceLimit() {
        return evidenceLimit;
    }

    public void setEvidenceLimit(int evidenceLimit) {
        this.evidenceLimit = evidenceLimit;
    }
}
