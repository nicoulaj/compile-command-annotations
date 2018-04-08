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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Map;

/**
 * Utility for integration test asserts..
 * <p>See {@code src/main/it/projects/.../postbuild.groovy} scripts.</p>
 *
 * @author <a href="http://github.com/nicoulaj">Julien Nicoulaud</a>
 * @see <a href="http://maven.apache.org/plugins/maven-invoker-plugin/examples/post-build-script.html">
 * maven-invoker-plugin post-build script invocation</a>
 */
public final class ITAssert {

    /** The name of the build log file. */
    public static final String BUILD_LOG_FILE = "build.log";

    /** The absolute path to the base directory of the test project. */
    protected File baseDirectory;

    /** The absolute path to the local repository used for the Maven invocation on the test project. */
    protected File localRepositoryPath;

    /** The storage of key-value pairs used to pass data from the pre-build hook script to the post-build hook script. */
    protected Map context;

    /**
     * Build a new {@link ITAssert} instance.
     *
     * @param baseDirectory       the absolute path to the base directory of the test project..
     * @param localRepositoryPath the absolute path to the local repository used for the Maven invocation on the test
     *                            project..
     * @param context             the storage of key-value pairs used to pass data from the pre-build hook script to the
     *                            post-build hook script..
     * @see <a href="http://maven.apache.org/plugins/maven-invoker-plugin/examples/post-build-script.html">
     * maven-invoker-plugin post-build script invocation</a>
     */
    public ITAssert(File baseDirectory, File localRepositoryPath, Map context) {
        this.baseDirectory = baseDirectory;
        this.localRepositoryPath = localRepositoryPath;
        this.context = context;
    }

    /**
     * Get the contents of the file.
     *
     * @param path the path to the file relative to {@link #baseDirectory}.
     * @return the file content.
     * @throws Exception if the build log could not be open.
     */
    public String getFileContent(String path) throws Exception {
        return FileUtils.readFileToString(new File(baseDirectory, path));
    }

    /**
     * Get the project build log content.
     *
     * @return the project build log content.
     * @throws Exception if the build log could not be open.
     */
    public String getBuildLog() throws Exception {
        return getFileContent(BUILD_LOG_FILE);
    }

    /**
     * Assert the given file exists and is a file.
     *
     * @param path the path to the file relative to {@link #baseDirectory}.
     * @throws Exception if conditions are not fulfilled.
     */
    public void assertFileExists(String path) throws Exception {
        if (!new File(baseDirectory, path).isFile())
            throw new Exception("The file " + path + " is missing.");
    }

    /**
     * Assert the given file does not exist.
     *
     * @param path the path to the file relative to {@link #baseDirectory}.
     * @throws Exception if conditions are not fulfilled.
     */
    public void assertFileDoesNotExist(String path) throws Exception {
        if (new File(baseDirectory, path).isFile())
            throw new Exception("The file " + path + " exists, but it should not.");
    }

    /**
     * Assert the given file exists and is a non-empty file.
     *
     * @param path the path to the file relative to {@link #baseDirectory}.
     * @throws Exception if conditions are not fulfilled.
     */
    public void assertFileIsNotEmpty(String path) throws Exception {
        final File file = new File(baseDirectory, path);
        if (!file.isFile())
            throw new Exception("The file " + path + " is missing or not a file.");
        else if (FileUtils.readFileToString(file).length() == 0)
            throw new Exception("The file " + path + " is empty.");
    }

    /**
     * Assert the file contains the given search.
     *
     * @param path   the path to the file relative to {@link #baseDirectory}.
     * @param search the expression to search in the build log.
     * @throws Exception if conditions are not fulfilled.
     */
    public void assertFileContains(String path, String search) throws Exception {
        if (!FileUtils.readFileToString(new File(baseDirectory, path)).contains(search))
            throw new Exception(path + " does not contain '" + search + "'.");
    }

    /**
     * Assert the project build log contains the given search.
     *
     * @param search the expression to search in the build log.
     * @throws Exception if conditions are not fulfilled.
     */
    public void assertBuildLogContains(String search) throws Exception {
        assertFileContains(BUILD_LOG_FILE, search);
    }

    /**
     * Assert the file does not contain the given search.
     *
     * @param path   the path to the file relative to {@link #baseDirectory}.
     * @param search the expression to search in the build log.
     * @throws Exception if conditions are not fulfilled.
     */
    public void assertFileDoesNotContain(String path, String search) throws Exception {
        if (FileUtils.readFileToString(new File(baseDirectory, path)).contains(search))
            throw new Exception(path + " contains '" + search + "'.");
    }

    /**
     * Assert the project build log does not contain the given search.
     *
     * @param search the expression to search in the build log.
     * @throws Exception if conditions are not fulfilled.
     */
    public void assertBuildLogDoesNotContain(String search) throws Exception {
        assertFileDoesNotContain(BUILD_LOG_FILE, search);
    }

    /**
     * Delete the given file if it exists.
     *
     * @param file path to the file relative to basedir
     * @throws Exception if conditions are not fulfilled.
     */
    public void deleteIfExists(String file) throws Exception {
        final File f = new File(baseDirectory, file);
        if (f.exists() && f.isFile()) f.delete();
    }

    /**
     * Test if currently running JVM is version 6.
     *
     * @return {@code true} if running on Java 6
     */
    public boolean isJava6() {
        return "1.6".equals(System.getProperty("java.specification.version"));
    }

    /**
     * Test if currently running JVM is version 7.
     *
     * @return {@code true} if running on Java 7
     */
    public boolean isJava7() {
        return "1.7".equals(System.getProperty("java.specification.version"));
    }

    /**
     * Test if currently running JVM is version 8.
     *
     * @return {@code true} if running on Java 8
     */
    public boolean isJava8() {
        return "1.8".equals(System.getProperty("java.specification.version"));
    }
}
