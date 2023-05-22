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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.nio.charset.IllegalCharsetNameException;

import org.apache.commons.compress.AbstractTestCase;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.utils.IOUtils;
import org.evosuite.runtime.EvoAssertions;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class DumpArchiveInputStreamTest extends AbstractTestCase {

    @Test
    public void multiByteReadConsistentlyReturnsMinusOneAtEof() throws Exception {
        final byte[] buf = new byte[2];
        try (InputStream in = newInputStream("bla.dump");
             DumpArchiveInputStream archive = new DumpArchiveInputStream(in)) {
            final ArchiveEntry e = archive.getNextEntry();
            IOUtils.toByteArray(archive);
            assertEquals(-1, archive.read(buf));
            assertEquals(-1, archive.read(buf));
        }
    }

    @Test
    public void singleByteReadConsistentlyReturnsMinusOneAtEof() throws Exception {
        try (InputStream in = newInputStream("bla.dump");
             DumpArchiveInputStream archive = new DumpArchiveInputStream(in)) {
            final ArchiveEntry e = archive.getNextEntry();
            IOUtils.toByteArray(archive);
            assertEquals(-1, archive.read());
            assertEquals(-1, archive.read());
        }
    }

    @Test
    public void testConsumesArchiveCompletely() throws Exception {
        try (final InputStream is = DumpArchiveInputStreamTest.class.getResourceAsStream("/archive_with_trailer.dump");
                DumpArchiveInputStream dump = new DumpArchiveInputStream(is)) {
            while (dump.getNextDumpEntry() != null) {
                // just consume the archive
            }
            final byte[] expected = { 'H', 'e', 'l', 'l', 'o', ',', ' ', 'w', 'o', 'r', 'l', 'd', '!', '\n' };
            final byte[] actual = new byte[expected.length];
            is.read(actual);
            assertArrayEquals(expected, actual);
        }
    }

    @Test
    public void testNotADumpArchive() throws Exception {
        try (InputStream is = newInputStream("bla.zip")) {
            final ArchiveException ex = assertThrows(ArchiveException.class, () -> new DumpArchiveInputStream(is).close(),
                    "expected an exception");
            assertTrue(ex.getCause() instanceof ShortFileException);
        }
    }

    @Test
    public void testNotADumpArchiveButBigEnough() throws Exception {
        try (InputStream is = newInputStream("zip64support.tar.bz2")) {
            final ArchiveException ex = assertThrows(ArchiveException.class, () -> new DumpArchiveInputStream(is).close(),
                    "expected an exception");
            assertInstanceOf(UnrecognizedFormatException.class, ex.getCause());
        }
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test0() throws Throwable {
        byte[] var1 = new byte[7];

        try {
            DumpArchiveInputStream.matches(var1, 1024);
            Assert.fail("Expecting exception: ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException var3) {
            EvoAssertions.verifyException("org.apache.commons.compress.utils.ByteUtils", var3);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test1() throws Throwable {
        byte[] var1 = new byte[7];

        try {
            DumpArchiveInputStream.matches(var1, 32);
            Assert.fail("Expecting exception: ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException var3) {
            EvoAssertions.verifyException("org.apache.commons.compress.utils.ByteUtils", var3);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test2() throws Throwable {
        Object var1 = null;

        try {
            new DumpArchiveInputStream((InputStream)null, (String)null);
            Assert.fail("Expecting exception: Exception");
        } catch (Throwable var3) {
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.dump.DumpArchiveInputStream", var3);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test3() throws Throwable {
        Object var1 = null;

        try {
            new DumpArchiveInputStream((InputStream)null, "..");
            Assert.fail("Expecting exception: IllegalCharsetNameException");
        } catch (IllegalCharsetNameException var3) {
            EvoAssertions.verifyException("java.nio.charset.Charset", var3);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test4() throws Throwable {
        DataInputStream var1 = new DataInputStream((InputStream)null);
        Object var2 = null;

        try {
            new DumpArchiveInputStream(var1);
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var4) {
            EvoAssertions.verifyException("java.io.DataInputStream", var4);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test5() throws Throwable {
        byte[] var1 = new byte[4];

        try {
            DumpArchiveInputStream.matches(var1, 51966);
            Assert.fail("Expecting exception: ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException var3) {
            EvoAssertions.verifyException("org.apache.commons.compress.utils.ByteUtils", var3);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test6() throws Throwable {
        byte[] var1 = new byte[7];
        boolean var2 = DumpArchiveInputStream.matches(var1, -109);
        Assert.assertFalse(var2);
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test7() throws Throwable {
        try {
            DumpArchiveInputStream.matches((byte[])null, 164);
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var2) {
            EvoAssertions.verifyException("org.apache.commons.compress.utils.ByteUtils", var2);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test8() throws Throwable {
        PipedInputStream var1 = new PipedInputStream();
        Object var2 = null;

        try {
            new DumpArchiveInputStream(var1);
            Assert.fail("Expecting exception: Exception");
        } catch (Throwable var4) {
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.dump.DumpArchiveInputStream", var4);
        }

    }
}
