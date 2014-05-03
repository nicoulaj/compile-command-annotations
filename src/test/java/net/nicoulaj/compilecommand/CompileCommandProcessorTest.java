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
