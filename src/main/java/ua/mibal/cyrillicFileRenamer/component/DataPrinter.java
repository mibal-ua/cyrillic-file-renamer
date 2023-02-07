/*
 * Copyright (c) 2022. http://t.me/mibal_ua
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package ua.mibal.cyrillicFileRenamer.component;

import java.io.File;
import java.util.List;

/**
 * @author Mykhailo Balakhon
 * @link https://t.me/mibal_ua
 */
public interface DataPrinter {

    void printInfoMessage(String message);

    void printErrorMessage(String message);

    void printWelcomeMessage();

    void printNonProcessedFiles(final File[] directoryFiles,
                                final List<String> notCyrillicSymbols,
                                final List<String> fileAlreadyRenamed,
                                final List<String> fileHaveHiddenName,
                                final List<String> fileHaveAnotherLanguageName,
                                final List<String> reasonsOfFileHaveAnotherLanguageName,
                                final List<String> nonProcessedFiles,
                                final List<String> reasonsOfNonProcessedFiles);

    void exit();

    @FunctionalInterface
    interface ExitHandler {

        void exit();
    }
}
