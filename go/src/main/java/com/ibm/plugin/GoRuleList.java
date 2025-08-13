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
package com.ibm.plugin;

import com.ibm.plugin.rules.GoInventoryRule;
import com.ibm.plugin.rules.GoNoMD5UseRule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import org.sonar.plugins.go.api.checks.GoCheck;

public final class GoRuleList {

    private GoRuleList() {}

    public static @Nonnull List<Class<?>> getChecks() {
        List<Class<? extends GoCheck>> checks = new ArrayList<>();
        checks.addAll(getGoChecks());
        checks.addAll(getGoTestChecks());
        return Collections.unmodifiableList(checks);
    }

    /** These rules are going to target MAIN code only */
    public static @Nonnull List<Class<? extends GoCheck>> getGoChecks() {
        return List.of(GoInventoryRule.class, GoNoMD5UseRule.class);
    }

    /** These rules are going to target TEST code only */
    public static @Nonnull List<Class<? extends GoCheck>> getGoTestChecks() {
        return List.of();
    }
}
