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

import net.nicoulaj.compilecommand.annotations.Option;

public class CompileCommandAnnotations_04_Options {

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
    //          -XX:CompileCommandFile=src/samples/java/net/nicoulaj/compilecommand/CompileCommandAnnotations_04_Options \
    //          net.nicoulaj.compilecommand.CompileCommandAnnotations_04_Options

    public static void main(String... args) {
    }

    @Option("UseSuperWord=true")
    @Option("PrintIntrinsics=true")
    @Option("EliminateAutoBox=true")
    private static void method01() {
    }
}
