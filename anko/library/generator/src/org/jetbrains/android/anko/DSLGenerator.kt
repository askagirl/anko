/*
 * Copyright 2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.android.anko

import org.jetbrains.android.anko.config.AnkoBuilderContext
import org.jetbrains.android.anko.config.CHECK_MODE
import org.jetbrains.android.anko.config.WithContext
import org.jetbrains.android.anko.config.get
import org.jetbrains.android.anko.generator.GenerationState
import org.jetbrains.android.anko.render.RenderFacade
import org.jetbrains.android.anko.utils.ClassTree
import org.jetbrains.android.anko.writer.CheckWriter
import org.jetbrains.android.anko.writer.GeneratorWriter
import java.io.File

class DSLGenerator(
    val platformJars: List<File>,
    val versionJars: List<File>,
    override val context: AnkoBuilderContext,
    val classTree: ClassTree? = null
): Runnable, WithContext {
    override fun run() {
        val classTree = this.classTree ?: ClassProcessor(platformJars, versionJars).genClassTree()
        val generationState = GenerationState(classTree, context)
        val renderer = RenderFacade(generationState)
        val writer = if (context.configuration[CHECK_MODE]) {
            CheckWriter(renderer)
        } else {
            GeneratorWriter(renderer)
        }
        writer.write()
    }
}