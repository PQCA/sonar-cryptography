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
package com.ibm.plugin.rules.detection.bc.cipherparameters;

import static org.assertj.core.api.Assertions.assertThat;

import com.ibm.engine.detection.DetectionStore;
import com.ibm.engine.model.IValue;
import com.ibm.engine.model.OperationMode;
import com.ibm.engine.model.ValueAction;
import com.ibm.engine.model.context.CipherContext;
import com.ibm.engine.model.context.DigestContext;
import com.ibm.mapper.model.BlockCipher;
import com.ibm.mapper.model.INode;
import com.ibm.mapper.model.MessageDigest;
import com.ibm.mapper.model.functionality.Encrypt;
import com.ibm.plugin.TestBase;
import com.ibm.plugin.rules.detection.bc.BouncyCastleJars;
import java.util.List;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;
import org.sonar.plugins.java.api.JavaCheck;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.Tree;

class BcNTRUParametersTest extends TestBase {
    @Test
    void test() {
        CheckVerifier.newVerifier()
                .onFile(
                        "src/test/files/rules/detection/bc/cipherparameters/BcNTRUParametersTestFile.java")
                .withChecks(this)
                .withClassPath(BouncyCastleJars.latestJar)
                .verifyIssues();
    }

    @Override
    public void asserts(
            int findingId,
            @Nonnull DetectionStore<JavaCheck, Tree, Symbol, JavaFileScannerContext> detectionStore,
            @Nonnull List<INode> nodes) {
        /*
         * Detection Store
         */

        assertThat(detectionStore.getDetectionValues()).hasSize(1);
        assertThat(detectionStore.getDetectionValueContext()).isInstanceOf(CipherContext.class);
        IValue<Tree> value0 = detectionStore.getDetectionValues().get(0);
        assertThat(value0).isInstanceOf(ValueAction.class);
        assertThat(value0.asString()).isEqualTo("AESEngine");

        DetectionStore<JavaCheck, Tree, Symbol, JavaFileScannerContext> store_1 =
                getStoreOfValueType(OperationMode.class, detectionStore.getChildren());
        assertThat(store_1.getDetectionValues()).hasSize(1);
        assertThat(store_1.getDetectionValueContext()).isInstanceOf(CipherContext.class);
        IValue<Tree> value0_1 = store_1.getDetectionValues().get(0);
        assertThat(value0_1).isInstanceOf(OperationMode.class);
        assertThat(value0_1.asString()).isEqualTo("1");

        DetectionStore<JavaCheck, Tree, Symbol, JavaFileScannerContext> store_1_1 =
                getStoreOfValueType(ValueAction.class, store_1.getChildren());
        assertThat(store_1_1.getDetectionValues()).hasSize(1);
        assertThat(store_1_1.getDetectionValueContext()).isInstanceOf(DigestContext.class);
        IValue<Tree> value0_1_1_1 = store_1_1.getDetectionValues().get(0);
        assertThat(value0_1_1_1).isInstanceOf(ValueAction.class);
        assertThat(value0_1_1_1.asString()).isEqualTo("SHA256Digest");

        /*
         * Translation
         */

        assertThat(nodes).hasSize(1);

        // BlockCipher
        INode blockCipherNode5 = nodes.get(0);
        assertThat(blockCipherNode5.getKind()).isEqualTo(BlockCipher.class);
        assertThat(blockCipherNode5.getChildren()).hasSize(4);
        assertThat(blockCipherNode5.asString()).isEqualTo("AES");

        // MessageDigest under BlockCipher
        INode messageDigestNode5 = blockCipherNode5.getChildren().get(MessageDigest.class);
        assertThat(messageDigestNode5).isNotNull();
        assertThat(messageDigestNode5.getChildren()).hasSize(4);
        assertThat(messageDigestNode5.asString()).isEqualTo("SHA256");

        // Encrypt under BlockCipher
        INode encryptNode5 = blockCipherNode5.getChildren().get(Encrypt.class);
        assertThat(encryptNode5).isNotNull();
        assertThat(encryptNode5.getChildren()).isEmpty();
        assertThat(encryptNode5.asString()).isEqualTo("ENCRYPT");
    }
}
