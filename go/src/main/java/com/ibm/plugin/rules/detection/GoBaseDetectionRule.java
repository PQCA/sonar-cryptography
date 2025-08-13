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
package com.ibm.plugin.rules.detection;

import com.ibm.common.IObserver;
import com.ibm.engine.detection.Finding;
import com.ibm.engine.executive.DetectionExecutive;
import com.ibm.engine.language.go.GoScanContext;
import com.ibm.engine.rule.IDetectionRule;
import com.ibm.mapper.model.INode;
import com.ibm.mapper.reorganizer.IReorganizerRule;
import com.ibm.plugin.GoAggregator;
import com.ibm.plugin.translation.GoTranslationProcess;
import com.ibm.plugin.translation.reorganizer.JavaReorganizerRules;
import com.ibm.rules.IReportableDetectionRule;
import com.ibm.rules.issue.Issue;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import org.sonar.go.impl.ClassDeclarationTreeImpl;
import org.sonar.go.impl.FunctionInvocationTreeImpl;
import org.sonar.go.plugin.InputFileContext;
import org.sonar.go.symbols.Symbol;
import org.sonar.go.visitors.TreeVisitor;
import org.sonar.plugins.go.api.Tree;
import org.sonar.plugins.go.api.checks.GoCheck;
import org.sonar.python.tree.ExpressionStatementImpl;

public abstract class GoBaseDetectionRule extends TreeVisitor<InputFileContext>
        implements IObserver<Finding<GoCheck, Tree, Symbol, InputFileContext>>,
                IReportableDetectionRule<Tree> {

    private final boolean isInventory;
    @Nonnull protected final GoTranslationProcess goTranslationProcess;
    @Nonnull protected final List<IDetectionRule<Tree>> detectionRules;

    protected GoBaseDetectionRule() {
        this.isInventory = false;
        this.detectionRules = GoDetectionRules.rules();
        this.goTranslationProcess = new GoTranslationProcess(JavaReorganizerRules.rules());
    }

    protected GoBaseDetectionRule(
            final boolean isInventory,
            @Nonnull List<IDetectionRule<Tree>> detectionRules,
            @Nonnull List<IReorganizerRule> reorganizerRules) {
        this.isInventory = isInventory;
        this.detectionRules = detectionRules;
        this.goTranslationProcess = new GoTranslationProcess(reorganizerRules);
    }

    /**
     * Returns a list of tree nodes to visit during detection.
     *
     * @return A list of tree node kinds to visit.
     */
    // @Override
    public List<Tree> nodesToVisit() {
        return List.of(
                (Tree) new FunctionInvocationTreeImpl(null, null, null, null),
                (Tree) new ExpressionStatementImpl(null, null),
                (Tree) new ClassDeclarationTreeImpl(null, null, null));
    }

    /**
     * Visits a tree node and applies detection rules to it.
     *
     * @param tree The tree node to visit.
     */
    // @Override
    public void visitNode(@Nonnull Tree tree) {
        detectionRules.forEach(
                rule -> {
                    DetectionExecutive<GoCheck, Tree, Symbol, InputFileContext> detectionExecutive =
                            GoAggregator.getLanguageSupport()
                                    .createDetectionExecutive(
                                            tree, rule, new GoScanContext(this.context));
                    detectionExecutive.subscribe(this);
                    detectionExecutive.start();
                });
    }

    /**
     * On new finding.
     *
     * @param finding A finding containing detection store information.
     */
    @Override
    public void update(@Nonnull Finding<GoCheck, Tree, Symbol, InputFileContext> finding) {
        final List<INode> nodes = goTranslationProcess.initiate(finding.detectionStore());
        if (isInventory) {
            GoAggregator.addNodes(nodes);
        }
        // report
        this.report(finding.getMarkerTree(), nodes)
                .forEach(
                        issue ->
                                finding.detectionStore()
                                        .getScanContext()
                                        .reportIssue(this, issue.tree(), issue.message()));
    }

    @Override
    @Nonnull
    public List<Issue<Tree>> report(
            @Nonnull Tree markerTree, @Nonnull List<INode> translatedNodes) {
        // override by higher level rule, to report an issue
        return Collections.emptyList();
    }
}
