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
package com.ibm.mapper.model;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface PasswordBasedEncryption extends IPrimitive {

    @Nonnull
    default Optional<Mac> getMac() {
        INode node = this.getChildren().get(Mac.class);
        if (node == null) {
            return Optional.empty();
        }
        return Optional.of((Mac) node);
    }

    @Nonnull
    default Optional<MessageDigest> getDigest() {
        INode node = this.getChildren().get(MessageDigest.class);
        if (node == null) {
            return Optional.empty();
        }
        return Optional.of((MessageDigest) node);
    }

    @Nonnull
    default Optional<Cipher> getCipher() {
        return this.getChildren().values().stream()
                .map(
                        n -> {
                            if (n instanceof Cipher cipher) {
                                return cipher;
                            } else {
                                return null;
                            }
                        })
                .filter(Objects::nonNull)
                .findFirst();
    }
}
