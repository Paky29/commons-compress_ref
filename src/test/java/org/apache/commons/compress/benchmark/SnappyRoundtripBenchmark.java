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

import org.apache.commons.compress.compressors.snappy.SnappyRoundtripTest;
import org.openjdk.jmh.annotations.*;
import java.io.IOException;

@State(Scope.Benchmark)
public class SnappyRoundtripBenchmark {
    private SnappyRoundtripTest snappyRoundtripTest = new SnappyRoundtripTest();

    public SnappyRoundtripBenchmark() {

    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void fourthTestCase(){
        try {
            snappyRoundtripTest.biggerFileRoundtrip();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void fifthTestCase(){
        try {
            snappyRoundtripTest.blaTarRoundtrip();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void sixthTestCase(){
        try {
            snappyRoundtripTest.blaTarRoundtripTunedForCompressionRatio();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void seventhTestCase(){
        try {
            snappyRoundtripTest.blaTarRoundtripTunedForSpeed();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void eigthTestCase(){
        try {
            snappyRoundtripTest.gzippedLoremIpsumRoundtrip();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void ninethTestCase(){
        try {
            snappyRoundtripTest.tryReallyBigOffset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void tenthTestCase(){
        try {
            snappyRoundtripTest.tryReallyLongLiterals();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
