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
package com.ibm.plugin.rules.detection;

import com.ibm.engine.rule.IDetectionRule;
import com.ibm.plugin.rules.detection.bc.BouncyCastleDetectionRules;
import com.ibm.plugin.rules.detection.jca.JcaDetectionRules;
import com.ibm.plugin.rules.detection.ssl.SSLDetectionRules;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.sonar.plugins.java.api.tree.Tree;

public final class JavaDetectionRules {
    private JavaDetectionRules() {
        // private
    }

    @Nonnull
    public static List<IDetectionRule<Tree>> rules() {
        return Stream.of(
                        JcaDetectionRules.rules().stream(),
                        BouncyCastleDetectionRules.rules().stream(),
                        SSLDetectionRules.rules().stream())
                .flatMap(i -> i)
                .toList();
    }
}
