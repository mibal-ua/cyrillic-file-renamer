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

import ua.mibal.cyrillicFileRenamer.component.translators.AbstractLetterTranslator;
import ua.mibal.cyrillicFileRenamer.model.exceptions.DontContainCyrillicSymbolsException;
import ua.mibal.cyrillicFileRenamer.model.exceptions.IllegalLanguageException;
import static java.util.Objects.requireNonNull;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mykhailo Balakhon
 * @link https://t.me/mibal_ua
 */
public class Application {

    private final DataPrinter dataPrinter;

    private final FileManager fileManager;

    private final String pathToCatalog;

    private final AbstractLetterTranslator letterTranslator;

    public Application(final DataPrinter dataPrinter,
                       final FileManager fileManager,
                       final String pathToCatalog,
                       final AbstractLetterTranslator letterTranslator) {
        this.dataPrinter = requireNonNull(dataPrinter);
        this.fileManager = requireNonNull(fileManager);
        this.pathToCatalog = requireNonNull(pathToCatalog);
        this.letterTranslator = requireNonNull(letterTranslator);
    }

    public void start() {
        final File[] directoryFiles = fileManager.getFilesFromDirectory(pathToCatalog);
        fileManager.createResultingDirectory(pathToCatalog);
        final Map<String, Exception> logList = new HashMap<>();

        for (final File sourceFile : directoryFiles) {
            final String oldName = sourceFile.getName(); // with extension
            String newName;
            try {
                newName = letterTranslator.translate(oldName);
            } catch (IllegalLanguageException | DontContainCyrillicSymbolsException e) {
                logList.put(oldName, e);
                continue;
            }
            try {
                fileManager.copyRenamedFile(sourceFile, newName);
            } catch (IOException e) {
                logList.put(oldName, e);
            }
        }
        dataPrinter.outInfo(directoryFiles.length, logList);
        dataPrinter.exit();
    }
}
