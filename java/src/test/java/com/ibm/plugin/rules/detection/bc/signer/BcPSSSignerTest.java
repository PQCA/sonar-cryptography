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
package com.ibm.plugin.rules.detection.bc.signer;

import static org.assertj.core.api.Assertions.assertThat;

import com.ibm.engine.detection.DetectionStore;
import com.ibm.engine.model.IValue;
import com.ibm.engine.model.OperationMode;
import com.ibm.engine.model.ValueAction;
import com.ibm.engine.model.context.CipherContext;
import com.ibm.engine.model.context.DigestContext;
import com.ibm.engine.model.context.SignatureContext;
import com.ibm.mapper.model.BlockSize;
import com.ibm.mapper.model.DigestSize;
import com.ibm.mapper.model.INode;
import com.ibm.mapper.model.MaskGenerationFunction;
import com.ibm.mapper.model.MessageDigest;
import com.ibm.mapper.model.Oid;
import com.ibm.mapper.model.ProbabilisticSignatureScheme;
import com.ibm.mapper.model.PublicKeyEncryption;
import com.ibm.mapper.model.functionality.Digest;
import com.ibm.mapper.model.functionality.Sign;
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

class BcPSSSignerTest extends TestBase {

    @Test
    void test() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/rules/detection/bc/signer/BcPSSSignerTestFile.java")
                .withChecks(this)
                .withClassPath(BouncyCastleJars.latestJar)
                .verifyIssues();
    }

    @Override
    public void asserts(
            int findingId,
            @Nonnull DetectionStore<JavaCheck, Tree, Symbol, JavaFileScannerContext> detectionStore,
            @Nonnull List<INode> nodes) {
        if (findingId == 0) {
            /*
             * Detection Store
             */
            assertThat(detectionStore).isNotNull();
            assertThat(detectionStore.getDetectionValues()).hasSize(1);
            assertThat(detectionStore.getDetectionValueContext()).isInstanceOf(DigestContext.class);
            IValue<Tree> value0 = detectionStore.getDetectionValues().get(0);
            assertThat(value0).isInstanceOf(ValueAction.class);
            assertThat(value0.asString()).isEqualTo("SHA256Digest");

            /*
             * Translation
             */
            assertThat(nodes).hasSize(1);

            // MessageDigest
            INode messageDigestNode = nodes.get(0);
            assertThat(messageDigestNode.getKind()).isEqualTo(MessageDigest.class);
            assertThat(messageDigestNode.getChildren()).hasSize(4);
            assertThat(messageDigestNode.asString()).isEqualTo("SHA256");

            // BlockSize under MessageDigest
            INode blockSizeNode = messageDigestNode.getChildren().get(BlockSize.class);
            assertThat(blockSizeNode).isNotNull();
            assertThat(blockSizeNode.getChildren()).isEmpty();
            assertThat(blockSizeNode.asString()).isEqualTo("512");

            // Oid under MessageDigest
            INode oidNode = messageDigestNode.getChildren().get(Oid.class);
            assertThat(oidNode).isNotNull();
            assertThat(oidNode.getChildren()).isEmpty();
            assertThat(oidNode.asString()).isEqualTo("2.16.840.1.101.3.4.2.1");

            // DigestSize under MessageDigest
            INode digestSizeNode = messageDigestNode.getChildren().get(DigestSize.class);
            assertThat(digestSizeNode).isNotNull();
            assertThat(digestSizeNode.getChildren()).isEmpty();
            assertThat(digestSizeNode.asString()).isEqualTo("256");

            // Digest under MessageDigest
            INode digestNode = messageDigestNode.getChildren().get(Digest.class);
            assertThat(digestNode).isNotNull();
            assertThat(digestNode.getChildren()).isEmpty();
            assertThat(digestNode.asString()).isEqualTo("DIGEST");
        } else if (findingId == 1) {
            /*
             * Detection Store
             */
            assertThat(detectionStore).isNotNull();
            assertThat(detectionStore.getDetectionValues()).hasSize(1);
            assertThat(detectionStore.getDetectionValueContext()).isInstanceOf(DigestContext.class);
            IValue<Tree> value0 = detectionStore.getDetectionValues().get(0);
            assertThat(value0).isInstanceOf(ValueAction.class);
            assertThat(value0.asString()).isEqualTo("SHA512Digest");

            /*
             * Translation
             */
            assertThat(nodes).hasSize(1);

            // MessageDigest
            INode messageDigestNode = nodes.get(0);
            assertThat(messageDigestNode.getKind()).isEqualTo(MessageDigest.class);
            assertThat(messageDigestNode.getChildren()).hasSize(4);
            assertThat(messageDigestNode.asString()).isEqualTo("SHA512");

            // BlockSize under MessageDigest
            INode blockSizeNode = messageDigestNode.getChildren().get(BlockSize.class);
            assertThat(blockSizeNode).isNotNull();
            assertThat(blockSizeNode.getChildren()).isEmpty();
            assertThat(blockSizeNode.asString()).isEqualTo("1024");

            // Oid under MessageDigest
            INode oidNode = messageDigestNode.getChildren().get(Oid.class);
            assertThat(oidNode).isNotNull();
            assertThat(oidNode.getChildren()).isEmpty();
            assertThat(oidNode.asString()).isEqualTo("2.16.840.1.101.3.4.2.3");

            // DigestSize under MessageDigest
            INode digestSizeNode = messageDigestNode.getChildren().get(DigestSize.class);
            assertThat(digestSizeNode).isNotNull();
            assertThat(digestSizeNode.getChildren()).isEmpty();
            assertThat(digestSizeNode.asString()).isEqualTo("512");

            // Digest under MessageDigest
            INode digestNode = messageDigestNode.getChildren().get(Digest.class);
            assertThat(digestNode).isNotNull();
            assertThat(digestNode.getChildren()).isEmpty();
            assertThat(digestNode.asString()).isEqualTo("DIGEST");
        } else if (findingId == 2) {
            /*
             * Detection Store
             */
            assertThat(detectionStore).isNotNull();
            assertThat(detectionStore.getDetectionValues()).hasSize(1);
            assertThat(detectionStore.getDetectionValueContext()).isInstanceOf(CipherContext.class);
            IValue<Tree> value0 = detectionStore.getDetectionValues().get(0);
            assertThat(value0).isInstanceOf(ValueAction.class);
            assertThat(value0.asString()).isEqualTo("RSAEngine");

            /*
             * Translation
             */
            assertThat(nodes).hasSize(1);

            // PublicKeyEncryption
            INode publicKeyEncryptionNode = nodes.get(0);
            assertThat(publicKeyEncryptionNode.getKind()).isEqualTo(PublicKeyEncryption.class);
            assertThat(publicKeyEncryptionNode.getChildren()).hasSize(1);
            assertThat(publicKeyEncryptionNode.asString()).isEqualTo("RSA");

            // Oid under PublicKeyEncryption
            INode oidNode = publicKeyEncryptionNode.getChildren().get(Oid.class);
            assertThat(oidNode).isNotNull();
            assertThat(oidNode.getChildren()).isEmpty();
            assertThat(oidNode.asString()).isEqualTo("1.2.840.113549.1.1.1");
        } else if (findingId == 3) {
            /*
             * Detection Store
             */
            assertThat(detectionStore).isNotNull();
            assertThat(detectionStore.getDetectionValues()).hasSize(1);
            assertThat(detectionStore.getDetectionValueContext())
                    .isInstanceOf(SignatureContext.class);
            IValue<Tree> value0 = detectionStore.getDetectionValues().get(0);
            assertThat(value0).isInstanceOf(ValueAction.class);
            assertThat(value0.asString()).isEqualTo("PSSSigner");

            DetectionStore<JavaCheck, Tree, Symbol, JavaFileScannerContext> store1 =
                    getStoreOfValueType(OperationMode.class, detectionStore.getChildren());
            assertThat(store1).isNotNull();
            assertThat(store1.getDetectionValues()).hasSize(1);
            assertThat(store1.getDetectionValueContext()).isInstanceOf(SignatureContext.class);
            IValue<Tree> value01 = store1.getDetectionValues().get(0);
            assertThat(value01).isInstanceOf(OperationMode.class);
            assertThat(value01.asString()).isEqualTo("1");

            List<DetectionStore<JavaCheck, Tree, Symbol, JavaFileScannerContext>> stores =
                    getStoresOfValueType(ValueAction.class, detectionStore.getChildren());
            assertThat(stores).hasSize(3);

            /*
             * Translation
             */
            assertThat(nodes).hasSize(1);

            // ProbabilisticSignatureScheme
            INode probabilisticSignatureSchemeNode = nodes.get(0);
            assertThat(probabilisticSignatureSchemeNode.getKind())
                    .isEqualTo(ProbabilisticSignatureScheme.class);
            assertThat(probabilisticSignatureSchemeNode.getChildren()).hasSize(5);
            assertThat(probabilisticSignatureSchemeNode.asString()).isEqualTo("RSASSA-PSS");

            // Sign under ProbabilisticSignatureScheme
            INode signNode = probabilisticSignatureSchemeNode.getChildren().get(Sign.class);
            assertThat(signNode).isNotNull();
            assertThat(signNode.getChildren()).isEmpty();
            assertThat(signNode.asString()).isEqualTo("SIGN");

            // MaskGenerationFunction under ProbabilisticSignatureScheme
            INode maskGenerationFunctionNode =
                    probabilisticSignatureSchemeNode
                            .getChildren()
                            .get(MaskGenerationFunction.class);
            assertThat(maskGenerationFunctionNode).isNotNull();
            assertThat(maskGenerationFunctionNode.getChildren()).hasSize(2);
            assertThat(maskGenerationFunctionNode.asString()).isEqualTo("MGF1");

            // Oid under MaskGenerationFunction under ProbabilisticSignatureScheme
            INode oidNode = maskGenerationFunctionNode.getChildren().get(Oid.class);
            assertThat(oidNode).isNotNull();
            assertThat(oidNode.getChildren()).isEmpty();
            assertThat(oidNode.asString()).isEqualTo("1.2.840.113549.1.1.8");

            // MessageDigest under MaskGenerationFunction under ProbabilisticSignatureScheme
            INode messageDigestNode =
                    maskGenerationFunctionNode.getChildren().get(MessageDigest.class);
            assertThat(messageDigestNode).isNotNull();
            assertThat(messageDigestNode.getChildren()).hasSize(4);
            assertThat(messageDigestNode.asString()).isEqualTo("SHA512");

            // BlockSize under MessageDigest under MaskGenerationFunction under
            // ProbabilisticSignatureScheme
            INode blockSizeNode = messageDigestNode.getChildren().get(BlockSize.class);
            assertThat(blockSizeNode).isNotNull();
            assertThat(blockSizeNode.getChildren()).isEmpty();
            assertThat(blockSizeNode.asString()).isEqualTo("1024");

            // Oid under MessageDigest under MaskGenerationFunction under
            // ProbabilisticSignatureScheme
            INode oidNode1 = messageDigestNode.getChildren().get(Oid.class);
            assertThat(oidNode1).isNotNull();
            assertThat(oidNode1.getChildren()).isEmpty();
            assertThat(oidNode1.asString()).isEqualTo("2.16.840.1.101.3.4.2.3");

            // DigestSize under MessageDigest under MaskGenerationFunction under
            // ProbabilisticSignatureScheme
            INode digestSizeNode = messageDigestNode.getChildren().get(DigestSize.class);
            assertThat(digestSizeNode).isNotNull();
            assertThat(digestSizeNode.getChildren()).isEmpty();
            assertThat(digestSizeNode.asString()).isEqualTo("512");

            // Digest under MessageDigest under MaskGenerationFunction under
            // ProbabilisticSignatureScheme
            INode digestNode = messageDigestNode.getChildren().get(Digest.class);
            assertThat(digestNode).isNotNull();
            assertThat(digestNode.getChildren()).isEmpty();
            assertThat(digestNode.asString()).isEqualTo("DIGEST");

            // Oid under ProbabilisticSignatureScheme
            INode oidNode2 = probabilisticSignatureSchemeNode.getChildren().get(Oid.class);
            assertThat(oidNode2).isNotNull();
            assertThat(oidNode2.getChildren()).isEmpty();
            assertThat(oidNode2.asString()).isEqualTo("1.2.840.113549.1.1.10");

            // PublicKeyEncryption under ProbabilisticSignatureScheme
            INode publicKeyEncryptionNode =
                    probabilisticSignatureSchemeNode.getChildren().get(PublicKeyEncryption.class);
            assertThat(publicKeyEncryptionNode).isNotNull();
            assertThat(publicKeyEncryptionNode.getChildren()).hasSize(1);
            assertThat(publicKeyEncryptionNode.asString()).isEqualTo("RSA");

            // Oid under PublicKeyEncryption under ProbabilisticSignatureScheme
            INode oidNode3 = publicKeyEncryptionNode.getChildren().get(Oid.class);
            assertThat(oidNode3).isNotNull();
            assertThat(oidNode3.getChildren()).isEmpty();
            assertThat(oidNode3.asString()).isEqualTo("1.2.840.113549.1.1.1");

            // MessageDigest under ProbabilisticSignatureScheme
            INode messageDigestNode1 =
                    probabilisticSignatureSchemeNode.getChildren().get(MessageDigest.class);
            assertThat(messageDigestNode1).isNotNull();
            assertThat(messageDigestNode1.getChildren()).hasSize(4);
            assertThat(messageDigestNode1.asString()).isEqualTo("SHA256");

            // BlockSize under MessageDigest under ProbabilisticSignatureScheme
            INode blockSizeNode1 = messageDigestNode1.getChildren().get(BlockSize.class);
            assertThat(blockSizeNode1).isNotNull();
            assertThat(blockSizeNode1.getChildren()).isEmpty();
            assertThat(blockSizeNode1.asString()).isEqualTo("512");

            // Oid under MessageDigest under ProbabilisticSignatureScheme
            INode oidNode4 = messageDigestNode1.getChildren().get(Oid.class);
            assertThat(oidNode4).isNotNull();
            assertThat(oidNode4.getChildren()).isEmpty();
            assertThat(oidNode4.asString()).isEqualTo("2.16.840.1.101.3.4.2.1");

            // DigestSize under MessageDigest under ProbabilisticSignatureScheme
            INode digestSizeNode1 = messageDigestNode1.getChildren().get(DigestSize.class);
            assertThat(digestSizeNode1).isNotNull();
            assertThat(digestSizeNode1.getChildren()).isEmpty();
            assertThat(digestSizeNode1.asString()).isEqualTo("256");

            // Digest under MessageDigest under ProbabilisticSignatureScheme
            INode digestNode1 = messageDigestNode1.getChildren().get(Digest.class);
            assertThat(digestNode1).isNotNull();
            assertThat(digestNode1.getChildren()).isEmpty();
            assertThat(digestNode1.asString()).isEqualTo("DIGEST");
        }
    }
}
