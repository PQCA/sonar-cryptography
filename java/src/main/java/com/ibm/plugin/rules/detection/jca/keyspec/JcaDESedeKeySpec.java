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
package com.ibm.plugin.rules.detection.jca.keyspec;

import static com.ibm.plugin.rules.detection.TypeShortcuts.BYTE_ARRAY_TYPE;

import com.ibm.engine.model.context.KeyContext;
import com.ibm.engine.model.context.SecretKeyContext;
import com.ibm.engine.model.factory.KeySizeFactory;
import com.ibm.engine.rule.IDetectionRule;
import com.ibm.engine.rule.builder.DetectionRuleBuilder;
import java.util.List;
import javax.annotation.Nonnull;
import org.sonar.plugins.java.api.tree.Tree;

public final class JcaDESedeKeySpec {

    private static final IDetectionRule<Tree> DESede_KEY_SPEC_1 =
            new DetectionRuleBuilder<Tree>()
                    .createDetectionRule()
                    .forObjectTypes("javax.crypto.spec.DESedeKeySpec")
                    .forConstructor()
                    .withMethodParameter(BYTE_ARRAY_TYPE)
                    .shouldBeDetectedAs(new KeySizeFactory<>())
                    .buildForContext(new SecretKeyContext(KeyContext.Kind.DESede))
                    .inBundle(() -> "Jca")
                    .withoutDependingDetectionRules();

    private static final IDetectionRule<Tree> DESede_KEY_SPEC_2 =
            new DetectionRuleBuilder<Tree>()
                    .createDetectionRule()
                    .forObjectTypes("javax.crypto.spec.DESedeKeySpec")
                    .forConstructor()
                    .withMethodParameter(BYTE_ARRAY_TYPE)
                    .shouldBeDetectedAs(new KeySizeFactory<>())
                    .withMethodParameter("int")
                    .buildForContext(new SecretKeyContext(KeyContext.Kind.DESede))
                    .inBundle(() -> "Jca")
                    .withoutDependingDetectionRules();

    private JcaDESedeKeySpec() {
        // nothing
    }

    @Nonnull
    public static List<IDetectionRule<Tree>> rules() {
        return List.of(DESede_KEY_SPEC_1, DESede_KEY_SPEC_2);
    }
}
