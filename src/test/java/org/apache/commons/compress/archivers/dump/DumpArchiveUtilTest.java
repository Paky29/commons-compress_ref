/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.commons.compress.archivers.dump;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.evosuite.runtime.EvoAssertions;
import org.evosuite.runtime.ViolatedAssumptionAnswer;
import org.evosuite.shaded.org.mockito.Mockito;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class DumpArchiveUtilTest {

    @Test
    public void convert16() {
        assertEquals(0xABCD,
                     DumpArchiveUtil.convert16(new byte[] {
                             (byte) 0xCD, (byte) 0xAB
                         }, 0));
    }

    @Test
    public void convert32() {
        assertEquals(0xABCDEF01,
                     DumpArchiveUtil.convert32(new byte[] {
                             1, (byte) 0xEF, (byte) 0xCD, (byte) 0xAB
                         }, 0));
    }

    @Test
    public void convert64() {
        assertEquals(0xABCDEF0123456780L,
                     DumpArchiveUtil.convert64(new byte[] {
                             (byte) 0x80, 0x67, 0x45, 0x23, 1, (byte) 0xEF,
                             (byte) 0xCD, (byte) 0xAB
                         }, 0));
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test1() throws Throwable {
        Object var1 = null;

        try {
            new DumpArchiveSummary((byte[])null, (ZipEncoding)null);
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var3) {
            EvoAssertions.verifyException("org.apache.commons.compress.utils.ByteUtils", var3);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test2() throws Throwable {
        byte[] var1 = new byte[16];
        Object var2 = null;

        try {
            new DumpArchiveSummary(var1, (ZipEncoding)null);
            Assert.fail("Expecting exception: ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException var4) {
        }

    }
}