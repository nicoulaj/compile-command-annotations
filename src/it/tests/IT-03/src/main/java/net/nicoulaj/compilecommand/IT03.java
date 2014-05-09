/*
 * ====================================================================
 * Hotspot compile command annotations
 * ====================================================================
 * Copyright (C) 2014 Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * ====================================================================
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
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
        }
    }

    @Print
    @Log
    @Options({"UseSuperWord=true",
              "PrintIntrinsics=true",
              "EliminateAutoBox=true"})
    private void method01() {
    }
}
