/**
 * Hotspot compile command annotations - http://compile-command-annotations.nicoulaj.net
 * Copyright © 2014-2017 Hotspot compile command annotations contributors
 *
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
 */
package net.nicoulaj.compilecommand;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.ObjectArrays.concat;
import static java.util.Arrays.sort;
import static net.nicoulaj.compilecommand.CompileCommandProcessor.COMPILE_COMMAND_FILE_PATH_DEFAULT;
import static net.nicoulaj.compilecommand.JavaCompilationTester.Report;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Unit tests for {@link CompileCommandProcessor}.
 *
 * @author <a href="http://github.com/nicoulaj">Julien Nicoulaud</a>
 */
public final class CompileCommandProcessorTest {

    private static final JavaCompilationTester JAVAC = new JavaCompilationTester();

    private static final File SAMPLES_SOURCES = new File("src/samples/java/net/nicoulaj/compilecommand");

    private static final File SAMPLES_RESOURCES = new File("src/samples/resources/net/nicoulaj/compilecommand");

    private static final File TEST_CASES_SOURCES = new File("src/test/java/net/nicoulaj/compilecommand/testcases");

    private static final File TEST_CASES_RESOURCES = new File("src/test/resources/net/nicoulaj/compilecommand/testcases");

    @DataProvider
    public Object[][] testcases() throws IOException {
        return concat(getDataProvider(TEST_CASES_SOURCES, TEST_CASES_RESOURCES),
                      getDataProvider(SAMPLES_SOURCES, SAMPLES_RESOURCES),
                      Object[].class);
    }

    private Object[][] getDataProvider(File sourceDir, File resourceDir) throws IOException {
        final List<Object[]> data = new ArrayList<Object[]>();
        final File[] sources = sourceDir.listFiles();
        if (sources == null) throw new IllegalArgumentException("No source in " + sourceDir);
        sort(sources);
        for (File source : sources)
            if ("java".equals(getExtension(source.toString())))
                if (!"package-info".equals(getBaseName(source.toString())))
                    data.add(new Object[]{source, new File(resourceDir, getBaseName(source.toString()))});
        return data.toArray(new Object[data.size()][]);
    }

    @Test(dataProvider = "testcases")
    public void test(File source, File expected) {
        final Report compilation = JAVAC.compile(source);
        assertTrue(compilation.isSuccessful(), "compilation failed");
        assertFalse(compilation.hasErrors(), "compilation has errors");
        assertFalse(compilation.hasWarnings(), "compilation has warnings");
        assertThat(new File(compilation.getClassesDirectory(), COMPILE_COMMAND_FILE_PATH_DEFAULT)).hasContentEqualTo(expected);
    }
}
