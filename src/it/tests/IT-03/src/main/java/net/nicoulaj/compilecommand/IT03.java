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

import net.nicoulaj.compilecommand.annotations.*;

@Quiet
public class IT03 {

    @Exclude
    public IT03() {
    }

    public static void main(String... args) {
        for (int i = 0; i < 100000; i++) {
            final IT03 it = new IT03();
            it.method01();
            it.method02();
        }
    }

    @Print
    @Log
    @Options({
        "UseSuperWord",
        "PrintIntrinsics",
        "EliminateAutoBox"
    })
    private void method01() {
    }

    @Option("UseSuperWord")
    private void method02() {
    }
}
