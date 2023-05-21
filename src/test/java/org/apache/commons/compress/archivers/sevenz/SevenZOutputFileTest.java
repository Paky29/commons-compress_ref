/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.compress.archivers.sevenz;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.*;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.*;

import org.apache.commons.compress.AbstractTestCase;
import org.apache.commons.compress.PasswordRequiredException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.dump.DumpArchiveEntry;
import org.apache.commons.compress.utils.ByteUtils;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.compress.utils.TimeUtils;
import org.evosuite.runtime.EvoAssertions;
import org.evosuite.runtime.mock.java.io.MockFile;
import org.evosuite.runtime.mock.java.io.MockFileInputStream;
import org.evosuite.runtime.mock.java.io.MockFileOutputStream;
import org.evosuite.runtime.testdata.EvoSuiteFile;
import org.evosuite.runtime.testdata.FileSystemHandling;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.tukaani.xz.LZMA2Options;

public class SevenZOutputFileTest extends AbstractTestCase {

    private static final boolean XZ_BCJ_IS_BUGGY;

    static {
        final String version = org.tukaani.xz.XZ.class.getPackage().getImplementationVersion();

        XZ_BCJ_IS_BUGGY= version != null && version.equals("1.4");
        if (XZ_BCJ_IS_BUGGY) {
            System.out.println("XZ version is " + version + " - skipping BCJ tests");
        }
    }
    private static void assertContentMethodsEquals(final Iterable<? extends SevenZMethodConfiguration> expected,
                                                   final Iterable<? extends SevenZMethodConfiguration> actual) {
        assertNotNull(actual);
        final Iterator<? extends SevenZMethodConfiguration> actualIter = actual.iterator();
        for (final SevenZMethodConfiguration expConfig : expected) {
            assertTrue(actualIter.hasNext());
            final SevenZMethodConfiguration actConfig = actualIter.next();
            assertEquals(expConfig.getMethod(), actConfig.getMethod());
        }
        assertFalse(actualIter.hasNext());
    }

    private File output;

    private void addDir(final SevenZOutputFile archive) throws Exception {
        final SevenZArchiveEntry entry = archive.createArchiveEntry(dir, "foo/");
        archive.putArchiveEntry(entry);
        archive.closeArchiveEntry();
    }

    private void addFile(final SevenZOutputFile archive, final int index, final boolean nonEmpty)
            throws Exception {
        addFile(archive, index, nonEmpty, null);
    }

    private void addFile(final SevenZOutputFile archive, final int index, final boolean nonEmpty, final Iterable<SevenZMethodConfiguration> methods)
            throws Exception {
        addFile(archive, index, nonEmpty ? 1 : 0, methods);
    }

    private void addFile(final SevenZOutputFile archive, final int index, final int size, final Iterable<SevenZMethodConfiguration> methods)
            throws Exception {
        final SevenZArchiveEntry entry = new SevenZArchiveEntry();
        entry.setName("foo/" + index + ".txt");
        entry.setContentMethods(methods);
        archive.putArchiveEntry(entry);
        archive.write(generateFileData(size));
        archive.closeArchiveEntry();
    }

    private void createAndReadBack(final File output, final Iterable<SevenZMethodConfiguration> methods) throws Exception {
        try (final SevenZOutputFile outArchive = new SevenZOutputFile(output)) {
            outArchive.setContentMethods(methods);
            addFile(outArchive, 0, true);
        }

        try (SevenZFile archive = new SevenZFile(output)) {
            assertEquals(Boolean.TRUE, verifyFile(archive, 0, methods));
        }
    }

    private void createAndReadBack(final SeekableInMemoryByteChannel output, final Iterable<SevenZMethodConfiguration> methods) throws Exception {
        try (final SevenZOutputFile outArchive = new SevenZOutputFile(output)) {
            outArchive.setContentMethods(methods);
            addFile(outArchive, 0, true);
        }
        try (SevenZFile archive =
                     new SevenZFile(new SeekableInMemoryByteChannel(output.array()), "in memory")) {
            assertEquals(Boolean.TRUE, verifyFile(archive, 0, methods));
        }
    }

    private byte[] generateFileData(final int size) {
        final byte[] data = new byte[size];
        for (int i = 0; i < size; ++i) {
            data[i] = (byte) ('A' + (i % 26));
        }
        return data;
    }

    private FileTime getHundredNanosFileTime() {
        final Instant now = Instant.now();
        // In some platforms, Java's Instant has a precision of milliseconds.
        // Add some nanos at the end to test 100ns intervals.
        final FileTime fileTime = FileTime.from(Instant.ofEpochSecond(now.getEpochSecond(), now.getNano() + 999900));
        // However, in some platforms, Java's Instant has a precision of nanoseconds.
        // Truncate the resulting FileTime to 100ns intervals.
        return TimeUtils.truncateToHundredNanos(fileTime);
    }

    @Override
    public void tearDown() throws Exception {
        if (output != null && !output.delete()) {
            output.deleteOnExit();
        }
        super.tearDown();
    }

    @Test
    public void testArchiveWithMixedMethods() throws Exception {
        output = new File(dir, "mixed-methods.7z");
        try (SevenZOutputFile outArchive = new SevenZOutputFile(output)) {
            addFile(outArchive, 0, true);
            addFile(outArchive, 1, true, Arrays.asList(new SevenZMethodConfiguration(SevenZMethod.BZIP2)));
        }

        try (SevenZFile archive = new SevenZFile(output)) {
            assertEquals(Boolean.TRUE,
                    verifyFile(archive, 0, Arrays.asList(new SevenZMethodConfiguration(SevenZMethod.LZMA2))));
            assertEquals(Boolean.TRUE,
                    verifyFile(archive, 1, Arrays.asList(new SevenZMethodConfiguration(SevenZMethod.BZIP2))));
        }
    }

