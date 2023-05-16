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

final class NID {
    public static final int K_END = 0x00;
    public static final int K_HEADER = 0x01;
    public static final int K_ARCHIVE_PROPERTIES = 0x02;
    public static final int K_ADDITIONAL_STREAMS_INFO = 0x03;
    public static final int K_MAIN_STREAMS_INFO = 0x04;
    public static final int K_FILES_INFO = 0x05;
    public static final int K_PACK_INFO = 0x06;
    public static final int K_UNPACK_INFO = 0x07;
    public static final int K_SUB_STREAMS_INFO = 0x08;
    public static final int K_SIZE = 0x09;
    public static final int K_CRC = 0x0A;
    public static final int K_FOLDER = 0x0B;
    public static final int K_CODERS_UNPACK_SIZE = 0x0C;
    public static final int K_NUM_UNPACK_STREAM = 0x0D;
    public static final int K_EMPTY_STREAM = 0x0E;
    public static final int K_EMPTY_FILE = 0x0F;
    public static final int K_ANTI = 0x10;
    public static final int K_NAME = 0x11;
    public static final int K_C_TIME = 0x12;
    public static final int K_A_TIME = 0x13;
    public static final int K_M_TIME = 0x14;
    public static final int K_WIN_ATTRIBUTES = 0x15;
    public static final int K_COMMENT = 0x16;
    public static final int K_ENCODED_HEADER = 0x17;
    public static final int K_START_POS = 0x18;
    public static final int K_DUMMY = 0x19;
}
