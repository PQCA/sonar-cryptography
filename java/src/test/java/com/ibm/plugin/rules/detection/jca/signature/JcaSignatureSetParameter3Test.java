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
package com.ibm.plugin.rules.detection.jca.signature;

import static org.assertj.core.api.Assertions.assertThat;

import com.ibm.engine.detection.DetectionStore;
import com.ibm.engine.model.Algorithm;
import com.ibm.engine.model.IValue;
import com.ibm.engine.model.SaltSize;
import com.ibm.engine.model.context.SignatureContext;
import com.ibm.mapper.model.BlockSize;
import com.ibm.mapper.model.DigestSize;
import com.ibm.mapper.model.INode;
import com.ibm.mapper.model.MaskGenerationFunction;
import com.ibm.mapper.model.MessageDigest;
import com.ibm.mapper.model.Oid;
import com.ibm.mapper.model.ProbabilisticSignatureScheme;
import com.ibm.mapper.model.SaltLength;
import com.ibm.mapper.model.functionality.Digest;
import com.ibm.plugin.TestBase;
import java.util.List;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;
import org.sonar.plugins.java.api.JavaCheck;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.Tree;

class JcaSignatureSetParameter3Test extends TestBase {

    @Test
    void test() {
        CheckVerifier.newVerifier()
                .onFile(
                        "src/test/files/rules/detection/jca/signature/JcaSignatureSetParameter3TestFile.java")
                .withChecks(this)
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
        IValue<Tree> value = detectionStore.getDetectionValues().get(0);
        assertThat(detectionStore.getDetectionValueContext()).isInstanceOf(SignatureContext.class);
        assertThat(value).isInstanceOf(Algorithm.class);
        assertThat(value.asString()).isEqualTo("RSASSA-PSS");

        DetectionStore<JavaCheck, Tree, Symbol, JavaFileScannerContext> pssStore =
                getStoreOfValueType(Algorithm.class, detectionStore.getChildren());
        assertThat(pssStore).isNotNull();
        assertThat(pssStore.getDetectionValueContext()).isInstanceOf(SignatureContext.class);
        assertThat(pssStore.getDetectionValues()).hasSize(3);
        for (IValue<Tree> v : pssStore.getDetectionValues()) {
            assertThat(v).isOfAnyClassIn(Algorithm.class, SaltSize.class);
            assertThat(v.asString()).containsAnyOf("SHA3-256", "MGF1", "160");
            if (v.asString().equals("MGF1")) {
                assertThat(pssStore.getChildrenForParameterWithId(1)).isPresent();
                DetectionStore<JavaCheck, Tree, Symbol, JavaFileScannerContext> mgf1md =
                        getStoreOfValueType(
                                Algorithm.class, pssStore.getChildrenForParameterWithId(1).get());
                assertThat(mgf1md).isNotNull();
                assertThat(mgf1md.getDetectionValues()).hasSize(1);
                IValue<Tree> md = mgf1md.getDetectionValues().get(0);
                assertThat(mgf1md.getDetectionValueContext()).isInstanceOf(SignatureContext.class);
                assertThat(md).isInstanceOf(Algorithm.class);
                assertThat(md.asString()).isEqualTo("SHA1");
            }
        }

        /*
         * Translation
         */

        assertThat(nodes).hasSize(1);

        // ProbabilisticSignatureScheme
        INode probabilisticSignatureSchemeNode = nodes.get(0);
        assertThat(probabilisticSignatureSchemeNode.getKind())
                .isEqualTo(ProbabilisticSignatureScheme.class);
        assertThat(probabilisticSignatureSchemeNode.getChildren()).hasSize(4);
        assertThat(probabilisticSignatureSchemeNode.asString()).isEqualTo("RSASSA-PSS");

        // SaltLength under ProbabilisticSignatureScheme
        INode saltLengthNode = probabilisticSignatureSchemeNode.getChildren().get(SaltLength.class);
        assertThat(saltLengthNode).isNotNull();
        assertThat(saltLengthNode.getChildren()).isEmpty();
        assertThat(saltLengthNode.asString()).isEqualTo("160");

        // MessageDigest under ProbabilisticSignatureScheme
        INode messageDigestNode =
                probabilisticSignatureSchemeNode.getChildren().get(MessageDigest.class);
        assertThat(messageDigestNode).isNotNull();
        assertThat(messageDigestNode.getChildren()).hasSize(4);
        assertThat(messageDigestNode.asString()).isEqualTo("SHA3-256");

        // DigestSize under MessageDigest under ProbabilisticSignatureScheme
        INode digestSizeNode = messageDigestNode.getChildren().get(DigestSize.class);
        assertThat(digestSizeNode).isNotNull();
        assertThat(digestSizeNode.getChildren()).isEmpty();
        assertThat(digestSizeNode.asString()).isEqualTo("256");

        // BlockSize under MessageDigest under ProbabilisticSignatureScheme
        INode blockSizeNode = messageDigestNode.getChildren().get(BlockSize.class);
        assertThat(blockSizeNode).isNotNull();
        assertThat(blockSizeNode.getChildren()).isEmpty();
        assertThat(blockSizeNode.asString()).isEqualTo("1088");

        // Digest under MessageDigest under ProbabilisticSignatureScheme
        INode digestNode = messageDigestNode.getChildren().get(Digest.class);
        assertThat(digestNode).isNotNull();
        assertThat(digestNode.getChildren()).isEmpty();
        assertThat(digestNode.asString()).isEqualTo("DIGEST");

        // Oid under MessageDigest under ProbabilisticSignatureScheme
        INode oidNode1 = messageDigestNode.getChildren().get(Oid.class);
        assertThat(oidNode1).isNotNull();
        assertThat(oidNode1.getChildren()).isEmpty();
        assertThat(oidNode1.asString()).isEqualTo("2.16.840.1.101.3.4.2.8");

        // MaskGenerationFunction under ProbabilisticSignatureScheme
        INode maskGenerationFunctionNode =
                probabilisticSignatureSchemeNode.getChildren().get(MaskGenerationFunction.class);
        assertThat(maskGenerationFunctionNode).isNotNull();
        assertThat(maskGenerationFunctionNode.getChildren()).hasSize(2);
        assertThat(maskGenerationFunctionNode.asString()).isEqualTo("MGF1");

        // MessageDigest under MaskGenerationFunction under ProbabilisticSignatureScheme
        INode messageDigestNode1 =
                maskGenerationFunctionNode.getChildren().get(MessageDigest.class);
        assertThat(messageDigestNode1).isNotNull();
        assertThat(messageDigestNode1.getChildren()).hasSize(4);
        assertThat(messageDigestNode1.asString()).isEqualTo("SHA1");

        // DigestSize under MessageDigest under MaskGenerationFunction under
        // ProbabilisticSignatureScheme
        INode digestSizeNode1 = messageDigestNode1.getChildren().get(DigestSize.class);
        assertThat(digestSizeNode1).isNotNull();
        assertThat(digestSizeNode1.getChildren()).isEmpty();
        assertThat(digestSizeNode1.asString()).isEqualTo("160");

        // BlockSize under MessageDigest under MaskGenerationFunction under
        // ProbabilisticSignatureScheme
        INode blockSizeNode1 = messageDigestNode1.getChildren().get(BlockSize.class);
        assertThat(blockSizeNode1).isNotNull();
        assertThat(blockSizeNode1.getChildren()).isEmpty();
        assertThat(blockSizeNode1.asString()).isEqualTo("512");

        // Digest under MessageDigest under MaskGenerationFunction under
        // ProbabilisticSignatureScheme
        INode digestNode1 = messageDigestNode1.getChildren().get(Digest.class);
        assertThat(digestNode1).isNotNull();
        assertThat(digestNode1.getChildren()).isEmpty();
        assertThat(digestNode1.asString()).isEqualTo("DIGEST");

        // Oid under MessageDigest under MaskGenerationFunction under
        // ProbabilisticSignatureScheme
        INode oidNode = messageDigestNode1.getChildren().get(Oid.class);
        assertThat(oidNode).isNotNull();
        assertThat(oidNode.getChildren()).isEmpty();
        assertThat(oidNode.asString()).isEqualTo("1.3.14.3.2.26");

        // Oid under MaskGenerationFunction under ProbabilisticSignatureScheme
        INode oidNode2 = maskGenerationFunctionNode.getChildren().get(Oid.class);
        assertThat(oidNode2).isNotNull();
        assertThat(oidNode2.getChildren()).isEmpty();
        assertThat(oidNode2.asString()).isEqualTo("1.2.840.113549.1.1.8");

        // Oid under ProbabilisticSignatureScheme
        INode oidNode3 = probabilisticSignatureSchemeNode.getChildren().get(Oid.class);
        assertThat(oidNode3).isNotNull();
        assertThat(oidNode3.getChildren()).isEmpty();
        assertThat(oidNode3.asString()).isEqualTo("1.2.840.113549.1.1.10");
    }
}
