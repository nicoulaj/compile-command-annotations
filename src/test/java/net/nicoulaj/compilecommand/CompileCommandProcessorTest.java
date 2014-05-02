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

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.Paths.get;
import static net.nicoulaj.compilecommand.JavaCompilationTester.Report;
import static net.nicoulaj.compilecommand.CompileCommandProcessor.COMPILE_COMMAND_FILE_PATH_DEFAULT;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Unit tests for {@link CompileCommandProcessor}.
 *
 * @author <a href="http://github.com/nicoulaj">Julien Nicoulaud</a>
 */
public final class CompileCommandProcessorTest {

    private static final JavaCompilationTester JAVAC = new JavaCompilationTester();

    private static final Path SAMPLES_SOURCES = get("src/samples/java/net/nicoulaj/compilecommand");

    private static final Path SAMPLES_RESOURCES = get("src/samples/java/net/nicoulaj/compilecommand");

    private static final Path TEST_CASES_SOURCES = get("src/test/java/net/nicoulaj/compilecommand/testcases");

    private static final Path TEST_CASES_RESOURCES = get("src/test/resources/net/nicoulaj/compilecommand/testcases");

    @DataProvider
    public Object[][] testcases() throws IOException {
        return getDataProvider(TEST_CASES_SOURCES, TEST_CASES_RESOURCES);
    }

    @DataProvider
    public Object[][] samples() throws IOException {
        return getDataProvider(SAMPLES_SOURCES, SAMPLES_RESOURCES);
    }

    @Test(dataProvider = "testcases")
    public void testCase(Path source, Path expected) {
        test(source, expected);
    }

    @Test(dataProvider = "samples")
    public void testSample(Path source, Path expected) {
        test(source, expected);
    }

    private void test(Path source, Path expected) {
        final Report compilation = JAVAC.compile(source);
        assertTrue(compilation.isSuccessful(), "compilation failed");
        assertFalse(compilation.hasErrors(), "compilation has errors");
        assertFalse(compilation.hasWarnings(), "compilation has warnings");
        assertThat(compilation.getClassesDirectory().resolve(COMPILE_COMMAND_FILE_PATH_DEFAULT).toFile()).hasContentEqualTo(expected.toFile());
    }

    private Object[][] getDataProvider(Path sourceDir, Path resourceDir) throws IOException {
        return Files.walk(sourceDir)
                    .filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> !path.endsWith("package-info.java"))
                    .sorted()
                    .map(path -> new Object[]{path, resourceDir.resolve(path.getFileName().toString().replace(".java", ""))})
                    .toArray(Object[][]::new);
    }
}
