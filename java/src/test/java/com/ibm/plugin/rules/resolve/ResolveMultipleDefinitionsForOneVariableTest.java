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
package com.ibm.plugin.rules.resolve;

import static org.assertj.core.api.Assertions.assertThat;

import com.ibm.engine.detection.DetectionStore;
import com.ibm.engine.model.Algorithm;
import com.ibm.engine.model.IValue;
import com.ibm.engine.model.KeySize;
import com.ibm.engine.model.OperationMode;
import com.ibm.engine.model.context.CipherContext;
import com.ibm.engine.model.context.SecretKeyContext;
import com.ibm.mapper.model.INode;
import com.ibm.plugin.TestBase;
import java.util.List;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;
import org.sonar.plugins.java.api.JavaCheck;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.Tree;

class ResolveMultipleDefinitionsForOneVariableTest extends TestBase {

    @Test
    void test() {
        CheckVerifier.newVerifier()
                .onFile(
                        "src/test/files/rules/resolve/ResolveMultipleDefinitionsForOneVariableTestFile.java")
                .withChecks(this)
                .verifyIssues();
    }

    @Override
    public void asserts(
            int findingId,
            @Nonnull DetectionStore<JavaCheck, Tree, Symbol, JavaFileScannerContext> detectionStore,
            @Nonnull List<INode> nodes) {
        if (findingId == 0) {
            assertThat(detectionStore.getDetectionValues()).hasSize(1);
            IValue<Tree> value = detectionStore.getDetectionValues().get(0);
            assertThat(detectionStore.getDetectionValueContext()).isInstanceOf(CipherContext.class);
            assertThat(value).isInstanceOf(Algorithm.class);
            assertThat(value.asString()).isEqualTo("AES/ECB/PKCS5Padding");

            DetectionStore<JavaCheck, Tree, Symbol, JavaFileScannerContext> operations =
                    getStoreOfValueType(OperationMode.class, detectionStore.getChildren());
            assertThat(operations).isNotNull();
            assertThat(operations.getDetectionValues()).hasSize(2);
            List<IValue<Tree>> values = operations.getDetectionValues();
            assertThat(values).anyMatch(v -> v.asString().equals("1") || v.asString().equals("2"));

            DetectionStore<JavaCheck, Tree, Symbol, JavaFileScannerContext> secretKey =
                    getStoreOfValueType(Algorithm.class, operations.getChildren());
            assertThat(secretKey).isNotNull();
            assertThat(secretKey.getDetectionValues()).hasSize(1);
            value = secretKey.getDetectionValues().get(0);
            assertThat(secretKey.getDetectionValueContext()).isInstanceOf(SecretKeyContext.class);
            assertThat(value).isInstanceOf(Algorithm.class);
            assertThat(value.asString()).isEqualTo("AES");

            DetectionStore<JavaCheck, Tree, Symbol, JavaFileScannerContext> keyLength =
                    getStoreOfValueType(KeySize.class, secretKey.getChildren());
            assertThat(keyLength).isNotNull();
            assertThat(keyLength.getDetectionValues()).hasSize(1);
            value = keyLength.getDetectionValues().get(0);
            assertThat(keyLength.getDetectionValueContext()).isInstanceOf(SecretKeyContext.class);
            assertThat(value).isInstanceOf(KeySize.class);
            assertThat(value.asString()).isEqualTo("128");
        } else if (findingId == 3) {
            assertThat(detectionStore.getDetectionValues()).hasSize(1);
            IValue<Tree> value = detectionStore.getDetectionValues().get(0);
            assertThat(detectionStore.getDetectionValueContext()).isInstanceOf(CipherContext.class);
            assertThat(value).isInstanceOf(Algorithm.class);
            assertThat(value.asString()).isEqualTo("AES/ECB/NoPadding");

            DetectionStore<JavaCheck, Tree, Symbol, JavaFileScannerContext> operations =
                    getStoreOfValueType(OperationMode.class, detectionStore.getChildren());
            assertThat(operations).isNotNull();
            assertThat(operations.getDetectionValues()).hasSize(2);
            List<IValue<Tree>> values = operations.getDetectionValues();
            assertThat(values).anyMatch(v -> v.asString().equals("1") || v.asString().equals("2"));

            DetectionStore<JavaCheck, Tree, Symbol, JavaFileScannerContext> secretKey =
                    getStoreOfValueType(Algorithm.class, operations.getChildren());
            assertThat(secretKey).isNotNull();
            assertThat(secretKey.getDetectionValues()).hasSize(1);
            value = secretKey.getDetectionValues().get(0);
            assertThat(secretKey.getDetectionValueContext()).isInstanceOf(SecretKeyContext.class);
            assertThat(value).isInstanceOf(Algorithm.class);
            assertThat(value.asString()).isEqualTo("AES");

            DetectionStore<JavaCheck, Tree, Symbol, JavaFileScannerContext> keyLength =
                    getStoreOfValueType(KeySize.class, secretKey.getChildren());
            assertThat(keyLength).isNotNull();
            assertThat(keyLength.getDetectionValues()).hasSize(1);
            value = keyLength.getDetectionValues().get(0);
            assertThat(keyLength.getDetectionValueContext()).isInstanceOf(SecretKeyContext.class);
            assertThat(value).isInstanceOf(KeySize.class);
            assertThat(value.asString()).isEqualTo("128");
        }
    }
}
