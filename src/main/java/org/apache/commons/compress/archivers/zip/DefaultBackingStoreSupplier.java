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

package org.apache.commons.compress.archivers.zip;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.compress.parallel.FileBasedScatterGatherBackingStore;
import org.apache.commons.compress.parallel.ScatterGatherBackingStore;
import org.apache.commons.compress.parallel.ScatterGatherBackingStoreSupplier;
import org.apache.commons.lang3.SystemUtils;

/**
 * Implements {@link ScatterGatherBackingStoreSupplier} using a temporary folder.
 * <p>
 * For example:
 * </p>
 * <pre>
 * final Path dir = Paths.get("target/custom-temp-dir");
 * Files.createDirectories(dir);
 * final ParallelScatterZipCreator zipCreator = new ParallelScatterZipCreator(
 *     Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()),
 *     new DefaultBackingStoreSupplier(dir));
 * </pre>
 *
 * @since 1.23
 */
public class DefaultBackingStoreSupplier implements ScatterGatherBackingStoreSupplier {

    private static final String PREFIX = "parallelscatter";

    private final AtomicInteger storeNum = new AtomicInteger();

    private final Path dir;
    /**
     * Constructs a new instance. If {@code dir} is null, then use the default temporary-file directory.
     *
     * @param dir temporary folder, may be null, must exist if non-null.
     */
    public DefaultBackingStoreSupplier(final Path dir) {
        this.dir = dir;
    }

    @Override
    public ScatterGatherBackingStore get() throws IOException {
        final String suffix = "n" + storeNum.incrementAndGet();
        final Path tempFile;

        if(SystemUtils.IS_OS_UNIX) {
            FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------"));
            tempFile = dir == null ? Files.createTempFile(PREFIX, suffix, attr) : Files.createTempFile(dir, PREFIX, suffix, attr);
        }
        else {
            File f = dir == null ? Files.createTempFile(PREFIX, suffix).toFile() : Files.createTempFile(dir, PREFIX, suffix).toFile();
            f.setReadable(true, true);
            f.setWritable(true, true);
            f.setExecutable(true, true);
            tempFile = f.toPath();
        }
        return new FileBasedScatterGatherBackingStore(tempFile);
    }
}