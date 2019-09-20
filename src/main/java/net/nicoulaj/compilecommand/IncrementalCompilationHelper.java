/*
 * Hotspot compile command annotations - http://compile-command-annotations.nicoulaj.net
 * Copyright © 2014-2019 Hotspot compile command annotations contributors
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Set;
import java.util.TreeSet;

public class IncrementalCompilationHelper {
    public static void mergeIncrementalFiles(File incrementalFiles, File outputFile) throws IOException {
        if (incrementalFiles == null)
            throw new NullPointerException("Input directory must not be null");
        if (!incrementalFiles.isDirectory())
            throw new FileNotFoundException("Directory for incrementalFiles '" + incrementalFiles + "' does not exist");
        if (outputFile == null)
            throw new NullPointerException("Output file must not be null");

        PrintWriter pw = null;
        try {
            File[] files = incrementalFiles.listFiles();
            if (files == null)
                throw new IOException("Could not list files in input directory " + incrementalFiles);

            Set<String> lines = new TreeSet<String>();
            boolean quiet = false;

            for (File file : files) {
                quiet = processFile(lines, quiet, file);
            }

            pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
            if (quiet) {
                pw.println("quiet");
            }
            for (String line : lines) {
                pw.println(line);
            }
        }
        finally {
            if (pw != null)
                pw.close();
        }
    }

    private static boolean processFile(Set<String> lines, boolean quiet, File file) throws IOException
    {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String l;
            while ((l = br.readLine()) != null) {
                if ("quiet".equals(l))
                    quiet = true;
                else
                    lines.add(l);
            }
        }
        finally {
            if (br != null)
                br.close();
        }
        return quiet;
    }
}
