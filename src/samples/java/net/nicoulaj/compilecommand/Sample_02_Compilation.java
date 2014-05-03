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

import net.nicoulaj.compilecommand.annotations.CompileOnly;
import net.nicoulaj.compilecommand.annotations.Exclude;

public class Sample_02_Compilation {

    // This sample demonstrates how to force or prevent JIT from compiling a method.
    //
    // To run:
    //   $ mvn clean test
    //   $ java -cp target/test-classes \
    //          -XX:+UnlockDiagnosticVMOptions \
    //          -XX:+PrintCompilation \
    //          -XX:CompileCommandFile=src/samples/java/net/nicoulaj/compilecommand/Sample_02_Compilation \
    //          net.nicoulaj.compilecommand.Sample_02_Compilation

    public static void main(String... args) {
        for (int i = 0; i < 100_000; i++) {
            method01();
            method02();
        }
    }

    @Exclude // Exclude the specified method from just in time compilation.
    private static void method01() {
    }

    @CompileOnly // Exclude all methods from just in time compilation except for the specified method.
    private static void method02() {
    }
}
