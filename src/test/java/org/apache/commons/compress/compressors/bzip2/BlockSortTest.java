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
package org.apache.commons.compress.compressors.bzip2;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.evosuite.runtime.EvoAssertions;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class BlockSortTest {

    private static class DS {
        private final BZip2CompressorOutputStream.Data data;
        private final BlockSort s;
        DS(final BZip2CompressorOutputStream.Data data, final BlockSort s) {
            this.data = data;
            this.s = s;
        }
    }

    /*
      Burrows-Wheeler transform of fixture the manual way:

      * build the matrix

      0, 1, 252, 253, 255, 254, 3, 2, 128
      1, 252, 253, 255, 254, 3, 2, 128, 0
      252, 253, 255, 254, 3, 2, 128, 0, 1
      253, 255, 254, 3, 2, 128, 0, 1, 252
      255, 254, 3, 2, 128, 0, 1, 252, 253
      254, 3, 2, 128, 0, 1, 252, 253, 255
      3, 2, 128, 0, 1, 252, 253, 255, 254
      2, 128, 0, 1, 252, 253, 255, 254, 3
      128, 0, 1, 252, 253, 255, 254, 3, 2

      * sort it

      0, 1, 252, 253, 255, 254, 3, 2, 128
      1, 252, 253, 255, 254, 3, 2, 128, 0
      2, 128, 0, 1, 252, 253, 255, 254, 3
      3, 2, 128, 0, 1, 252, 253, 255, 254
      128, 0, 1, 252, 253, 255, 254, 3, 2
      252, 253, 255, 254, 3, 2, 128, 0, 1
      253, 255, 254, 3, 2, 128, 0, 1, 252
      254, 3, 2, 128, 0, 1, 252, 253, 255
      255, 254, 3, 2, 128, 0, 1, 252, 253

      * grab last column

      128, 0, 3, 254, 2, 1, 252, 255, 253

        and the original line has been 0
    */

    private static final byte[] FIXTURE = { 0, 1, (byte) 252, (byte) 253, (byte) 255,
                                            (byte) 254, 3, 2, (byte) 128 };

    private static final byte[] FIXTURE_BWT = { (byte) 128, 0, 3, (byte) 254, 2, 1,
                                                (byte) 252, (byte) 255, (byte) 253 };

    private static final int[] FIXTURE_SORTED = {
        0, 1, 7, 6, 8, 2, 3, 5, 4
    };

    private static final byte[] FIXTURE2 = {
        'C', 'o', 'm', 'm', 'o', 'n', 's', ' ', 'C', 'o', 'm', 'p', 'r', 'e', 's', 's',
    };

    private static final byte[] FIXTURE2_BWT = {
        's', 's', ' ', 'r', 'o', 'm', 'o', 'o', 'C', 'C', 'm', 'm', 'p', 'n', 's', 'e',
    };

    private void assertFixture2Sorted(final BZip2CompressorOutputStream.Data data) {
        assertFixtureSorted(data, FIXTURE2, FIXTURE2_BWT);
    }

    private void assertFixtureSorted(final BZip2CompressorOutputStream.Data data) {
        assertFixtureSorted(data, FIXTURE, FIXTURE_BWT);
    }

    private void assertFixtureSorted(final BZip2CompressorOutputStream.Data data,
                                     final byte[] fixture, final byte[] fixtureBwt) {
        assertEquals(fixture[fixture.length - 1], data.block[0]);
        for (int i = 0; i < fixture.length; ++i) {
            assertEquals(fixtureBwt[i], data.block[data.fmap[i]]);
        }
    }

    private DS setUpFixture() {
        return setUpFixture(FIXTURE);
    }

    private DS setUpFixture(final byte[] fixture) {
        final BZip2CompressorOutputStream.Data data = new BZip2CompressorOutputStream.Data(1);
        System.arraycopy(fixture, 0, data.block, 1, fixture.length);
        return new DS(data, new BlockSort(data));
    }

    private DS setUpFixture2() {
        return setUpFixture(FIXTURE2);
    }

    @Test
    public void testFallbackSort() {
        final BZip2CompressorOutputStream.Data data = new BZip2CompressorOutputStream.Data(1);
        final BlockSort s = new BlockSort(data);
        final int[] fmap = new int[FIXTURE.length];
        s.fallbackSort(fmap, FIXTURE, FIXTURE.length);
        assertArrayEquals(FIXTURE_SORTED, fmap);
    }

    @Test
    public void testSortFixture() {
        final DS ds = setUpFixture();
        ds.s.blockSort(ds.data, FIXTURE.length - 1);
        assertFixtureSorted(ds.data);
        assertEquals(0, ds.data.origPtr);
    }

    @Test
    public void testSortFixture2() {
        final DS ds = setUpFixture2();
        ds.s.blockSort(ds.data, FIXTURE2.length - 1);
        assertFixture2Sorted(ds.data);
        assertEquals(1, ds.data.origPtr);
    }

    @Test
    public void testSortFixture2FallbackSort() {
        final DS ds = setUpFixture2();
        ds.s.fallbackSort(ds.data, FIXTURE2.length - 1);
        assertFixture2Sorted(ds.data);
    }

    @Test
    public void testSortFixture2MainSort() {
        final DS ds = setUpFixture2();
        ds.s.mainSort(ds.data, FIXTURE2.length - 1);
        assertFixture2Sorted(ds.data);
    }

    @Test
    public void testSortFixtureFallbackSort() {
        final DS ds = setUpFixture();
        ds.s.fallbackSort(ds.data, FIXTURE.length - 1);
        assertFixtureSorted(ds.data);
    }

    @Test
    public void testSortFixtureMainSort() {
        final DS ds = setUpFixture();
        ds.s.mainSort(ds.data, FIXTURE.length - 1);
        assertFixtureSorted(ds.data);
    }

    //Test generati da Evosuite

    @org.junit.Test(
            timeout = 4000L
    )
    public void test00() throws Throwable {
        BZip2CompressorOutputStream.Data var1 = new BZip2CompressorOutputStream.Data(3);
        BlockSort var2 = new BlockSort(var1);
        var2.blockSort(var1, 0);
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test01() throws Throwable {
        BZip2CompressorOutputStream.Data var1 = new BZip2CompressorOutputStream.Data(3);
        BlockSort var2 = new BlockSort(var1);
        var2.fallbackSort(var1, 5);
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test02() throws Throwable {
        BZip2CompressorOutputStream.Data var1 = new BZip2CompressorOutputStream.Data(18);
        BlockSort var2 = new BlockSort(var1);

        try {
            var2.mainSort((BZip2CompressorOutputStream.Data)null, 18);
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var4) {
            EvoAssertions.verifyException("org.apache.commons.compress.compressors.bzip2.BlockSort", var4);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test03() throws Throwable {
        BZip2CompressorOutputStream.Data var1 = new BZip2CompressorOutputStream.Data(3);
        BlockSort var2 = new BlockSort(var1);
        int[] var3 = new int[7];
        var2.fallbackSort(var3, new byte[7], 7);
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test04() throws Throwable {
        BZip2CompressorOutputStream.Data var1 = new BZip2CompressorOutputStream.Data(4);
        BlockSort var2 = new BlockSort(var1);
        int[] var3 = new int[4];
        byte[] var4 = new byte[0];

        try {
            var2.fallbackSort(var3, var4, -3298);
            Assert.fail("Expecting exception: NegativeArraySizeException");
        } catch (NegativeArraySizeException var6) {
            EvoAssertions.verifyException("java.util.BitSet", var6);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test05() throws Throwable {
        BZip2CompressorOutputStream.Data var1 = new BZip2CompressorOutputStream.Data(0);
        BlockSort var2 = new BlockSort(var1);
        byte[] var3 = new byte[9];

        try {
            var2.fallbackSort((int[])null, var3, -24);
            Assert.fail("Expecting exception: IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException var5) {
            EvoAssertions.verifyException("java.util.BitSet", var5);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test06() throws Throwable {
        BZip2CompressorOutputStream.Data var1 = new BZip2CompressorOutputStream.Data(3);
        BlockSort var2 = new BlockSort(var1);
        byte[] var3 = new byte[0];

        try {
            var2.fallbackSort((int[])null, var3, 9967);
            Assert.fail("Expecting exception: ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException var5) {
            EvoAssertions.verifyException("org.apache.commons.compress.compressors.bzip2.BlockSort", var5);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test07() throws Throwable {
        BZip2CompressorOutputStream.Data var1 = new BZip2CompressorOutputStream.Data(0);
        BlockSort var2 = new BlockSort(var1);

        try {
            var2.fallbackSort(var1, 0);
            Assert.fail("Expecting exception: ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException var4) {
            EvoAssertions.verifyException("org.apache.commons.compress.compressors.bzip2.BlockSort", var4);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test08() throws Throwable {
        BZip2CompressorOutputStream.Data var1 = new BZip2CompressorOutputStream.Data(4);
        BlockSort var2 = new BlockSort(var1);
        var2.blockSort(var1, 9975);
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test09() throws Throwable {
        BZip2CompressorOutputStream.Data var1 = new BZip2CompressorOutputStream.Data(18);
        BlockSort var2 = new BlockSort(var1);

        try {
            var2.blockSort((BZip2CompressorOutputStream.Data)null, 50);
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var4) {
            EvoAssertions.verifyException("org.apache.commons.compress.compressors.bzip2.BlockSort", var4);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test10() throws Throwable {
        Object var1 = null;

        try {
            new BlockSort((BZip2CompressorOutputStream.Data)null);
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var3) {
            EvoAssertions.verifyException("org.apache.commons.compress.compressors.bzip2.BlockSort", var3);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test11() throws Throwable {
        BZip2CompressorOutputStream.Data var1 = new BZip2CompressorOutputStream.Data(0);
        BlockSort var2 = new BlockSort(var1);
        int[] var3 = new int[0];
        byte[] var4 = new byte[0];
        var2.fallbackSort(var3, var4, 0);

        try {
            var2.blockSort(var1, 2);
            Assert.fail("Expecting exception: ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException var6) {
            EvoAssertions.verifyException("org.apache.commons.compress.compressors.bzip2.BlockSort", var6);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test12() throws Throwable {
        BZip2CompressorOutputStream.Data var1 = new BZip2CompressorOutputStream.Data(3);
        BlockSort var2 = new BlockSort(var1);
        var2.mainSort(var1, 200);
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test13() throws Throwable {
        BZip2CompressorOutputStream.Data var1 = new BZip2CompressorOutputStream.Data(4);
        BlockSort var2 = new BlockSort(var1);

        try {
            var2.mainSort(var1, -2);
            Assert.fail("Expecting exception: ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException var4) {
            EvoAssertions.verifyException("org.apache.commons.compress.compressors.bzip2.BlockSort", var4);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test14() throws Throwable {
        BZip2CompressorOutputStream.Data var1 = new BZip2CompressorOutputStream.Data(3);
        BlockSort var2 = new BlockSort(var1);
        var2.blockSort(var1, 3);
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test15() throws Throwable {
        BZip2CompressorOutputStream.Data var1 = new BZip2CompressorOutputStream.Data(4);
        BlockSort var2 = new BlockSort(var1);
        var2.fallbackSort(var1, 1469);
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test16() throws Throwable {
        BZip2CompressorOutputStream.Data var1 = new BZip2CompressorOutputStream.Data(4);
        BlockSort var2 = new BlockSort(var1);
        var2.blockSort(var1, 4);
        var2.fallbackSort(var1, 1469);
    }
}