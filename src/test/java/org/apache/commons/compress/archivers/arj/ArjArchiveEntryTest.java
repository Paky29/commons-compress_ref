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
package org.apache.commons.compress.archivers.arj;

import org.apache.commons.compress.AbstractTestCase;
import org.evosuite.runtime.EvoAssertions;
import org.junit.Assert;
import org.junit.Test;

public class ArjArchiveEntryTest extends AbstractTestCase {
    @Test(
            timeout = 4000L
    )
    public void test00() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        var1.hostOS = 10;
        var2.getLastModifiedDate();
        Assert.assertEquals(10L, (long)var2.getHostOs());
    }

    @Test(
            timeout = 4000L
    )
    public void test01() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        var1.fileType = 3459;
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        boolean var3 = var2.isDirectory();
        Assert.assertFalse(var3);
    }

    @Test(
            timeout = 4000L
    )
    public void test02() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        var1.hostOS = 3;
        var1.dateTimeModified = 10;
        var1.hostOS = 8;
        var2.getLastModifiedDate();
        Assert.assertEquals(8L, (long)var2.getHostOs());
    }

    @Test(
            timeout = 4000L
    )
    public void test03() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        var1.hostOS = 3;
        var1.hostOS = 8;
        var1.fileAccessMode = 8;
        int var3 = var2.getUnixMode();
        Assert.assertEquals(8L, (long)var2.getMode());
        Assert.assertEquals(8L, (long)var3);
    }

    @Test(
            timeout = 4000L
    )
    public void test04() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        var1.originalSize = 1009L;
        long var3 = var2.getSize();
        Assert.assertEquals(1009L, var3);
    }

    @Test(
            timeout = 4000L
    )
    public void test05() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        var1.originalSize = -90L;
        long var3 = var2.getSize();
        Assert.assertEquals(-90L, var3);
    }

    @Test(
            timeout = 4000L
    )
    public void test06() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        var1.name = "\")ZB";
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        String var3 = var2.getName();
        Assert.assertEquals("\")ZB", var3);
    }

    @Test(
            timeout = 4000L
    )
    public void test07() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        var1.name = "";
        String var3 = var2.getName();
        Assert.assertEquals("", var3);
    }

    @Test(
            timeout = 4000L
    )
    public void test08() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        var1.fileAccessMode = 476;
        int var3 = var2.getMode();
        Assert.assertEquals(476L, (long)var3);
    }

    @Test(
            timeout = 4000L
    )
    public void test09() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        var1.fileAccessMode = -1222;
        int var3 = var2.getMode();
        Assert.assertEquals(-1222L, (long)var3);
    }

    @Test(
            timeout = 4000L
    )
    public void test10() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        var1.method = 78;
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        int var3 = var2.getMethod();
        Assert.assertEquals(78L, (long)var3);
    }

    @Test(
            timeout = 4000L
    )
    public void test11() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        var1.method = -2096;
        int var3 = var2.getMethod();
        Assert.assertEquals(-2096L, (long)var3);
    }

    @Test(
            timeout = 4000L
    )
    public void test12() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        var1.hostOS = 10;
        int var3 = var2.getHostOs();
        Assert.assertEquals(10L, (long)var3);
    }

    @Test(
            timeout = 4000L
    )
    public void test13() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        var1.hostOS = -962;
        int var3 = var2.getHostOs();
        Assert.assertEquals(-962L, (long)var3);
    }

    @Test(
            timeout = 4000L
    )
    public void test14() throws Throwable {
        ArjArchiveEntry var1 = new ArjArchiveEntry((LocalFileHeader)null);

        try {
            var1.isHostOsUnix();
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var3) {
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.arj.ArjArchiveEntry", var3);
        }

    }

    @Test(
            timeout = 4000L
    )
    public void test15() throws Throwable {
        ArjArchiveEntry var1 = new ArjArchiveEntry((LocalFileHeader)null);

        try {
            var1.isDirectory();
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var3) {
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.arj.ArjArchiveEntry", var3);
        }

    }

    @Test(
            timeout = 4000L
    )
    public void test16() throws Throwable {
        ArjArchiveEntry var1 = new ArjArchiveEntry((LocalFileHeader)null);

        try {
            var1.getUnixMode();
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var3) {
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.arj.ArjArchiveEntry", var3);
        }

    }

    @Test(
            timeout = 4000L
    )
    public void test17() throws Throwable {
        ArjArchiveEntry var1 = new ArjArchiveEntry((LocalFileHeader)null);

        try {
            var1.getSize();
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var3) {
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.arj.ArjArchiveEntry", var3);
        }

    }

    @Test(
            timeout = 4000L
    )
    public void test18() throws Throwable {
        ArjArchiveEntry var1 = new ArjArchiveEntry((LocalFileHeader)null);

        try {
            var1.getMode();
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var3) {
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.arj.ArjArchiveEntry", var3);
        }

    }

    @Test(
            timeout = 4000L
    )
    public void test19() throws Throwable {
        ArjArchiveEntry var1 = new ArjArchiveEntry((LocalFileHeader)null);

        try {
            var1.getLastModifiedDate();
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var3) {
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.arj.ArjArchiveEntry", var3);
        }

    }

    @Test(
            timeout = 4000L
    )
    public void test20() throws Throwable {
        ArjArchiveEntry var1 = new ArjArchiveEntry((LocalFileHeader)null);
        ArjArchiveEntry var2 = new ArjArchiveEntry((LocalFileHeader)null);

        try {
            var1.equals(var2);
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var4) {
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.arj.ArjArchiveEntry", var4);
        }

    }

    @Test(
            timeout = 4000L
    )
    public void test21() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        var1.hostOS = 3;
        var1.hostOS = 2;
        boolean var3 = var2.isHostOsUnix();
        Assert.assertEquals(2L, (long)var2.getHostOs());
        Assert.assertTrue(var3);
    }

    @Test(
            timeout = 4000L
    )
    public void test22() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        boolean var3 = var2.isHostOsUnix();
        Assert.assertFalse(var3);
        Assert.assertEquals(0L, (long)var2.getHostOs());
    }

    @Test(
            timeout = 4000L
    )
    public void test23() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        var1.hostOS = 3;
        var1.hostOS = 8;
        boolean var3 = var2.isHostOsUnix();
        Assert.assertEquals(8L, (long)var2.getHostOs());
        Assert.assertTrue(var3);
    }

    @Test(
            timeout = 4000L
    )
    public void test24() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        var1.arjFlags = -129;
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);

        try {
            var2.getName();
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var4) {
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.arj.ArjArchiveEntry", var4);
        }

    }

    @Test(
            timeout = 4000L
    )
    public void test25() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        int var3 = var2.getHostOs();
        Assert.assertEquals(0L, (long)var3);
    }

    @Test(
            timeout = 4000L
    )
    public void test26() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        int var3 = var2.getMode();
        Assert.assertEquals(0L, (long)var3);
    }

    @Test(
            timeout = 4000L
    )
    public void test27() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        Assert.assertFalse(var2.isDirectory());
        var1.fileType = 3;
        boolean var3 = var2.isDirectory();
        Assert.assertTrue(var3);
    }

    @Test(
            timeout = 4000L
    )
    public void test28() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        boolean var3 = var2.isDirectory();
        Assert.assertFalse(var3);
    }

    @Test(
            timeout = 4000L
    )
    public void test29() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        var2.hashCode();
    }

    @Test(
            timeout = 4000L
    )
    public void test30() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        var1.name = "";
        var2.hashCode();
    }

    @Test(
            timeout = 4000L
    )
    public void test31() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        int var3 = var2.getUnixMode();
        Assert.assertEquals(0L, (long)var2.getHostOs());
        Assert.assertFalse(var2.isHostOsUnix());
        Assert.assertEquals(0L, (long)var3);
    }

    @Test(
            timeout = 4000L
    )
    public void test32() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        var1.arjFlags = -3338;

        try {
            var2.hashCode();
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var4) {
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.arj.ArjArchiveEntry", var4);
        }

    }

    @Test(
            timeout = 4000L
    )
    public void test33() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        String var3 = var2.getName();
        Assert.assertNull(var3);
    }

    @Test(
            timeout = 4000L
    )
    public void test34() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        ArjArchiveEntry var3 = new ArjArchiveEntry();
        boolean var4 = var2.equals(var3);
        Assert.assertTrue(var4);
    }

    @Test(
            timeout = 4000L
    )
    public void test35() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        boolean var3 = var2.equals((Object)null);
        Assert.assertFalse(var3);
    }

    @Test(
            timeout = 4000L
    )
    public void test36() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        Object var3 = new Object();
        boolean var4 = var2.equals(var3);
        Assert.assertFalse(var4);
    }

    @Test(
            timeout = 4000L
    )
    public void test37() throws Throwable {
        new ArjArchiveEntry.HostOs();
        Assert.assertEquals(6L, 6L);
    }

    @Test(
            timeout = 4000L
    )
    public void test38() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        long var3 = var2.getSize();
        Assert.assertEquals(0L, var3);
    }

    @Test(
            timeout = 4000L
    )
    public void test39() throws Throwable {
        LocalFileHeader var1 = new LocalFileHeader();
        ArjArchiveEntry var2 = new ArjArchiveEntry(var1);
        int var3 = var2.getMethod();
        Assert.assertEquals(0L, (long)var3);
    }

    @Test(
            timeout = 4000L
    )
    public void test40() throws Throwable {
        ArjArchiveEntry var1 = new ArjArchiveEntry();
        boolean var2 = var1.equals(var1);
        Assert.assertTrue(var2);
    }
}
