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

import com.ibm.engine.detection.IType;
import com.ibm.engine.detection.MatchContext;
import com.ibm.engine.language.ILanguageTranslation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.go.utils.ExpressionUtils;
import org.sonar.plugins.go.api.FunctionInvocationTree;
import org.sonar.plugins.go.api.IdentifierTree;
import org.sonar.plugins.go.api.MemberSelectTree;
import org.sonar.plugins.go.api.Tree;

public class GoLanguageTranslation implements ILanguageTranslation<Tree> {
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(GoLanguageTranslation.class);

    @Nonnull
    @Override
    public Optional<String> getMethodName(
            @Nonnull MatchContext matchContext, @Nonnull Tree methodInvocation) {
        if (methodInvocation instanceof FunctionInvocationTree functionInvocationTree) {
            return ExpressionUtils.getMemberSelectOrIdentifierName(functionInvocationTree);
        }
        return Optional.empty();
    }

    @SuppressWarnings("java:S3776")
    @Nonnull
    @Override
    public Optional<IType> getInvokedObjectTypeString(
            @Nonnull MatchContext matchContext, @Nonnull Tree methodInvocation) {
        /*
         * ECJ Unable to resolve type junit.framework.TestCase
         *
         * Since the implementation of Hooks the MethodMatcher and therefore this function is used to
         * determine if a hook is invoked or not. Since Hooks persist over the whole scan (not deleted per module),
         * this check happens even on a module switch. Sonar-java uses ECJ to be able to resolve subtypes. This will
         * fail and throw when a hook-check is done, but the type is not part of the currently scanned module.
         * This failure would throw an error message into the logs, which could distract a user.
         *
         * Therefore, we excluded the subType check for hook invocation checks to stop the sonar-java-frontend from
         * throwing those errors.
         */
        if (methodInvocation instanceof FunctionInvocationTree functionInvocationTree) {
            Tree expressionTree = functionInvocationTree.memberSelect();
            if (expressionTree instanceof MemberSelectTree memberSelectExpressionTree) {
                if (memberSelectExpressionTree.expression()
                        instanceof FunctionInvocationTree functionInvocationTree1) {
                    return getMethodReturnTypeString(matchContext, functionInvocationTree1);
                }

                return Optional.of(memberSelectExpressionTree.expression())
                        .map(
                                tree ->
                                        string -> {
                                            if (matchContext.isHookContext()
                                                    || matchContext.objectShouldMatchExactTypes()) {
                                                return tree.symbolType().is(string);
                                            }
                                            return tree.symbolType().is(string)
                                                    || tree.symbolType().isSubtypeOf(string);
                                        });
            }

            if (functionInvocationTree.memberSelect().type().isUnknown()) {
                return Optional.ofNullable(functionInvocationTree.memberSelect().enclosingClass())
                        .map(
                                tree ->
                                        string -> {
                                            if (matchContext.isHookContext()
                                                    || matchContext.objectShouldMatchExactTypes()) {
                                                return tree.type().is(string);
                                            }
                                            return tree.type().is(string)
                                                    || tree.type().isSubtypeOf(string);
                                        });
            }
            return Optional.of(functionInvocationTree.memberSelect())
                    .map(
                            tree ->
                                    string -> {
                                        if (matchContext.isHookContext()
                                                || matchContext.objectShouldMatchExactTypes()) {
                                            return tree.type().is(string);
                                        }
                                        return tree.type().is(string)
                                                || tree.type().isSubtypeOf(string);
                                    });
        }
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<IType> getMethodReturnTypeString(
            @Nonnull MatchContext matchContext, @Nonnull Tree methodInvocation) {
        if (methodInvocation instanceof FunctionInvocationTree functionInvocationTree) {
            return Optional.of(functionInvocationTree.memberSelect())
                    .map(
                            tree ->
                                    string -> {
                                        if (matchContext.isHookContext()
                                                || matchContext.objectShouldMatchExactTypes()) {
                                            return tree.returnType().type().is(string);
                                        }
                                        return tree.returnType().type().is(string)
                                                || tree.returnType().type().isSubtypeOf(string);
                                    });
        }
        return Optional.empty();
    }

    @Nonnull
    @Override
    public List<IType> getMethodParameterTypes(
            @Nonnull MatchContext matchContext, @Nonnull Tree methodInvocation) {
        List<Tree> arguments;
        if (methodInvocation instanceof FunctionInvocationTree functionInvocationTree) {
            arguments = functionInvocationTree.arguments();
        } else {
            return Collections.emptyList();
        }

        if (arguments.isEmpty()) {
            return Collections.emptyList();
        }

        final List<Boolean> parameterMatchExactTypes =
                matchContext.parametersShouldMatchExactTypes();
        List<Boolean> matchMatrix = parameterMatchExactTypes;
        if (parameterMatchExactTypes.size() != arguments.size()) {
            final Boolean[] defaults = new Boolean[arguments.size()];
            Arrays.fill(defaults, false);
            matchMatrix = Arrays.asList(defaults);
        }

        final List<IType> types = new ArrayList<>();
        for (int i = 0; i < arguments.size(); i++) {
            final Tree argument = arguments.get(i);
            final boolean exactMatch = matchMatrix.get(i);

            if (argument instanceof FunctionInvocationTree functionInvocationTree1) {
                Optional<IType> returnType =
                        getMethodReturnTypeString(
                                new MatchContext(
                                        matchContext.isHookContext(),
                                        exactMatch,
                                        Collections.emptyList()),
                                functionInvocationTree1);
                if (returnType.isPresent()) {
                    types.add(returnType.get());
                    continue;
                }
            }

            types.add(
                    string -> {
                        if (matchContext.isHookContext() || exactMatch) {
                            return argument.symbolType().is(string);
                        }
                        return argument.symbolType().is(string)
                                || argument.symbolType().isSubtypeOf(string);
                    });
        }
        return types;
    }

    @Nonnull
    @Override
    public Optional<String> resolveIdentifierAsString(
            @Nonnull MatchContext matchContext, @Nonnull Tree identifier) {
        if (identifier instanceof IdentifierTree identifierTree) {
            return Optional.of(identifierTree.name());
        }
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<String> getEnumIdentifierName(
            @Nonnull MatchContext matchContext, @Nonnull Tree enumIdentifier) {
        // unclear
        return resolveIdentifierAsString(matchContext, enumIdentifier);
    }

    @Nonnull
    @Override
    public Optional<String> getEnumClassName(
            @Nonnull MatchContext matchContext, @Nonnull Tree enumClass) {
        // unclear
        return Optional.empty();
    }
}