    @Test
    public void testBCJARMRoundtrip() throws Exception {
        if (XZ_BCJ_IS_BUGGY) { return; }
        testFilterRoundTrip(new SevenZMethodConfiguration(SevenZMethod.BCJ_ARM_FILTER));
    }

    @Test
    public void testBCJARMThumbRoundtrip() throws Exception {
        if (XZ_BCJ_IS_BUGGY) { return; }
        testFilterRoundTrip(new SevenZMethodConfiguration(SevenZMethod.BCJ_ARM_THUMB_FILTER));
    }

    @Test
    public void testBCJIA64Roundtrip() throws Exception {
        if (XZ_BCJ_IS_BUGGY) { return; }
        testFilterRoundTrip(new SevenZMethodConfiguration(SevenZMethod.BCJ_IA64_FILTER));
    }

    @Test
    public void testBCJPPCRoundtrip() throws Exception {
        if (XZ_BCJ_IS_BUGGY) { return; }
        testFilterRoundTrip(new SevenZMethodConfiguration(SevenZMethod.BCJ_PPC_FILTER));
    }

    @Test
    public void testBCJSparcRoundtrip() throws Exception {
        if (XZ_BCJ_IS_BUGGY) { return; }
        testFilterRoundTrip(new SevenZMethodConfiguration(SevenZMethod.BCJ_SPARC_FILTER));
    }

    @Test
    public void testBCJX86Roundtrip() throws Exception {
        if (XZ_BCJ_IS_BUGGY) { return; }
        testFilterRoundTrip(new SevenZMethodConfiguration(SevenZMethod.BCJ_X86_FILTER));
    }

    @Test
    public void testBzip2Roundtrip() throws Exception {
        testRoundTrip(SevenZMethod.BZIP2);
    }

    @Test
    public void testBzip2WithConfiguration() throws Exception {
        output = new File(dir, "bzip2-options.7z");
        // 400k block size
        createAndReadBack(output, Collections
                .singletonList(new SevenZMethodConfiguration(SevenZMethod.BZIP2, 4)));
    }

    @Test
    public void testCantFinishTwice() throws IOException {
        output = new File(dir, "finish.7z");
        try (SevenZOutputFile outArchive = new SevenZOutputFile(output)) {
            outArchive.finish();
            final IOException ex = assertThrows(IOException.class, outArchive::finish,
                    "shouldn't be able to call finish twice");
            assertEquals("This archive has already been finished", ex.getMessage());
        }
    }

    private void testCompress252(final int numberOfFiles, final int numberOfNonEmptyFiles)
            throws Exception {
        final int nonEmptyModulus = numberOfNonEmptyFiles != 0
                ? numberOfFiles / numberOfNonEmptyFiles
                : numberOfFiles + 1;
        int nonEmptyFilesAdded = 0;
        output = new File(dir, "COMPRESS252-" + numberOfFiles + "-" + numberOfNonEmptyFiles + ".7z");
        try (SevenZOutputFile archive = new SevenZOutputFile(output)) {
            addDir(archive);
            for (int i = 0; i < numberOfFiles; ++i) {
                addFile(archive, i,
                        (i + 1) % nonEmptyModulus == 0 && nonEmptyFilesAdded++ < numberOfNonEmptyFiles);
            }
        }
        verifyCompress252(output, numberOfFiles, numberOfNonEmptyFiles);
    }

    @Test
    public void testCopyRoundtrip() throws Exception {
        testRoundTrip(SevenZMethod.COPY);
    }

    @Test
    public void testDeflateRoundtrip() throws Exception {
        testRoundTrip(SevenZMethod.DEFLATE);
    }

    @Test
    public void testDeflateWithConfiguration() throws Exception {
        output = new File(dir, "deflate-options.7z");
        // Deflater.BEST_SPEED
        createAndReadBack(output, Collections
                .singletonList(new SevenZMethodConfiguration(SevenZMethod.DEFLATE, 1)));
    }

    @Test
    public void testDeltaRoundtrip() throws Exception {
        testFilterRoundTrip(new SevenZMethodConfiguration(SevenZMethod.DELTA_FILTER));
    }

    @Test
    public void testDirectoriesAndEmptyFiles() throws Exception {
        output = new File(dir, "empties.7z");

        final FileTime accessTime = getHundredNanosFileTime();
        final Date accessDate = new Date(accessTime.toMillis());
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -1);
        final Date creationDate = cal.getTime();

