/*
 * Sonar Cryptography Plugin
 * Copyright (C) 2024 PQCA
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.plugin.rules.detection.bc;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

public class BouncyCastleJars {
    // From https://www.bouncycastle.org/latest_releases.html
    public static Collection<File> bcprov178Jar =
            Collections.singletonList(
                    new File("src/test/resources/test-jars/bcprov-jdk18on-1.78.1.jar"));
    public static Collection<File> bcprov180Jar =
            Collections.singletonList(
                    new File("src/test/resources/test-jars/bcprov-jdk18on-1.80.jar"));

    public static Collection<File> latestJar = bcprov180Jar;
}
