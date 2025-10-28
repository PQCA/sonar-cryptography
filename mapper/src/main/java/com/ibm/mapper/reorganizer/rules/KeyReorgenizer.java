/*
 * Sonar Cryptography Plugin
 * Copyright (C) 2025 PQCA
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
package com.ibm.mapper.reorganizer.rules;

import com.ibm.mapper.model.Algorithm;
import com.ibm.mapper.model.BlockCipher;
import com.ibm.mapper.model.INode;
import com.ibm.mapper.model.Key;
import com.ibm.mapper.model.PrivateKey;
import com.ibm.mapper.model.PublicKey;
import com.ibm.mapper.model.SecretKey;
import com.ibm.mapper.model.functionality.KeyGeneration;
import com.ibm.mapper.reorganizer.IReorganizerRule;
import com.ibm.mapper.reorganizer.builder.ReorganizerRuleBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KeyReorgenizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyReorgenizer.class);

    private KeyReorgenizer() {
        // private
    }

    /**
     * A reorganizer rule for moving a key object under an Algorithm as its parent and replacing the
     * inner algorithm (if exists) with the outer one.
     *
     * <p>This rule applies when an Algorithm node has a SecretKey child, and that SecretKey
     * contains an inner algorithm. The rule:
     *
     * <ul>
     *   <li>Creates a new SecretKey based on the outer (parent) algorithm
     *   <li>Moves all children from the old SecretKey to the new one
     *   <li>Replaces the inner algorithm with the outer algorithm
     * </ul>
     */
    @Nonnull
    public static final IReorganizerRule MOVE_KEY_UNDER_ALGORITHM_AND_REPLACE_INNER_ALGORITHM =
            new ReorganizerRuleBuilder()
                    .createReorganizerRule("MOVE_KEY_UNDER_ALGORITHM_AND_REPLACE_INNER_ALGORITHM")
                    .forNodeKind(BlockCipher.class)
                    .withDetectionCondition(
                            (node, parent, roots) -> {
                                // Check if the Algorithm node has a SecretKey child
                                Optional<INode> secretKeyOpt = node.hasChildOfType(SecretKey.class);
                                if (secretKeyOpt.isEmpty()) {
                                    return false;
                                }

                                // Check if the SecretKey has a BlockCipher child (inner algorithm)
                                INode secretKey = secretKeyOpt.get();
                                return secretKey.hasChildOfType(BlockCipher.class).isPresent();
                            })
                    .perform(
                            (node, parent, roots) -> {
                                // Get the SecretKey child
                                final Optional<INode> secretKeyOpt =
                                        node.hasChildOfType(SecretKey.class);
                                if (secretKeyOpt.isEmpty()) {
                                    return roots;
                                }
                                // remove secret key from parent
                                node.removeChildOfType(SecretKey.class);

                                final SecretKey secretKey = new SecretKey((Algorithm) node);
                                // Add all the children to the new node
                                for (Map.Entry<Class<? extends INode>, INode> childKeyValue :
                                        secretKeyOpt.get().getChildren().entrySet()) {
                                    secretKey.put(childKeyValue.getValue());
                                }

                                // Put the parent algorithm into the secret key and replace the
                                // inner
                                secretKey.removeChildOfType(BlockCipher.class);
                                secretKey.put(node);

                                return new ArrayList<>(Collections.singleton(secretKey));
                            });

    @Nonnull
    public static final IReorganizerRule SPECIFY_KEY_TYPE_BY_LOOKING_AT_KEY_GENERATION =
            new ReorganizerRuleBuilder()
                    .createReorganizerRule("MAKE_RSA_TO_SIGNATURE")
                    .forNodeKind(Key.class)
                    .withDetectionCondition(
                            (node, parent, roots) ->
                                    node.hasChildOfType(KeyGeneration.class).isPresent())
                    .perform(
                            (node, parent, roots) -> {
                                final Optional<INode> optionalKeyGen =
                                        node.hasChildOfType(KeyGeneration.class);
                                if (optionalKeyGen.isEmpty()) {
                                    return null;
                                }

                                final KeyGeneration keyGeneration =
                                        (KeyGeneration) optionalKeyGen.get();
                                if (keyGeneration.getSpecification().isEmpty()) {
                                    return null;
                                }
                                final KeyGeneration.Specification specification =
                                        keyGeneration.getSpecification().get();
                                final INode newNode =
                                        switch (specification) {
                                            case PUBLIC_KEY -> new PublicKey((Key) node);
                                            case PRIVATE_KEY -> new PrivateKey((Key) node);
                                            case SECRET_KEY -> new SecretKey((Key) node);
                                        };

                                // Add all the children to the new node
                                for (Map.Entry<Class<? extends INode>, INode> childKeyValue :
                                        keyGeneration.getChildren().entrySet()) {
                                    newNode.put(childKeyValue.getValue());
                                }
                                // remove key gen
                                node.removeChildOfType(KeyGeneration.class);
                                if (parent == null) {
                                    // `node` is a root node
                                    // Create a copy of the root nodes
                                    List<INode> rootsCopy = new ArrayList<>(roots);
                                    for (int i = 0; i < rootsCopy.size(); i++) {
                                        if (rootsCopy.get(i).equals(node)) {
                                            rootsCopy.set(i, newNode);
                                            break;
                                        }
                                    }
                                    return rootsCopy;
                                } else {
                                    // Replace the previous node
                                    parent.put(newNode);
                                    return roots;
                                }
                            });
}