        try (SevenZOutputFile outArchive = new SevenZOutputFile(output)) {
            SevenZArchiveEntry entry = outArchive.createArchiveEntry(dir, "foo/");
            outArchive.putArchiveEntry(entry);
            outArchive.closeArchiveEntry();

            entry = new SevenZArchiveEntry();
            entry.setName("foo/bar");
            entry.setCreationDate(creationDate);
            entry.setAccessTime(accessTime);
            outArchive.putArchiveEntry(entry);
            outArchive.write(ByteUtils.EMPTY_BYTE_ARRAY);
            outArchive.closeArchiveEntry();

            entry = new SevenZArchiveEntry();
            entry.setName("foo/bar/boo0");
            entry.setCreationDate(creationDate);
            entry.setAccessTime(accessTime);
            outArchive.putArchiveEntry(entry);
            outArchive.write(new ByteArrayInputStream(ByteUtils.EMPTY_BYTE_ARRAY));
            outArchive.closeArchiveEntry();

            entry = new SevenZArchiveEntry();
            entry.setName("foo/bar/boo1");
            entry.setCreationDate(creationDate);
            entry.setAccessTime(accessTime);
            outArchive.putArchiveEntry(entry);
            outArchive.write(new ByteArrayInputStream(new byte[] {'a'}));
            outArchive.closeArchiveEntry();

            entry = new SevenZArchiveEntry();
            entry.setName("foo/bar/boo10000");
            entry.setCreationDate(creationDate);
            entry.setAccessTime(accessTime);
            outArchive.putArchiveEntry(entry);
            outArchive.write(new ByteArrayInputStream(new byte[10000]));
            outArchive.closeArchiveEntry();

            entry = new SevenZArchiveEntry();
            entry.setName("foo/bar/test.txt");
            entry.setCreationDate(creationDate);
            entry.setAccessTime(accessTime);
            outArchive.putArchiveEntry(entry);
            outArchive.write(Paths.get("src/test/resources/test.txt"));
            outArchive.closeArchiveEntry();

            entry = new SevenZArchiveEntry();
            entry.setName("xyzzy");
            outArchive.putArchiveEntry(entry);
            outArchive.write(0);
            outArchive.closeArchiveEntry();

            entry = outArchive.createArchiveEntry(dir, "baz/");
            entry.setAntiItem(true);
            outArchive.putArchiveEntry(entry);
            outArchive.closeArchiveEntry();

            entry = outArchive.createArchiveEntry(dir.toPath(), "baz2/");
            entry.setAntiItem(true);
            outArchive.putArchiveEntry(entry);
            outArchive.closeArchiveEntry();

            entry = new SevenZArchiveEntry();
            entry.setName("dada");
            entry.setHasWindowsAttributes(true);
            entry.setWindowsAttributes(17);
            outArchive.putArchiveEntry(entry);
            outArchive.write(5);
            outArchive.write(42);
            outArchive.closeArchiveEntry();

            outArchive.finish();
        }

