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
package com.ibm.engine.language.go;

import com.ibm.engine.detection.DetectionStore;
import com.ibm.engine.detection.EnumMatcher;
import com.ibm.engine.detection.Handler;
import com.ibm.engine.detection.IBaseMethodVisitorFactory;
import com.ibm.engine.detection.IDetectionEngine;
import com.ibm.engine.detection.MatchContext;
import com.ibm.engine.detection.MethodMatcher;
import com.ibm.engine.executive.DetectionExecutive;
import com.ibm.engine.language.ILanguageSupport;
import com.ibm.engine.language.ILanguageTranslation;
import com.ibm.engine.language.IScanContext;
import com.ibm.engine.rule.IDetectionRule;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.go.plugin.InputFileContext;
import org.sonar.go.symbols.Symbol;
import org.sonar.go.utils.ExpressionUtils;
import org.sonar.plugins.go.api.ExpressionStatementTree;
import org.sonar.plugins.go.api.FunctionDeclarationTree;
import org.sonar.plugins.go.api.Tree;
import org.sonar.plugins.go.api.checks.GoCheck;

public final class GoLanguageSupport
        implements ILanguageSupport<GoCheck, Tree, Symbol, InputFileContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoLanguageSupport.class);
    @Nonnull private final Handler<GoCheck, Tree, Symbol, InputFileContext> handler;

    public GoLanguageSupport() {
        this.handler = new Handler<>(this);
    }

    @Nonnull
    @Override
    public ILanguageTranslation<Tree> translation() {
        return new GoLanguageTranslation();
    }

    @Nonnull
    // @Override
    public DetectionExecutive<GoCheck, Tree, Symbol, InputFileContext> createDetectionExecutive(
            @Nonnull Tree tree,
            @Nonnull IDetectionRule<Tree> detectionRule,
            @Nonnull IScanContext<GoCheck, Tree> scanContext) {
        return new DetectionExecutive<>(tree, detectionRule, scanContext, this.handler);
    }

    @Nonnull
    @Override
    public IDetectionEngine<Tree, Symbol> createDetectionEngineInstance(
            @Nonnull DetectionStore<GoCheck, Tree, Symbol, InputFileContext> detectionStore) {
        return new GoDetectionEngine(detectionStore, this.handler);
    }

    @Nonnull
    @Override
    public IBaseMethodVisitorFactory<Tree, Symbol> getBaseMethodVisitorFactory() {
        return GoBaseMethodVisitor::new;
    }

    @Nonnull
    @Override
    public Optional<Tree> getEnclosingMethod(@Nonnull Tree expression) {
        if (expression instanceof ExpressionStatementTree expressionTree) {
            return Optional.ofNullable(ExpressionUtils.getEnclosingMethod(expressionTree));
        }
        return Optional.empty();
    }

    @Nullable @Override
    public MethodMatcher<Tree> createMethodMatcherBasedOn(@Nonnull Tree methodDefinition) {
        if (methodDefinition instanceof FunctionDeclarationTree functionDeclaration) {
            Symbol.TypeSymbol enclosingClass = functionDeclaration.symbol().enclosingClass();
            if (enclosingClass == null) {
                return null;
            }
            ClassTree classDeclaration = enclosingClass.declaration();
            if (classDeclaration == null) {
                return null;
            }
            String invocationObjectName = classDeclaration.symbol().type().fullyQualifiedName();

            try {
                Tree returnType = functionDeclaration.returnType();
                if (returnType == null) {
                    return null;
                }
                String name = functionDeclaration.receiverName();
                String[] parameters =
                        functionDeclaration.parameters().stream()
                                .map(para -> para.type().symbolType().fullyQualifiedName())
                                .toArray(String[]::new);
                LinkedList<String> parameterTypeList = new LinkedList<>(Arrays.asList(parameters));

                return new MethodMatcher<>(invocationObjectName, name, parameterTypeList);
            } catch (Exception e) {
                LOGGER.error(e.getLocalizedMessage());
                return null;
            }
        }
        return null;
    }

    @Nullable @Override
    public EnumMatcher<Tree> createSimpleEnumMatcherFor(
            @Nonnull Tree enumIdentifier, @Nonnull MatchContext matchContext) {
        Optional<String> enumIdentifierName =
                translation().getEnumIdentifierName(matchContext, enumIdentifier);
        return enumIdentifierName.<EnumMatcher<Tree>>map(EnumMatcher::new).orElse(null);
    }
}
