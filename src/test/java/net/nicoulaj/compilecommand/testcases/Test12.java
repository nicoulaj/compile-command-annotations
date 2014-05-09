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
package net.nicoulaj.compilecommand.testcases;

import net.nicoulaj.compilecommand.annotations.Inline;

public class Test12 {

    @Inline
    private void method01(boolean arg1,
                          short arg2,
                          int arg3,
                          long arg4,
                          float arg5,
                          double arg6,
                          char arg7,
                          byte arg8) {
    }
}
