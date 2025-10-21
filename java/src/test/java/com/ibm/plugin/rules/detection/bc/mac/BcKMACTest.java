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
package com.ibm.plugin.rules.detection.bc.mac;

import static org.assertj.core.api.Assertions.assertThat;

import com.ibm.engine.detection.DetectionStore;
import com.ibm.engine.model.IValue;
import com.ibm.engine.model.ParameterIdentifier;
import com.ibm.engine.model.ValueAction;
import com.ibm.engine.model.context.DigestContext;
import com.ibm.engine.model.context.MacContext;
import com.ibm.mapper.model.ExtendableOutputFunction;
import com.ibm.mapper.model.INode;
import com.ibm.mapper.model.Mac;
import com.ibm.mapper.model.MessageDigest;
import com.ibm.mapper.model.ParameterSetIdentifier;
import com.ibm.mapper.model.functionality.Digest;
import com.ibm.mapper.model.functionality.Tag;
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

class BcKMACTest extends TestBase {
    @Test
    void test() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/rules/detection/bc/mac/BcKMACTestFile.java")
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
            assertThat(value0.asString()).isEqualTo("KMAC");

            /*
             * Translation
             */
            assertThat(nodes).hasSize(1);

            // MessageDigest
            INode messageDigestNode = nodes.get(0);
            assertThat(messageDigestNode.getKind()).isEqualTo(MessageDigest.class);
            assertThat(messageDigestNode.getChildren()).hasSize(2);
            assertThat(messageDigestNode.asString()).isEqualTo("KMAC");

            // ExtendableOutputFunction under MessageDigest
            INode extendableOutputFunctionNode =
                    messageDigestNode.getChildren().get(ExtendableOutputFunction.class);
            assertThat(extendableOutputFunctionNode).isNotNull();
            assertThat(extendableOutputFunctionNode.getChildren()).hasSize(1);
            assertThat(extendableOutputFunctionNode.asString()).isEqualTo("cSHAKE");

            // Digest under ExtendableOutputFunction under MessageDigest
            INode digestNode = extendableOutputFunctionNode.getChildren().get(Digest.class);
            assertThat(digestNode).isNotNull();
            assertThat(digestNode.getChildren()).isEmpty();
            assertThat(digestNode.asString()).isEqualTo("DIGEST");

            // Digest under MessageDigest
            INode digestNode1 = messageDigestNode.getChildren().get(Digest.class);
            assertThat(digestNode1).isNotNull();
            assertThat(digestNode1.getChildren()).isEmpty();
            assertThat(digestNode1.asString()).isEqualTo("DIGEST");
        } else if (findingId == 1) {
            /*
             * Detection Store
             */
            assertThat(detectionStore).isNotNull();
            assertThat(detectionStore.getDetectionValues()).hasSize(1);
            assertThat(detectionStore.getDetectionValueContext()).isInstanceOf(MacContext.class);
            IValue<Tree> value0 = detectionStore.getDetectionValues().get(0);
            assertThat(value0).isInstanceOf(ValueAction.class);
            assertThat(value0.asString()).isEqualTo("KMAC");

            DetectionStore<JavaCheck, Tree, Symbol, JavaFileScannerContext> store1 =
                    getStoreOfValueType(ParameterIdentifier.class, detectionStore.getChildren());
            assertThat(store1).isNotNull();
            assertThat(store1.getDetectionValues()).hasSize(1);
            assertThat(store1.getDetectionValueContext()).isInstanceOf(MacContext.class);
            IValue<Tree> value01 = store1.getDetectionValues().get(0);
            assertThat(value01).isInstanceOf(ParameterIdentifier.class);
            assertThat(value01.asString()).isEqualTo("256");

            /*
             * Translation
             */
            assertThat(nodes).hasSize(1);

            // Mac
            INode macNode = nodes.get(0);
            assertThat(macNode.getKind()).isEqualTo(Mac.class);
            assertThat(macNode.getChildren()).hasSize(3);
            assertThat(macNode.asString()).isEqualTo("KMAC256");

            // ExtendableOutputFunction under Mac
            INode extendableOutputFunctionNode =
                    macNode.getChildren().get(ExtendableOutputFunction.class);
            assertThat(extendableOutputFunctionNode).isNotNull();
            assertThat(extendableOutputFunctionNode.getChildren()).hasSize(1);
            assertThat(extendableOutputFunctionNode.asString()).isEqualTo("cSHAKE");

            // Digest under ExtendableOutputFunction under Mac
            INode digestNode = extendableOutputFunctionNode.getChildren().get(Digest.class);
            assertThat(digestNode).isNotNull();
            assertThat(digestNode.getChildren()).isEmpty();
            assertThat(digestNode.asString()).isEqualTo("DIGEST");

            // Tag under Mac
            INode tagNode = macNode.getChildren().get(Tag.class);
            assertThat(tagNode).isNotNull();
            assertThat(tagNode.getChildren()).isEmpty();
            assertThat(tagNode.asString()).isEqualTo("TAG");

            // ParameterSetIdentifier under Mac
            INode parameterSetIdentifierNode =
                    macNode.getChildren().get(ParameterSetIdentifier.class);
            assertThat(parameterSetIdentifierNode).isNotNull();
            assertThat(parameterSetIdentifierNode.getChildren()).isEmpty();
            assertThat(parameterSetIdentifierNode.asString()).isEqualTo("256");
        }
    }
}
