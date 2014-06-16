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

import net.nicoulaj.compilecommand.annotations.Options;

public class Sample_04_Options {

    // This sample demonstrates how to use compiler options.
    //
    // To list available options:
    //   $ java -XX:+UnlockDiagnosticVMOptions -XX:+PrintFlagsFinal -version | grep C2
    //
    // To run:
    //   $ mvn clean test
    //   $ java -cp target/test-classes \
    //          -XX:+UnlockDiagnosticVMOptions \
    //          -XX:+PrintCompilation \
    //          -XX:+LogCompilation \
    //          -XX:+PrintInlining \
    //          -XX:CompileCommandFile=src/samples/resources/net/nicoulaj/compilecommand/Sample_04_Options \
    //          net.nicoulaj.compilecommand.Sample_04_Options

    public static void main(String... args) {
    }

    @Options({"UseSuperWord=true",
              "PrintIntrinsics=true",
              "EliminateAutoBox=true"})
    private static void method01() {
    }
}
