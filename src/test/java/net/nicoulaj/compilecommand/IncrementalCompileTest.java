/*
 * Hotspot compile command annotations - http://compile-command-annotations.nicoulaj.net
 * Copyright © 2014-2018 Hotspot compile command annotations contributors
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.sort;
import static net.nicoulaj.compilecommand.CompileCommandProcessor.COMPILE_COMMAND_FILE_PATH_DEFAULT;
import static net.nicoulaj.compilecommand.CompileCommandProcessor.COMPILE_COMMAND_INCREMENTAL_OUTPUT_OPTION;
import static net.nicoulaj.compilecommand.JavaCompilationTester.Report;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class IncrementalCompileTest {
    private static final JavaCompilationTester JAVAC = new JavaCompilationTester();

    private static final File TEST_CASES_SOURCES = new File("src/test/java/net/nicoulaj/compilecommand/incrementaltests");

    private static final File TEST_CASES_RESOURCES = new File("src/test/resources/net/nicoulaj/compilecommand/incrementaltests");

    private static final File TEST_CASES_MERGED = new File("src/test/resources/net/nicoulaj/compilecommand/incrementaltests-merged");

    private static final String INCREMENTAL_FRAGMENTS = "INCREMENTAL_FRAGMENTS";

    @DataProvider
    public Object[][] testcases() throws IOException {
        return getDataProvider(TEST_CASES_SOURCES, TEST_CASES_RESOURCES, TEST_CASES_MERGED);
    }

    private Object[][] getDataProvider(File sourceDir, File resourceDir, File mergedDir) {
        final List<Object[]> data = new ArrayList<Object[]>();
        final File[] sources = sourceDir.listFiles();
        if (sources == null) throw new IllegalArgumentException("No source in " + sourceDir);
        sort(sources);
        for (File source : sources)
            if ("java".equals(getExtension(source.toString())))
                if (!"package-info".equals(getBaseName(source.toString())))
                    data.add(new Object[]{source,
                                          new File(resourceDir, getBaseName(source.toString())),
                                          new File(mergedDir, getBaseName(source.toString()))});
        return data.toArray(new Object[data.size()][]);
    }

    @Test(dataProvider = "testcases")
    public void test(File source, File expected, File expectedMerged) throws IOException
    {
        final Report compilation = JAVAC.compile(source,
                                                 String.format("-A%s=%s", COMPILE_COMMAND_INCREMENTAL_OUTPUT_OPTION, INCREMENTAL_FRAGMENTS));
        assertTrue(compilation.isSuccessful(), "compilation failed");
        assertFalse(compilation.hasErrors(), "compilation has errors");
        assertFalse(compilation.hasWarnings(), "compilation has warnings");
        assertFalse(new File(compilation.getClassesDirectory(), COMPILE_COMMAND_FILE_PATH_DEFAULT).exists(), "default output file exists, but must not exist");
        File fragmentsDir = new File(compilation.getClassesDirectory(), INCREMENTAL_FRAGMENTS);
        assertTrue(fragmentsDir.isDirectory(), "incremental fragments output must be a directory");

        Map<String, String> fragments = readFiles(fragmentsDir);
        Map<String, String> expectedFragments = readFiles(expected);
        assertEquals(expectedFragments, fragments);

        String expectedMerge = readFile(expectedMerged);
        File hotspotCompilerFile = new File(fragmentsDir.getParent(), "hotspot_compiler");
        IncrementalCompilationHelper.mergeIncrementalFiles(fragmentsDir, hotspotCompilerFile);
        String merged = readFile(hotspotCompilerFile);
        assertEquals(expectedMerge, merged);
    }

    private static Map<String, String> readFiles(File dir) throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        File[] files = dir.listFiles();
        for (File file : files) {
            if (!file.isFile())
                continue;

            String content = readFile(file);
            map.put(file.getName(), content);
        }
        return map;
    }

    private static String readFile(File file) throws IOException
    {
        byte[] data = new byte[(int) file.length()];
        FileInputStream in = new FileInputStream(file);
        try {
            for (int p = 0; p < data.length; ) {
                int rd = in.read(data, p, data.length - p);
                if (rd < 0) {
                    throw new IOException("EOF reading " + file);
                }
                p += rd;
            }
        }
        finally {
            in.close();
        }
        return new String(data);
    }

    @Test(expectedExceptions = { NullPointerException.class }, expectedExceptionsMessageRegExp = "Input directory must not be null")
    public void testNullDir() throws IOException
    {
        IncrementalCompilationHelper.mergeIncrementalFiles(null, new File("f"));
    }

    @Test(expectedExceptions = { NullPointerException.class }, expectedExceptionsMessageRegExp = "Output file must not be null")
    public void testNullFile() throws IOException
    {
        IncrementalCompilationHelper.mergeIncrementalFiles(new File("."), null);
    }

    @Test(expectedExceptions = { FileNotFoundException.class }, expectedExceptionsMessageRegExp = "Directory for incrementalFiles '.*' does not exist")
    public void testInvalidDirectory() throws IOException
    {
        IncrementalCompilationHelper.mergeIncrementalFiles(new File("does_not_exist"), null);
    }
}