        try (SevenZFile archive = new SevenZFile(output)) {
            SevenZArchiveEntry entry = archive.getNextEntry();
            assert (entry != null);
            assertEquals("foo/", entry.getName());
            assertTrue(entry.isDirectory());
            assertFalse(entry.isAntiItem());

            entry = archive.getNextEntry();
            assert (entry != null);
            assertEquals("foo/bar", entry.getName());
            assertFalse(entry.isDirectory());
            assertFalse(entry.isAntiItem());
            assertEquals(0, entry.getSize());
            assertFalse(entry.getHasLastModifiedDate());
            assertEquals(accessTime, entry.getAccessTime());
            assertEquals(accessDate, entry.getAccessDate());
            assertEquals(creationDate, entry.getCreationDate());

            entry = archive.getNextEntry();
            assert (entry != null);
            assertEquals("foo/bar/boo0", entry.getName());
            assertFalse(entry.isDirectory());
            assertFalse(entry.isAntiItem());
            assertEquals(0, entry.getSize());
            assertFalse(entry.getHasLastModifiedDate());
            assertEquals(accessTime, entry.getAccessTime());
            assertEquals(accessDate, entry.getAccessDate());
            assertEquals(creationDate, entry.getCreationDate());

            entry = archive.getNextEntry();
            assert (entry != null);
            assertEquals("foo/bar/boo1", entry.getName());
            assertFalse(entry.isDirectory());
            assertFalse(entry.isAntiItem());
            assertEquals(1, entry.getSize());
            assertFalse(entry.getHasLastModifiedDate());
            assertEquals(accessTime, entry.getAccessTime());
            assertEquals(accessDate, entry.getAccessDate());
            assertEquals(creationDate, entry.getCreationDate());

            entry = archive.getNextEntry();
            assert (entry != null);
            assertEquals("foo/bar/boo10000", entry.getName());
            assertFalse(entry.isDirectory());
            assertFalse(entry.isAntiItem());
            assertEquals(10000, entry.getSize());
            assertFalse(entry.getHasLastModifiedDate());
            assertEquals(accessTime, entry.getAccessTime());
            assertEquals(accessDate, entry.getAccessDate());
            assertEquals(creationDate, entry.getCreationDate());

            entry = archive.getNextEntry();
            assert (entry != null);
            assertEquals("foo/bar/test.txt", entry.getName());
            assertFalse(entry.isDirectory());
            assertFalse(entry.isAntiItem());
            assertEquals(Files.size(Paths.get("src/test/resources/test.txt")), entry.getSize());
            assertFalse(entry.getHasLastModifiedDate());
            assertEquals(accessTime, entry.getAccessTime());
            assertEquals(accessDate, entry.getAccessDate());
            assertEquals(creationDate, entry.getCreationDate());

            entry = archive.getNextEntry();
            assert (entry != null);
            assertEquals("xyzzy", entry.getName());
            assertEquals(1, entry.getSize());
            assertFalse(entry.getHasAccessDate());
            assertFalse(entry.getHasCreationDate());
            assertEquals(0, archive.read());

            entry = archive.getNextEntry();
            assert (entry != null);
            assertEquals("baz/", entry.getName());
            assertTrue(entry.isDirectory());
            assertTrue(entry.isAntiItem());

            entry = archive.getNextEntry();
            assert (entry != null);
            assertEquals("baz2/", entry.getName());
            assertTrue(entry.isDirectory());
            assertTrue(entry.isAntiItem());

            entry = archive.getNextEntry();
            assert (entry != null);
            assertEquals("dada", entry.getName());
            assertEquals(2, entry.getSize());
            final byte[] content = new byte[2];
            assertEquals(2, archive.read(content));
            assertEquals(5, content[0]);
            assertEquals(42, content[1]);
            assertEquals(17, entry.getWindowsAttributes());

            assert (archive.getNextEntry() == null);
        }

    }

    @Test
    public void testDirectoriesOnly() throws Exception {
        output = new File(dir, "dirs.7z");
        try (SevenZOutputFile outArchive = new SevenZOutputFile(output)) {
            final SevenZArchiveEntry entry = new SevenZArchiveEntry();
            entry.setName("foo/");
            entry.setDirectory(true);
            outArchive.putArchiveEntry(entry);
            outArchive.closeArchiveEntry();
        }

        try (SevenZFile archive = new SevenZFile(output)) {
            final SevenZArchiveEntry entry = archive.getNextEntry();
            assert (entry != null);
            assertEquals("foo/", entry.getName());
            assertTrue(entry.isDirectory());
            assertFalse(entry.isAntiItem());

            assert (archive.getNextEntry() == null);
        }

    }

    @Test
    public void testEightEmptyFiles() throws Exception {
        testCompress252(8, 0);
    }

    @Test
    public void testEightFilesSomeNotEmpty() throws Exception {
        testCompress252(8, 2);
    }

    /**
     * Test password-based encryption
     *
     * <p>
     * As AES/CBC Cipher requires a minimum of 16 bytes file data to be encrypted, some padding logic has been implemented.
     * This test checks different file sizes (1, 16..) to ensure code coverage
     * </p>
     */
    @Test
    public void testEncrypt() throws Exception {
        output = new File(dir, "encrypted.7z");
        try (SevenZOutputFile outArchive = new SevenZOutputFile(output, "foo".toCharArray())) {
            addFile(outArchive, 0, 1, null);
            addFile(outArchive, 1, 16, null);
            addFile(outArchive, 2, 32, null);
            addFile(outArchive, 3, 33, null);
            addFile(outArchive, 4, 10000, null);
        }

        // Is archive really password-based encrypted ?
        try (SevenZFile archive = new SevenZFile(output)) {
            assertThrows(

                    PasswordRequiredException.class,
                    () -> verifyFile(archive, 0), "A password should be needed");
        }

        try (SevenZFile archive = new SevenZFile(output, "foo".toCharArray())) {
            assertEquals(Boolean.TRUE, verifyFile(archive, 0, 1, null));
            assertEquals(Boolean.TRUE, verifyFile(archive, 1, 16, null));
            assertEquals(Boolean.TRUE, verifyFile(archive, 2, 32, null));
            assertEquals(Boolean.TRUE, verifyFile(archive, 3, 33, null));
            assertEquals(Boolean.TRUE, verifyFile(archive, 4, 10000, null));
        }
    }

    private void testFilterRoundTrip(final SevenZMethodConfiguration method) throws Exception {
        output = new File(dir, method.getMethod() + "-roundtrip.7z");
        final ArrayList<SevenZMethodConfiguration> methods = new ArrayList<>();
        methods.add(method);
        methods.add(new SevenZMethodConfiguration(SevenZMethod.LZMA2));
        createAndReadBack(output, methods);
    }

    @Test
    public void testLzma2Roundtrip() throws Exception {
        testRoundTrip(SevenZMethod.LZMA2);
    }

    @Test
    public void testLzma2WithIntConfiguration() throws Exception {
        output = new File(dir, "lzma2-options.7z");
        // 1 MB dictionary
        createAndReadBack(output, Collections
                .singletonList(new SevenZMethodConfiguration(SevenZMethod.LZMA2, 1 << 20)));
    }

    @Test
    public void testLzma2WithOptionsConfiguration() throws Exception {
        output = new File(dir, "lzma2-options2.7z");
        final LZMA2Options opts = new LZMA2Options(1);
        createAndReadBack(output, Collections
                .singletonList(new SevenZMethodConfiguration(SevenZMethod.LZMA2, opts)));
    }

    @Test
    public void testLzmaWithIntConfiguration() throws Exception {
        output = new File(dir, "lzma-options.7z");
        // 1 MB dictionary
        createAndReadBack(output, Collections
                .singletonList(new SevenZMethodConfiguration(SevenZMethod.LZMA, 1 << 20)));
    }

    @Test
    public void testLzmaWithOptionsConfiguration() throws Exception {
        output = new File(dir, "lzma-options2.7z");
        final LZMA2Options opts = new LZMA2Options(1);
        createAndReadBack(output, Collections
                .singletonList(new SevenZMethodConfiguration(SevenZMethod.LZMA, opts)));
    }

    @Test
    public void testNineEmptyFiles() throws Exception {
        testCompress252(9, 0);
    }

    @Test
    public void testNineFilesSomeNotEmpty() throws Exception {
        testCompress252(9, 2);
    }

    private void testRoundTrip(final SevenZMethod method) throws Exception {
        output = new File(dir, method + "-roundtrip.7z");
        final ArrayList<SevenZMethodConfiguration> methods = new ArrayList<>();
        methods.add(new SevenZMethodConfiguration(method));
        createAndReadBack(output, methods);
    }

    @Test
    public void testSevenEmptyFiles() throws Exception {
        testCompress252(7, 0);
    }

    @Test
    public void testSevenFilesSomeNotEmpty() throws Exception {
        testCompress252(7, 2);
    }

    @Test
    public void testSixEmptyFiles() throws Exception {
        testCompress252(6, 0);
    }

    @Test
    public void testSixFilesSomeNotEmpty() throws Exception {
        testCompress252(6, 2);
    }

    @Test
    public void testStackOfContentCompressions() throws Exception {
        output = new File(dir, "multiple-methods.7z");
        final ArrayList<SevenZMethodConfiguration> methods = new ArrayList<>();
        methods.add(new SevenZMethodConfiguration(SevenZMethod.LZMA2));
        methods.add(new SevenZMethodConfiguration(SevenZMethod.COPY));
        methods.add(new SevenZMethodConfiguration(SevenZMethod.DEFLATE));
        methods.add(new SevenZMethodConfiguration(SevenZMethod.BZIP2));
        createAndReadBack(output, methods);
    }

    @Test
    public void testStackOfContentCompressionsInMemory() throws Exception {
        final ArrayList<SevenZMethodConfiguration> methods = new ArrayList<>();
        methods.add(new SevenZMethodConfiguration(SevenZMethod.LZMA2));
        methods.add(new SevenZMethodConfiguration(SevenZMethod.COPY));
        methods.add(new SevenZMethodConfiguration(SevenZMethod.DEFLATE));
        methods.add(new SevenZMethodConfiguration(SevenZMethod.BZIP2));
        createAndReadBack(new SeekableInMemoryByteChannel(), methods);
    }

    @Test
    public void testTwentyNineEmptyFiles() throws Exception {
        testCompress252(29, 0);
    }

    @Test
    public void testTwentyNineFilesSomeNotEmpty() throws Exception {
        testCompress252(29, 7);
    }

    private void verifyCompress252(final File output, final int numberOfFiles, final int numberOfNonEmptyFiles)
            throws Exception {
        int filesFound = 0;
        int nonEmptyFilesFound = 0;
        try (SevenZFile archive = new SevenZFile(output)) {
            verifyDir(archive);
            Boolean b = verifyFile(archive, filesFound++);
            while (b != null) {
                if (Boolean.TRUE.equals(b)) {
                    nonEmptyFilesFound++;
                }
                b = verifyFile(archive, filesFound++);
            }
        }
        assertEquals(numberOfFiles + 1, filesFound);
        assertEquals(numberOfNonEmptyFiles, nonEmptyFilesFound);
    }

    private void verifyDir(final SevenZFile archive) throws Exception {
        final SevenZArchiveEntry entry = archive.getNextEntry();
        assertNotNull(entry);
        assertEquals("foo/", entry.getName());
        assertTrue(entry.isDirectory());
    }

    private Boolean verifyFile(final SevenZFile archive, final int index) throws Exception {
        return verifyFile(archive, index, null);
    }

    private Boolean verifyFile(final SevenZFile archive, final int index, final int size,
                               final Iterable<SevenZMethodConfiguration> methods) throws Exception {
        final SevenZArchiveEntry entry = archive.getNextEntry();
        if (entry == null) {
            return null;
        }
        assertEquals("foo/" + index + ".txt", entry.getName());
        assertFalse(entry.isDirectory());
        if (entry.getSize() == 0) {
            return Boolean.FALSE;
        }
        assertEquals(size, entry.getSize());

        final byte[] actual = new byte[size];
        int count = 0;
        while (count < size) {
            final int read = archive.read(actual, count, actual.length - count);
            assertNotEquals(-1, read, "EOF reached before reading all expected data");
            count += read;
        }
        assertArrayEquals(generateFileData(size), actual);
        assertEquals(-1, archive.read());
        if (methods != null) {
            assertContentMethodsEquals(methods, entry.getContentMethods());
        }
        return Boolean.TRUE;
    }

    private Boolean verifyFile(final SevenZFile archive, final int index,
                               final Iterable<SevenZMethodConfiguration> methods) throws Exception {
        return verifyFile(archive, index, 1, methods);
    }

    //Test generati con Evosuite
    @org.junit.Test(
            timeout = 4000L
    )
    public void test00() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(", ", true);
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        HashSet var4 = new HashSet();
        var3.setContentMethods(var4);
        Assert.assertEquals(0, var2.position());
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test01() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream("org.apache.commons.compress.archivers.sevenz.SevenZOutputFile$1", false);
        FileChannel var2 = var1.getChannel();
        new SevenZOutputFile(var2, (char[])null);
        Assert.assertEquals(32L, var2.position());
        var1.close();
        var2.close();
        File file = new File("org.apache.commons.compress.archivers.sevenz.SevenZOutputFile$1");
        file.delete();
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test02() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(".$_G!OMb");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        SevenZArchiveEntry var4 = new SevenZArchiveEntry();
        var3.putArchiveEntry(var4);
        var3.closeArchiveEntry();
        Assert.assertEquals(32L, var2.position());
        var1.close();
        var2.close();
        File file = new File(".$_G!OMb");
        file.delete();
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test03() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream("test03s7o", false);
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        MockFile var4 = new MockFile("");
        Path var5 = var4.toPath();
        LinkOption[] var6 = new LinkOption[1];
        LinkOption var7 = LinkOption.NOFOLLOW_LINKS;
        var6[0] = var7;
        var3.createArchiveEntry(var5, "test03s7o", var6);
        Assert.assertEquals(32L, var2.position());
        var1.close();
        var2.close();
        File file = new File("test03s7o");
        file.delete();
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test04() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream("R92pre92", true);
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        MockFile var4 = new MockFile("", "");
        var3.createArchiveEntry(var4, "org.apache.commons.compress.archivers.sevenz.SevenZOutputFile");
        Assert.assertEquals(0, var2.position());
        var1.close();
        var2.close();
        File file = new File("R92pre92");
        file.delete();
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test05() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream("test05s7o");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        var3.putArchiveEntry((ArchiveEntry)null);

        try {
            var3.write((byte[])null, 45, 1101);
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var5) {
            var1.close();
            var2.close();
            File file = new File("test05s7o");
            file.delete();
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.sevenz.SevenZOutputFile", var5);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test07() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(".U");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        SevenZArchiveEntry var4 = new SevenZArchiveEntry();
        var3.putArchiveEntry(var4);
        byte[] var5 = new byte[2];

        try {
            var3.write(var5);
            Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (Throwable var7) {
            var1.close();
            var2.close();
            File file = new File(".U");
            file.delete();
            assertTrue(true);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test08() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(".H_GOMP");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        MockFile var4 = new MockFile(".H_GOMP");
        Path var5 = var4.toPath();
        OpenOption[] var6 = new OpenOption[0];

        try {
            var3.write(var5, var6);
            Assert.fail("Expecting exception: NoSuchFileException");
        } catch (Throwable var8) {
            var1.close();
            var2.close();
            File file = new File(".H_GOMP");
            file.delete();
            assertTrue(true);
        }

    }

    /*
    @org.junit.Test(
            timeout = 4000L
    )
    public void test09() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream("RC2re2", true);
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        MockFile var4 = new MockFile("");
        Path var5 = var4.toPath();
        OpenOption[] var6 = new OpenOption[1];
        StandardOpenOption var7 = StandardOpenOption.SPARSE;
        var6[0] = var7;

        try {
            var3.write(var5, var6);
            Assert.fail("Expecting exception: AccessDeniedException");
        } catch (AccessDeniedException var9) {
            var1.close();
            var2.close();
            File file = new File("RC2re2");
            file.delete();
            assertTrue(true);
        }

    }*/

    @org.junit.Test(
            timeout = 4000L
    )
    public void test10() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream("R92pre92", true);
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        File var4 = MockFile.createTempFile("This archive has already been finished", "This archive has already been finished");
        Path var5 = var4.toPath();
        OpenOption[] var6 = new OpenOption[1];
        StandardOpenOption var7 = StandardOpenOption.WRITE;
        var6[0] = var7;

        try {
            var3.write(var5, var6);
            Assert.fail("Expecting exception: UnsupportedOperationException");
        } catch (UnsupportedOperationException var9) {
            var1.close();
            var2.close();
            File file = new File("R92pre92");
            file.delete();
            EvoAssertions.verifyException("java.nio.file.spi.FileSystemProvider", var9);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test11() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(",EC9");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);

        try {
            var3.write((InputStream)null);
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var5) {
            var1.close();
            var2.close();
            File file = new File(",EC9");
            file.delete();
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.sevenz.SevenZOutputFile", var5);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test12() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream("HGOMP");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        SevenZArchiveEntry var4 = new SevenZArchiveEntry();
        var3.putArchiveEntry(var4);
        byte[] var5 = new byte[2];
        ByteArrayInputStream var6 = new ByteArrayInputStream(var5);

        try {
            var3.write(var6);
            Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (Throwable var8) {
            var1.close();
            var2.close();
            File file = new File("HGOMP");
            file.delete();
            assertTrue(true);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test13() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(".$_G!OMb");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        byte[] var4 = new byte[0];
        ByteArrayInputStream var5 = new ByteArrayInputStream(var4, -2842, 1167);

        try {
            var3.write(var5);
            Assert.fail("Expecting exception: ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException var7) {
            var1.close();
            var2.close();
            File file = new File(".$_G!OMb");
            file.delete();
            EvoAssertions.verifyException("java.io.ByteArrayInputStream", var7);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test14() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(".H_GOMP");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        PipedInputStream var4 = new PipedInputStream();

        try {
            var3.write(var4);
            Assert.fail("Expecting exception: IOException");
        } catch (IOException var6) {
            var1.close();
            var2.close();
            File file = new File(".H_GOMP");
            file.delete();
            EvoAssertions.verifyException("java.io.PipedInputStream", var6);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test15() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(".H_GOMP");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        var3.putArchiveEntry((ArchiveEntry)null);

        try {
            var3.write(-277496552);
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var5) {
            var1.close();
            var2.close();
            File file = new File(".H_GOMP");
            file.delete();
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.sevenz.SevenZOutputFile", var5);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test16() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream("HGOMP");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        DumpArchiveEntry var4 = new DumpArchiveEntry();

        try {
            var3.putArchiveEntry(var4);
            Assert.fail("Expecting exception: ClassCastException");
        } catch (ClassCastException var6) {
            var1.close();
            var2.close();
            File file = new File("HGOMP");
            file.delete();
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.sevenz.SevenZOutputFile", var6);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test17() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream("org.apache.commons.compress.archivers.sevenz.SevenZOutputFile", false);
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        SevenZOutputFile var4 = new SevenZOutputFile(var2);
        var4.close();

        try {
            var3.finish();
            Assert.fail("Expecting exception: ClosedChannelException");
        } catch (ClosedChannelException var6) {
            var1.close();
            var2.close();
            File file = new File("org.apache.commons.compress.archivers.sevenz.SevenZOutputFile");
            file.delete();
            assertTrue(true);
        }

    }

    /*@org.junit.Test(
            timeout = 4000L
    )
    public void test18() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(".H_GOMP");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        MockFile var4 = new MockFile(".H_GOMP", ".H_GOMP");
        Path var5 = var4.toPath();
        LinkOption[] var6 = new LinkOption[1];
        LinkOption var7 = LinkOption.NOFOLLOW_LINKS;
        var6[0] = var7;

        try {
            var3.createArchiveEntry(var5, ".H_GOMP", var6);
            Assert.fail("Expecting exception: NoSuchFileException");
        } catch (NoSuchFileException var9) {
            var1.close();
            var2.close();
            File file = new File(".H_GOMP");
            file.delete();
            assertTrue(true);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test19() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream("org.apache.commons.compress.utils.IOUtils", true);
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        MockFile var4 = new MockFile("~R~*`!DI.>S6YV=!(1", "~R~*`!DI.>S6YV=!(1");

        try {
            var3.createArchiveEntry(var4, "~R~*`!DI.>S6YV=!(1");
            Assert.fail("Expecting exception: InvalidPathException");
        } catch (InvalidPathException var6) {
            var1.close();
            var2.close();
            File file = new File("org.apache.commons.compress.utils.IOUtils");
            file.delete();
            assertTrue(true);
        }

    }*/

    @org.junit.Test(
            timeout = 4000L
    )
    public void test20() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream("R92pre92", true);
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);

        try {
            var3.createArchiveEntry((File)null, "R92pre92");
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var5) {
            var1.close();
            var2.close();
            File file = new File("R92pre92");
            file.delete();
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.sevenz.SevenZOutputFile", var5);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test22() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(".H_GOMP");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        var1.close();

        try {
            var3.close();
            Assert.fail("Expecting exception: ClosedChannelException");
        } catch (ClosedChannelException var5) {
            var1.close();
            var2.close();
            File file = new File(".H_GOMP");
            file.delete();
            assertTrue(true);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test23() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(".H_GOMP");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        FileSystemHandling.shouldAllThrowIOExceptions();

        try {
            var3.close();
            Assert.fail("Expecting exception: IOException");
        } catch (Throwable var5) {
            var1.close();
            var2.close();
            File file = new File(".H_GOMP");
            file.delete();
            assertTrue(true);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test24() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream("R92pre92", true);
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        var3.close();
        Object var4 = null;

        try {
            new SevenZOutputFile(var2, (char[])null);
            Assert.fail("Expecting exception: ClosedChannelException");
        } catch (Throwable var6) {
            var1.close();
            var2.close();
            File file = new File("R92pre92");
            file.delete();
            assertTrue(true);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test25() throws Throwable {
        char[] var1 = new char[4];
        Object var2 = null;

        try {
            new SevenZOutputFile((SeekableByteChannel)null, var1);
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var4) {
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.sevenz.SevenZOutputFile", var4);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test26() throws Throwable {
        FileSystemHandling.shouldAllThrowIOExceptions();
        MockFileOutputStream var1 = new MockFileOutputStream("RC2pre52", true);
        FileChannel var2 = var1.getChannel();
        char[] var3 = new char[0];
        Object var4 = null;

        try {
            new SevenZOutputFile(var2, var3);
            Assert.fail("Expecting exception: IOException");
        } catch (Throwable var6) {
            var1.close();
            var2.close();
            File file = new File("RC2pre52");
            file.delete();
            assertTrue(true);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test27() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(",EC9");
        FileChannel var2 = var1.getChannel();
        var2.close();
        Object var3 = null;

        try {
            new SevenZOutputFile(var2);
            Assert.fail("Expecting exception: ClosedChannelException");
        } catch (ClosedChannelException var5) {
            var1.close();
            var2.close();
            File file = new File(",EC9");
            file.delete();
            assertTrue(true);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test28() throws Throwable {
        Object var1 = null;

        try {
            new SevenZOutputFile((SeekableByteChannel)null);
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var3) {
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.sevenz.SevenZOutputFile", var3);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test29() throws Throwable {
        EvoSuiteFile var1 = new EvoSuiteFile(".H_GOP");
        FileSystemHandling.shouldThrowIOException(var1);
        MockFileOutputStream var2 = new MockFileOutputStream(".H_GOP");
        FileChannel var3 = var2.getChannel();
        Object var4 = null;

        try {
            new SevenZOutputFile(var3);
            Assert.fail("Expecting exception: IOException");
        } catch (Throwable var6) {
            var2.close();
            var3.close();
            File file = new File(".H_GOP");
            file.delete();
            assertTrue(true);
        }

    }

    /*
    @org.junit.Test(
            timeout = 4000L
    )
    public void test30() throws Throwable {
        MockFile var1 = new MockFile("0Y.6fgL]xD\" JUuD;G", "0Y.6fgL]xD\" JUuD;G");
        Object var2 = null;

        try {
            new SevenZOutputFile(var1, (char[])null);
            Assert.fail("Expecting exception: InvalidPathException");
        } catch (InvalidPathException var4) {
        }

    }*/

    @org.junit.Test(
            timeout = 4000L
    )
    public void test31() throws Throwable {
        MockFile var1 = new MockFile("s:b");
        char[] var2 = new char[0];
        Object var3 = null;

        try {
            new SevenZOutputFile(var1, var2);
            Assert.fail("Expecting exception: IOException");
        } catch (Throwable var5) {
            File file = new File("s:b");
            file.delete();
        }

    }

    /*
    @org.junit.Test(
            timeout = 4000L
    )
    public void test32() throws Throwable {
        MockFile var1 = new MockFile("_k\".2\u007fA6IMN^6HR@", "_k\".2\u007fA6IMN^6HR@");
        Object var2 = null;

        try {
            new SevenZOutputFile(var1);
            Assert.fail("Expecting exception: InvalidPathException");
        } catch (InvalidPathException var4) {
        }

    }*/

    @org.junit.Test(
            timeout = 4000L
    )
    public void test33() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(".$_G!OMb");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);

        try {
            var3.write((byte[])null, 5, 5);
            Assert.fail("Expecting exception: IllegalStateException");
        } catch (IllegalStateException var5) {
            var1.close();
            var2.close();
            File file = new File(".$_G!OMb");
            file.delete();
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.sevenz.SevenZOutputFile", var5);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test34() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(", ", true);
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        byte[] var4 = new byte[3];
        var3.write(var4, 111, -277496563);
        Assert.assertEquals(0, var2.position());
        var1.close();
        var2.close();
        File file = new File(", ");
        file.delete();
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test35() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(".H_GOMP");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        var3.finish();
        Assert.assertEquals(32L, var2.position());
        Assert.assertEquals(44L, var2.size());
        var1.close();
        var2.close();
        File file = new File(".H_GOMP");
        file.delete();
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test36() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(", =C");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);

        try {
            var3.setContentMethods((Iterable)null);
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var5) {
            var1.close();
            var2.close();
            File file = new File(", =C");
            file.delete();
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.sevenz.SevenZOutputFile", var5);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test37() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(".H_GOMP");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        SevenZArchiveEntry var4 = new SevenZArchiveEntry();
        var3.putArchiveEntry(var4);

        try {
            var3.close();
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var6) {
            var1.close();
            var2.close();
            File file = new File(".H_GOMP");
            file.delete();
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.sevenz.SevenZOutputFile", var6);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test38() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream("org.ap]che.commons.comwress.archivers.zip.ZipEightByteInteger");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        byte[] var4 = new byte[10];
        ByteArrayInputStream var5 = new ByteArrayInputStream(var4);
        BufferedInputStream var6 = new BufferedInputStream(var5);

        try {
            var3.write(var6);
            Assert.fail("Expecting exception: IllegalStateException");
        } catch (IllegalStateException var8) {
            var1.close();
            var2.close();
            File file = new File("org.ap]che.commons.comwress.archivers.zip.ZipEightByteInteger");
            file.delete();
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.sevenz.SevenZOutputFile", var8);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test39() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream("HGOMP");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        MockFileInputStream var4 = new MockFileInputStream("HGOMP");
        var3.write(var4);
        Assert.assertEquals(32L, var2.position());
        var1.close();
        var2.close();
        var4.close();
        File file = new File("HGOMP");
        file.delete();
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test40() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(".H_GOMP");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        byte[] var4 = new byte[9];

        try {
            var3.write(var4);
            Assert.fail("Expecting exception: IllegalStateException");
        } catch (IllegalStateException var6) {
            var1.close();
            var2.close();
            File file = new File(".H_GOMP");
            file.delete();
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.sevenz.SevenZOutputFile", var6);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test41() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(".H_GOMP");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        byte[] var4 = new byte[0];
        var3.write(var4);
        Assert.assertEquals(32L, var2.position());
        var1.close();
        var2.close();
        File file = new File(".H_GOMP");
        file.delete();
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test42() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(".H_GOMP");
        FileChannel var2 = var1.getChannel();
        SevenZArchiveEntry var3 = new SevenZArchiveEntry();
        SevenZOutputFile var4 = new SevenZOutputFile(var2);
        var4.putArchiveEntry(var3);

        try {
            var4.write(-1);
            Assert.fail("Expecting exception: NoClassDefFoundError");
        } catch (Throwable var6) {
            var1.close();
            var2.close();
            File file = new File(".H_GOMP");
            file.delete();
            assertTrue(true);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test43() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream("R92pre92", true);
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        var3.close();

        try {
            var3.finish();
            Assert.fail("Expecting exception: IOException");
        } catch (IOException var5) {
            var1.close();
            var2.close();
            File file = new File("R92pre92");
            file.delete();
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.sevenz.SevenZOutputFile", var5);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test44() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(".H_GOMP");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);

        try {
            var3.closeArchiveEntry();
            Assert.fail("Expecting exception: IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException var5) {
            var1.close();
            var2.close();
            File file = new File(".H_GOMP");
            file.delete();
            assertTrue(true);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test45() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream("org.apache.commons.compress.archivers.zip.X0017_StrongEncryptionHeader");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        var3.close();
        var3.close();
        var1.close();
        var2.close();
        File file = new File("org.apache.commons.compress.archivers.zip.X0017_StrongEncryptionHeader");
        file.delete();
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test46() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream("No current 7z entry", true);
        FileChannel var2 = var1.getChannel();
        char[] var3 = new char[0];
        new SevenZOutputFile(var2, var3);
        var1.close();
        var2.close();
        File file = new File("No current 7z entry");
        file.delete();
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test47() throws Throwable {
        Object var1 = null;

        try {
            new SevenZOutputFile((File)null, (char[])null);
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var3) {
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.sevenz.SevenZOutputFile", var3);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test48() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(".H]_GOMP");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);

        try {
            var3.write(-277496552);
            Assert.fail("Expecting exception: IllegalStateException");
        } catch (IllegalStateException var5) {
            var1.close();
            var2.close();
            File file = new File(".H]_GOMP");
            file.delete();
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.sevenz.SevenZOutputFile", var5);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test49() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(".H_GOMP");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        SevenZMethod var4 = SevenZMethod.DEFLATE64;
        var3.setContentCompression(var4);
        Assert.assertEquals(32L, var2.position());
        var1.close();
        var2.close();
        File file = new File(".H_GOMP");
        file.delete();
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test50() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(".H_GOMP");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        var3.putArchiveEntry((ArchiveEntry)null);

        try {
            var3.closeArchiveEntry();
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var5) {
            var1.close();
            var2.close();
            File file = new File(".H_GOMP");
            file.delete();
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.sevenz.SevenZOutputFile", var5);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test51() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(".H_GOMP");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        MockFile var4 = new MockFile(".H_GOMP", ".H_GOMP");
        Path var5 = var4.toPath();

        try {
            var3.write(var5, (OpenOption[])null);
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var7) {
            var1.close();
            var2.close();
            File file = new File(".H_GOMP");
            file.delete();
            EvoAssertions.verifyException("java.nio.file.spi.FileSystemProvider", var7);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test52() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(".$_G!OMb");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);

        try {
            var3.write((byte[])null);
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var5) {
            var1.close();
            var2.close();
            File file = new File(".$_G!OMb");
            file.delete();
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.sevenz.SevenZOutputFile", var5);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test53() throws Throwable {
        Object var1 = null;

        try {
            new SevenZOutputFile((File)null);
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var3) {
            EvoAssertions.verifyException("org.apache.commons.compress.archivers.sevenz.SevenZOutputFile", var3);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test54() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream(".H_GOMP");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        MockFile var4 = new MockFile(".H_GOMP", ".H_GOMP");
        Path var5 = var4.toPath();

        try {
            var3.createArchiveEntry(var5, "", (LinkOption[])null);
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var7) {
            var1.close();
            var2.close();
            File file = new File(".H_GOMP");
            file.delete();
            assertTrue(true);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test55() throws Throwable {
        MockFileOutputStream var1 = new MockFileOutputStream("GR92pr92");
        FileChannel var2 = var1.getChannel();
        SevenZOutputFile var3 = new SevenZOutputFile(var2);
        MockFile var4 = new MockFile("GR92pr92", "GR92pr92");
        var3.createArchiveEntry(var4, "GR92pr92");
        Assert.assertEquals(32L, var2.position());
        var1.close();
        var2.close();
        File file = new File("GR92pr92");
        file.delete();
    }
}
