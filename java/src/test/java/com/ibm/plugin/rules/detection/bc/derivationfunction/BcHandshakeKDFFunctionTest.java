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
package com.ibm.plugin.rules.detection.bc.derivationfunction;

import static org.assertj.core.api.Assertions.assertThat;

import com.ibm.engine.detection.DetectionStore;
import com.ibm.engine.model.IValue;
import com.ibm.engine.model.OperationMode;
import com.ibm.engine.model.ValueAction;
import com.ibm.engine.model.context.DigestContext;
import com.ibm.engine.model.context.KeyContext;
import com.ibm.mapper.model.DigestSize;
import com.ibm.mapper.model.INode;
import com.ibm.mapper.model.KeyDerivationFunction;
import com.ibm.mapper.model.MessageDigest;
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

class BcHandshakeKDFFunctionTest extends TestBase {
    @Test
    void test() {
        CheckVerifier.newVerifier()
                .onFile(
                        "src/test/files/rules/detection/bc/derivationfunction/BcHandshakeKDFFunctionTestFile.java")
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
        assertThat(detectionStore.getDetectionValueContext()).isInstanceOf(KeyContext.class);
        IValue<Tree> value0 = detectionStore.getDetectionValues().get(0);
        assertThat(value0).isInstanceOf(OperationMode.class);
        assertThat(value0.asString()).isEqualTo("1");

        DetectionStore<JavaCheck, Tree, Symbol, JavaFileScannerContext> store_1 =
                getStoreOfValueType(ValueAction.class, detectionStore.getChildren());
        assertThat(store_1.getDetectionValues()).hasSize(1);
        assertThat(store_1.getDetectionValueContext()).isInstanceOf(DigestContext.class);
        IValue<Tree> value0_1 = store_1.getDetectionValues().get(0);
        assertThat(value0_1).isInstanceOf(ValueAction.class);
        assertThat(value0_1.asString()).isEqualTo("SHA256Digest");

        /*
         * Translation
         */

        assertThat(nodes).hasSize(1);

        // KeyDerivationFunction
        INode keyDerivationFunctionNode = nodes.get(0);
        assertThat(keyDerivationFunctionNode.getKind()).isEqualTo(KeyDerivationFunction.class);
        assertThat(keyDerivationFunctionNode.getChildren()).hasSize(1);
        assertThat(keyDerivationFunctionNode.asString()).isEqualTo("KDF2");

        // MessageDigest under KeyDerivationFunction
        INode messageDigestNode = keyDerivationFunctionNode.getChildren().get(MessageDigest.class);
        assertThat(messageDigestNode).isNotNull();
        assertThat(messageDigestNode.getChildren()).hasSize(4);
        assertThat(messageDigestNode.asString()).isEqualTo("SHA256");

        // DigestSize under MessageDigest under KeyDerivationFunction
        INode digestSizeNode = messageDigestNode.getChildren().get(DigestSize.class);
        assertThat(digestSizeNode).isNotNull();
        assertThat(digestSizeNode.getChildren()).isEmpty();
        assertThat(digestSizeNode.asString()).isEqualTo("256");

        // // Digest under MessageDigest under KeyDerivationFunction
        // INode digestNode = messageDigestNode.getChildren().get(Digest.class);
        // assertThat(digestNode).isNotNull();
        // assertThat(digestNode.getChildren()).isEmpty();
        // assertThat(digestNode.asString()).isEqualTo("DIGEST");

        // // BlockSize under MessageDigest under KeyDerivationFunction
        // INode blockSizeNode = messageDigestNode.getChildren().get(BlockSize.class);
        // assertThat(blockSizeNode).isNotNull();
        // assertThat(blockSizeNode.getChildren()).isEmpty();
        // assertThat(blockSizeNode.asString()).isEqualTo("512");

        // // Oid under MessageDigest under KeyDerivationFunction
        // INode oidNode = messageDigestNode.getChildren().get(Oid.class);
        // assertThat(oidNode).isNotNull();
        // assertThat(oidNode.getChildren()).isEmpty();
        // assertThat(oidNode.asString()).isEqualTo("2.16.840.1.101.3.4.2.1");
    }
}
