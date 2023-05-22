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
package org.apache.commons.compress.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;

import org.evosuite.runtime.EvoAssertions;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for class {@link ServiceLoaderIterator org.apache.commons.compress.utils.ServiceLoaderIterator}.
 *
 * @see ServiceLoaderIterator
 */
public class ServiceLoaderIteratorTest {

    @Test
    public void testHasNextReturnsFalse() {
        final Class<Object> clasz = Object.class;
        final ServiceLoaderIterator<Object> serviceLoaderIterator = new ServiceLoaderIterator<>(clasz);
        final boolean result = serviceLoaderIterator.hasNext();
        assertFalse(result);
    }

    @Test
    public void testNextThrowsNoSuchElementException() {
        final Class<String> clasz = String.class;
        final ServiceLoaderIterator<String> serviceLoaderIterator = new ServiceLoaderIterator<>(clasz);
        assertThrows(NoSuchElementException.class, () -> serviceLoaderIterator.next());
    }

    @Test
    public void testRemoveThrowsUnsupportedOperationException() {
        final Class<Integer> clasz = Integer.class;
        final ServiceLoaderIterator<Integer> serviceLoaderIterator = new ServiceLoaderIterator<>(clasz);
        assertThrows(UnsupportedOperationException.class, () -> serviceLoaderIterator.remove());
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test0() throws Throwable {
        Class var1 = Object.class;
        ClassLoader var2 = ClassLoader.getSystemClassLoader();
        new ServiceLoaderIterator(var1, var2);
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test1() throws Throwable {
        ClassLoader var1 = ClassLoader.getSystemClassLoader();
        Object var2 = null;

        try {
            new ServiceLoaderIterator((Class)null, var1);
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var4) {
            EvoAssertions.verifyException("java.util.Objects", var4);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test2() throws Throwable {
        Object var1 = null;

        try {
            new ServiceLoaderIterator((Class)null);
            Assert.fail("Expecting exception: NullPointerException");
        } catch (NullPointerException var3) {
            EvoAssertions.verifyException("java.util.Objects", var3);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test3() throws Throwable {
        Class var1 = Object.class;
        ServiceLoaderIterator var2 = new ServiceLoaderIterator(var1);
        boolean var3 = var2.hasNext();
        Assert.assertFalse(var3);
    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test4() throws Throwable {
        Class var1 = SecurityException.class;
        ServiceLoaderIterator var2 = new ServiceLoaderIterator(var1);

        try {
            var2.remove();
            Assert.fail("Expecting exception: UnsupportedOperationException");
        } catch (UnsupportedOperationException var4) {
            EvoAssertions.verifyException("org.apache.commons.compress.utils.ServiceLoaderIterator", var4);
        }

    }

    @org.junit.Test(
            timeout = 4000L
    )
    public void test5() throws Throwable {
        Class var1 = Object.class;
        ServiceLoaderIterator var2 = new ServiceLoaderIterator(var1);

        try {
            var2.next();
            Assert.fail("Expecting exception: NoSuchElementException");
        } catch (NoSuchElementException var4) {
            EvoAssertions.verifyException("org.apache.commons.compress.utils.ServiceLoaderIterator", var4);
        }

    }

}
