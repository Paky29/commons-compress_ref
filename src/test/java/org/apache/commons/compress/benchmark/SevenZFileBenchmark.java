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
package org.apache.commons.compress.benchmark;

import org.apache.commons.compress.archivers.sevenz.SevenZFileTest;
import org.openjdk.jmh.annotations.*;
import java.io.IOException;

@State(Scope.Benchmark)
public class SevenZFileBenchmark {
    private SevenZFileTest sevenZFileTest = new SevenZFileTest();

    public SevenZFileBenchmark() {

    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void extractNonExistSpecifiedFile(){
        try {
            sevenZFileTest.extractNonExistSpecifiedFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void extractSpecifiedFile(){
        try {
            sevenZFileTest.extractSpecifiedFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void getDefaultNameWorksAsExpected(){
        try {
            sevenZFileTest.getDefaultNameWorksAsExpected();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void getEntriesOfUnarchiveInMemoryTest(){
        try {
            sevenZFileTest.getEntriesOfUnarchiveInMemoryTest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void getEntriesOfUnarchiveTest(){
        try {
            sevenZFileTest.getEntriesOfUnarchiveTest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void givenNameWinsOverDefaultName(){
        try {
            sevenZFileTest.givenNameWinsOverDefaultName();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void handlesEmptyArchiveWithFilesInfo(){
        try {
            sevenZFileTest.handlesEmptyArchiveWithFilesInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void handlesEmptyArchiveWithoutFilesInfo(){
        try {
            sevenZFileTest.handlesEmptyArchiveWithoutFilesInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void limitExtractionMemory(){
        sevenZFileTest.limitExtractionMemory();

    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void noNameCanBeReplacedByDefaultName(){
        try {
            sevenZFileTest.noNameCanBeReplacedByDefaultName();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void noNameMeansNoNameByDefault(){
        try {
            sevenZFileTest.noNameMeansNoNameByDefault();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void readBigSevenZipFile() {
        try {
            sevenZFileTest.readBigSevenZipFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void readEntriesOfSize0() {
        try {
            sevenZFileTest.readEntriesOfSize0();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void readTimesFromFile() {
        try {
            sevenZFileTest.readTimesFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void retrieveInputStreamForAllEntriesMultipleTimes() {
        try {
            sevenZFileTest.retrieveInputStreamForAllEntriesMultipleTimes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void retrieveInputStreamForAllEntriesWithoutCRCMultipleTimes(){
        try {
            sevenZFileTest.retrieveInputStreamForAllEntriesWithoutCRCMultipleTimes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void retrieveInputStreamForShuffledEntries(){
        try {
            sevenZFileTest.retrieveInputStreamForShuffledEntries();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void test7zDecryptUnarchive(){
        try {
            sevenZFileTest.test7zDecryptUnarchive();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void test7zDecryptUnarchiveUsingCharArrayPassword(){
        try {
            sevenZFileTest.test7zDecryptUnarchiveUsingCharArrayPassword();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void test7zDeflate64Unarchive(){
        try {
            sevenZFileTest.test7zDeflate64Unarchive();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void test7zDeflateUnarchive(){
        try {
            sevenZFileTest.test7zDeflate64Unarchive();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void test7zMultiVolumeUnarchive(){
        try {
            sevenZFileTest.test7zDeflate64Unarchive();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void test7zUnarchive(){
        try {
            sevenZFileTest.test7zUnarchive();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void test7zUnarchiveWithDefectHeader(){
        try {
            sevenZFileTest.test7zUnarchiveWithDefectHeader();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void test7zUnarchiveWithDefectHeaderFailsByDefault(){
        try {
            sevenZFileTest.test7zUnarchiveWithDefectHeaderFailsByDefault();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testAllEmptyFilesArchive(){
        try {
            sevenZFileTest.testAllEmptyFilesArchive();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testCompressedHeaderWithNonDefaultDictionarySize(){
        try {
            sevenZFileTest.testCompressedHeaderWithNonDefaultDictionarySize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testEncryptedArchiveRequiresPassword(){
        try {
            sevenZFileTest.testEncryptedArchiveRequiresPassword();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testHelloWorldHeaderCompressionOffCopy(){
        try {
            sevenZFileTest.testHelloWorldHeaderCompressionOffCopy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testHelloWorldHeaderCompressionOffLZMA2(){
        try {
            sevenZFileTest.testHelloWorldHeaderCompressionOffLZMA2();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testNoOOMOnCorruptedHeader(){
        try {
            sevenZFileTest.testNoOOMOnCorruptedHeader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testRandomAccessMultipleReadSameFile(){
        try {
            sevenZFileTest.testRandomAccessMultipleReadSameFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testRandomAccessTogetherWithSequentialAccess(){
        try {
            sevenZFileTest.testRandomAccessTogetherWithSequentialAccess();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testRandomAccessWhenJumpingBackwards(){
        try {
            sevenZFileTest.testRandomAccessWhenJumpingBackwards();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testRandomAccessWhenJumpingForwards(){
        try {
            sevenZFileTest.testRandomAccessWhenJumpingForwards();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testRandomlySkippingEntries(){
        try {
            sevenZFileTest.testRandomlySkippingEntries();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testReadingBackDeltaDistance(){
        try {
            sevenZFileTest.testReadingBackDeltaDistance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testReadingBackLZMA2DictSize(){
        try {
            sevenZFileTest.testReadingBackLZMA2DictSize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testSevenZWithEOS(){
        try {
            sevenZFileTest.testSevenZWithEOS();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testSignatureCheck(){
        sevenZFileTest.testSignatureCheck();

    }

}
