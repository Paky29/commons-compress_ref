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
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorRoundtripTest;
import org.openjdk.jmh.annotations.*;
import java.io.IOException;

@State(Scope.Benchmark)
public class FramedLZ4CompressorRoundtripBenchmark {
    private FramedLZ4CompressorRoundtripTest lz4test = new FramedLZ4CompressorRoundtripTest();

    @Param({"true","false"})
    private boolean withContentChecksum, withBlockChecksum, withBlockDependency;
    @Param({"K64","K256","M1","M4"})
    private FramedLZ4CompressorOutputStream.BlockSize blockSize;

    public FramedLZ4CompressorRoundtripBenchmark() {

    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void firstTestCase() {
        try {
            lz4test.biggerFileRoundtrip(new FramedLZ4CompressorOutputStream.Parameters(blockSize,withContentChecksum,withBlockChecksum,withBlockDependency));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void secondTestCase() {
        try {
            lz4test.blaTarRoundtrip(new FramedLZ4CompressorOutputStream.Parameters(blockSize,withContentChecksum,withBlockChecksum,withBlockDependency));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void thridTestCase() {
        try {
            lz4test.gzippedLoremIpsumRoundtrip(new FramedLZ4CompressorOutputStream.Parameters(blockSize,withContentChecksum,withBlockChecksum,withBlockDependency));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
