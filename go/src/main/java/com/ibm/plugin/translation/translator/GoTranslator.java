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
package com.ibm.plugin.translation.translator;

import com.ibm.engine.model.IValue;
import com.ibm.engine.model.context.IDetectionContext;
import com.ibm.engine.rule.IBundle;
import com.ibm.mapper.ITranslator;
import com.ibm.mapper.model.INode;
import com.ibm.mapper.utils.DetectionLocation;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.sonar.go.plugin.InputFileContext;
import org.sonar.go.symbols.Symbol;
import org.sonar.plugins.go.api.Tree;
import org.sonar.plugins.go.api.checks.GoCheck;

public final class GoTranslator extends ITranslator<GoCheck, Tree, Symbol, InputFileContext> {

    public GoTranslator() {
        // nothing
    }

    @Override
    @Nonnull
    public Optional<INode> translate(
            @Nonnull final IBundle bundleIdentifier,
            @Nonnull final IValue<Tree> value,
            @Nonnull final IDetectionContext detectionValueContext,
            @Nonnull final String filePath) {
        // DetectionLocation detectionLocation =
        //         getDetectionContextFrom(value.getLocation(), bundleIdentifier, filePath);
        // if (detectionLocation == null) {
        //     return Optional.empty();
        // }

        // // cipher context
        // if (detectionValueContext.is(CipherContext.class)) {
        //     JavaCipherContextTranslator javaCipherContextTranslation =
        //             new JavaCipherContextTranslator();
        //     return javaCipherContextTranslation.translate(
        //             bundleIdentifier, value, detectionValueContext, detectionLocation);

        //     // secret key context
        // } else if (detectionValueContext.is(SecretKeyContext.class)) {
        //     JavaSecretKeyContextTranslator javaSecretKeyContextTranslation =
        //             new JavaSecretKeyContextTranslator();
        //     return javaSecretKeyContextTranslation.translate(
        //             bundleIdentifier, value, detectionValueContext, detectionLocation);

        //     // private- / public- / secret- / key context
        // } else if (detectionValueContext.is(KeyContext.class)
        //         || detectionValueContext.is(PublicKeyContext.class)
        //         || detectionValueContext.is(PrivateKeyContext.class)
        //         || detectionValueContext.is(SecretKeyContext.class)) {
        //     JavaKeyContextTranslator javaKeyContextTranslation = new JavaKeyContextTranslator();
        //     return javaKeyContextTranslation.translate(
        //             bundleIdentifier, value, detectionValueContext, detectionLocation);

        //     // key agreement context
        // } else if (detectionValueContext.is(KeyAgreementContext.class)) {
        //     final JavaKeyAgreementContextTranslator javaKeyAgreementContextTranslator =
        //             new JavaKeyAgreementContextTranslator();
        //     return javaKeyAgreementContextTranslator.translate(
        //             bundleIdentifier, value, detectionValueContext, detectionLocation);

        //     // PRNG context
        // } else if (detectionValueContext.is(PRNGContext.class)) {
        //     JavaPRNGContextTranslator javaPRNGContextTranslation = new
        // JavaPRNGContextTranslator();
        //     return javaPRNGContextTranslation.translate(
        //             bundleIdentifier, value, detectionValueContext, detectionLocation);

        //     // digest context
        // } else if (detectionValueContext.is(DigestContext.class)) {
        //     JavaDigestContextTranslator javaDigestContextTranslation =
        //             new JavaDigestContextTranslator();
        //     return javaDigestContextTranslation.translate(
        //             bundleIdentifier, value, detectionValueContext, detectionLocation);

        //     // signature context
        // } else if (detectionValueContext.is(SignatureContext.class)) {
        //     JavaSignatureContextTranslator javaSignatureContextTranslation =
        //             new JavaSignatureContextTranslator();
        //     return javaSignatureContextTranslation.translate(
        //             bundleIdentifier, value, detectionValueContext, detectionLocation);

        //     // mac context
        // } else if (detectionValueContext.is(MacContext.class)) {
        //     JavaMacContextTranslator javaMacContextTranslation = new JavaMacContextTranslator();
        //     return javaMacContextTranslation.translate(
        //             bundleIdentifier, value, detectionValueContext, detectionLocation);

        //     // algorithm parameter context
        // } else if (detectionValueContext.is(AlgorithmParameterContext.class)) {
        //     JavaAlgorithmParameterContextTranslator javaAlgorithmParameterContextTranslation =
        //             new JavaAlgorithmParameterContextTranslator();
        //     return javaAlgorithmParameterContextTranslation.translate(
        //             bundleIdentifier, value, detectionValueContext, detectionLocation);

        //     // protocol
        // } else if (detectionValueContext.is(ProtocolContext.class)) {
        //     final JavaProtocolContextTranslator javaProtocolContextTranslator =
        //             new JavaProtocolContextTranslator();
        //     return javaProtocolContextTranslator.translate(
        //             bundleIdentifier, value, detectionValueContext, detectionLocation);
        // }
        return Optional.empty();
    }

    /**
     * This function gets a detection context from the specified Tree location and file path. <br>
     * <li>It retrieves the first and last tokens in the location, checks if both are not null, and
     *     then extracts the range of the first token. From this, it obtains the line number and
     *     offset.
     * <li>It also determines the kind of the location (e.g., NEW_CLASS, METHOD_INVOCATION,
     *     ENUM_CONSTANT) and populates a list of keywords accordingly.
     * <li>Finally, it creates a new DetectionContext object using the obtained information and
     *     returns it. If any of the conditions are not met or if an error occurs during the
     *     process, it returns null.
     */
    @Override
    @Nullable public DetectionLocation getDetectionContextFrom(
            @Nonnull Tree location, @Nonnull IBundle bundle, @Nonnull String filePath) {
        // SyntaxToken firstToken = location.firstToken();
        // SyntaxToken lastToken = location.lastToken();
        // if (firstToken != null && lastToken != null) {
        //     Range rangeFirst = firstToken.range();
        //     Position start = rangeFirst.start();
        //     int lineNumber = start.line();
        //     int offset = start.columnOffset();
        //     List<String> keywords = List.of();
        //     switch (location.kind()) {
        //         case NEW_CLASS:
        //             keywords =
        //                     List.of(
        //                             ((NewClassTree) location).methodSymbol().signature(),
        //                             ((NewClassTree) location).methodSymbol().name(),
        //                             ((NewClassTree) location).identifier().toString());
        //             break;
        //         case METHOD_INVOCATION:
        //             keywords =
        //                     List.of(
        //                             ((MethodInvocationTree) location).methodSymbol().signature(),
        //                             ((MethodInvocationTree) location).methodSymbol().name());
        //             break;
        //         case ENUM_CONSTANT:
        //             keywords =
        //                     List.of(
        //                             ((EnumConstantTree) location)
        //                                     .initializer()
        //                                     .methodSymbol()
        //                                     .signature(),
        //                             ((EnumConstantTree) location)
        //                                     .initializer()
        //                                     .methodSymbol()
        //                                     .name());
        //             break;
        //         default:
        //             // nothing
        //     }
        //     return new DetectionLocation(filePath, lineNumber, offset, keywords, bundle);
        // }
        return null;
    }
}
