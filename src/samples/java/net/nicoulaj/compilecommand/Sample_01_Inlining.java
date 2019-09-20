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

import net.nicoulaj.compilecommand.annotations.DontInline;
import net.nicoulaj.compilecommand.annotations.Inline;

public class Sample_01_Inlining {

    // This sample demonstrates how to force or prevent JIT from inlining a method.
    //
    // To run:
    //   $ mvn clean test
    //   $ java -cp target/test-classes \
    //          -XX:+UnlockDiagnosticVMOptions \
    //          -XX:+PrintInlining \
    //          -XX:CompileCommandFile=src/samples/resources/net/nicoulaj/compilecommand/Sample_01_Inlining \
    //          net.nicoulaj.compilecommand.Sample_01_Inlining

    public static void main(String... args) {
        for (int i = 0; i < 100000; i++) {
            method01();
            method02();
        }
    }

    @Inline // Force the just in time compiler to attempt inlining the specified method.
    private static void method01() {
    }

    @DontInline // Prevent the just in time compiler from inlining the specified method.
    private static void method02() {
    }
}
