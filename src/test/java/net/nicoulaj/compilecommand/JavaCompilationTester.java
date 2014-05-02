/*
 * Copyright 2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.nicoulaj.compilecommand;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import static com.google.common.collect.Iterables.toArray;
import static java.nio.file.Files.*;
import static java.nio.file.Paths.get;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static javax.tools.Diagnostic.Kind.MANDATORY_WARNING;
import static javax.tools.Diagnostic.Kind.WARNING;
import static javax.tools.JavaCompiler.CompilationTask;
import static javax.tools.StandardLocation.*;

/**
 * Java compiler for testing.
 *
 * @author <a href="http://github.com/nicoulaj">Julien Nicoulaud</a>
 */
public final class JavaCompilationTester {

    private static final JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();

    private static final Path COMPILATION_OUTPUT_BASE_DIRECTORY = get("target", "test-compilation");

    public Report compile(Path source, String... options) {
        return compile(source.toString(), options);
    }

    public Report compile(String source, String... options) {
        return compile(asList(source), asList(options));
    }

    public Report compile(Iterable<String> sources, Iterable<String> options) {

        for (String option : options)
            if (COMPILER.isSupportedOption(option) < 0)
                throw new IllegalArgumentException("Unsupported option \"" + option + "\"");

        final DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        final StandardJavaFileManager fileManager = COMPILER.getStandardFileManager(collector, Locale.getDefault(), Charset.defaultCharset());

        Path outputClasses;
        Path outputSources;
        Path outputNativeHeaders;
        try {
            createDirectories(COMPILATION_OUTPUT_BASE_DIRECTORY);
            final Path output = createTempDirectory(COMPILATION_OUTPUT_BASE_DIRECTORY, "test-compilation-");

            outputClasses = output.resolve("classes");
            createDirectory(outputClasses);
            fileManager.setLocation(CLASS_OUTPUT, singleton(outputClasses.toFile()));

            outputSources = output.resolve("sources");
            createDirectory(outputSources);
            fileManager.setLocation(SOURCE_OUTPUT, singleton(outputClasses.toFile()));

            outputNativeHeaders = output.resolve("native-headers");
            createDirectory(outputNativeHeaders);
            fileManager.setLocation(NATIVE_HEADER_OUTPUT, singleton(outputNativeHeaders.toFile()));

        } catch (IOException e) {
            throw new RuntimeException("Failed setting up compilation output directories", e);
        }

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
             OutputStreamWriter out = new OutputStreamWriter(stream)) {

            final CompilationTask task = COMPILER.getTask(out,
                                                          fileManager,
                                                          collector,
                                                          options,
                                                          null,
                                                          fileManager.getJavaFileObjects(toArray(sources, String.class)));

            final Boolean successful = task.call();

            final String stdout = new String(stream.toByteArray());

            return new Report(outputClasses, outputSources, outputNativeHeaders, successful, collector.getDiagnostics(), stdout);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static final class Report {

        private final Path classesDirectory;

        private final Path sourcesDirectory;

        private final Path nativeHeadersDirectory;

        private final boolean successful;

        private final List<Diagnostic<? extends JavaFileObject>> diagnostics;

        private final String stdout;

        Report(final Path classesDirectory,
               final Path sourcesDirectory,
               final Path nativeHeadersDirectory,
               final boolean successful,
               final List<Diagnostic<? extends JavaFileObject>> diagnostics,
               final String stdout) {
            this.classesDirectory = classesDirectory;
            this.sourcesDirectory = sourcesDirectory;
            this.nativeHeadersDirectory = nativeHeadersDirectory;
            this.successful = successful;
            this.diagnostics = diagnostics;
            this.stdout = stdout;
        }

        public Path getClassesDirectory() {
            return classesDirectory;
        }

        public Path getSourcesDirectory() {
            return sourcesDirectory;
        }

        public Path getNativeHeadersDirectory() {
            return nativeHeadersDirectory;
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
