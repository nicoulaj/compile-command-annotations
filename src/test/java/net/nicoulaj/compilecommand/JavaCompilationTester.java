/*
 * ====================================================================
 * Hotspot compile command annotations
 * ====================================================================
 * Copyright (C) 2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package net.nicoulaj.compilecommand;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

import static com.google.common.collect.Iterables.toArray;
import static java.lang.System.nanoTime;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static javax.tools.Diagnostic.Kind.MANDATORY_WARNING;
import static javax.tools.Diagnostic.Kind.WARNING;
import static javax.tools.JavaCompiler.CompilationTask;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.SOURCE_OUTPUT;

/**
 * Java compiler for testing.
 *
 * @author <a href="http://github.com/nicoulaj">Julien Nicoulaud</a>
 */
public final class JavaCompilationTester {

    private static final JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();

    private static final File COMPILATION_OUTPUT_BASE_DIRECTORY = new File("target", "test-compilation");

    public Report compile(File source, String... options) {
        return compile(source.toString(), options);
    }

    public Report compile(String source, String... options) {
        return compile(asList(source), asList(options));
    }

    public Report compile(Iterable<String> sources, Iterable<String> options) {

        for (String option : options)
            if (COMPILER.isSupportedOption(option) < 0)
                throw new IllegalArgumentException("Unsupported option \"" + option + "\"");

        final DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<JavaFileObject>();
        final StandardJavaFileManager fileManager = COMPILER.getStandardFileManager(collector, Locale.getDefault(), Charset.defaultCharset());

        File outputClasses;
        File outputSources;
        try {
            final File output = new File(COMPILATION_OUTPUT_BASE_DIRECTORY, String.valueOf(nanoTime()));
            if (!output.mkdirs()) throw new RuntimeException("Failed creating compilation output directory");

            outputClasses = new File(output, "classes");
            if (!outputClasses.mkdir()) throw new RuntimeException("Failed creating compilation output directory");
            fileManager.setLocation(CLASS_OUTPUT, singleton(outputClasses));

            outputSources = new File(output, "sources");
            if (!outputSources.mkdir()) throw new RuntimeException("Failed creating compilation output directory");
            fileManager.setLocation(SOURCE_OUTPUT, singleton(outputClasses));

        } catch (IOException e) {
            throw new RuntimeException("Failed setting up compilation output directories", e);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        OutputStreamWriter out = new OutputStreamWriter(stream);
        try {

            final CompilationTask task = COMPILER.getTask(out,
                                                          fileManager,
                                                          collector,
                                                          options,
                                                          null,
                                                          fileManager.getJavaFileObjects(toArray(sources, String.class)));

            final Boolean successful = task.call();

            final String stdout = new String(stream.toByteArray());

            return new Report(outputClasses, outputSources, successful, collector.getDiagnostics(), stdout);
        } finally {
            try {
                stream.close();
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static final class Report {

        private final File classesDirectory;

        private final File sourcesDirectory;

        private final boolean successful;

        private final List<Diagnostic<? extends JavaFileObject>> diagnostics;

        private final String stdout;

        Report(final File classesDirectory,
               final File sourcesDirectory,
               final boolean successful,
               final List<Diagnostic<? extends JavaFileObject>> diagnostics,
               final String stdout) {
            this.classesDirectory = classesDirectory;
            this.sourcesDirectory = sourcesDirectory;
            this.successful = successful;
            this.diagnostics = diagnostics;
            this.stdout = stdout;
        }

        public File getClassesDirectory() {
            return classesDirectory;
        }

        public File getSourcesDirectory() {
            return sourcesDirectory;
        }

        public boolean isSuccessful() {
            return successful;
        }

        public boolean isFailed() {
            return !successful;
        }

        public boolean hasErrors() {
            return !successful;
        }

        public boolean hasWarnings() {
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics)
                if (diagnostic.getKind() == WARNING || diagnostic.getKind() == MANDATORY_WARNING)
                    return true;
            return false;
        }

        public List<Diagnostic<? extends JavaFileObject>> getDiagnostics() {
            return diagnostics;
        }

        public String getStdout() {
            return stdout;
        }
    }
}
