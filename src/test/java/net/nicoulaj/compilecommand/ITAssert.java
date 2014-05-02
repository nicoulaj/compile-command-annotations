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

import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.util.Map;

/**
 * Utility for integration test asserts..
 * <p/>
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
        return FileUtils.fileRead(new File(baseDirectory, path));
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
        else if (FileUtils.fileRead(file).length() == 0)
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
        if (!FileUtils.fileRead(new File(baseDirectory, path)).contains(search))
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
        if (FileUtils.fileRead(new File(baseDirectory, path)).contains(search))
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
}

