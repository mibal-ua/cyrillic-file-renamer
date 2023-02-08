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

import ua.mibal.cyrillicFileRenamer.component.translators.LetterTranslator;
import ua.mibal.cyrillicFileRenamer.model.exceptions.IllegalLanguageException;
import ua.mibal.cyrillicFileRenamer.model.exceptions.IllegalNameException;
import static java.util.Objects.requireNonNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mykhailo Balakhon
 * @link t.me/mibal_ua
 */
public class Application {

    private final DataPrinter dataPrinter;

    private final FileManager fileManager;

    private final String pathToCatalog;

    private final LetterTranslator letterTranslator;

    public Application(final DataPrinter dataPrinter,
                       final FileManager fileManager,
                       final String pathToCatalog,
                       final LetterTranslator letterTranslator) {
        this.dataPrinter = requireNonNull(dataPrinter);
        this.fileManager = requireNonNull(fileManager);
        this.pathToCatalog = requireNonNull(pathToCatalog);
        this.letterTranslator = requireNonNull(letterTranslator);
    }

    public void start() {
        final File[] directoryFiles = fileManager.getFilesFromDirectory(pathToCatalog);
        final File resultingDirectory = fileManager.createResultingDirectory(pathToCatalog);
        final Map<String, List<String>> logList = initLogList();

        for (final File sourceFile : directoryFiles) {
            final String oldName = sourceFile.getName(); //this is name with extension
            if (oldName.equals(resultingDirectory.getName())) {
                continue;
            }
            if (fileManager.isIgnoredFile(oldName)) {
                logList.get("fileHaveHiddenName").add(oldName);
                continue;
            }
            String newName;
            try {
                newName = letterTranslator.translateName(oldName);
            } catch (IllegalNameException e) {
                logList.get("notCyrillicSymbols").add(oldName);
                continue;
            } catch (IllegalLanguageException e) {
                logList.get("fileHaveAnotherLanguageName").add(oldName);
                logList.get("reasonsOfFileHaveAnotherLanguageName").add(e.getMessage());
                continue;
            }
            try {
                fileManager.createRenamedFile(sourceFile, newName, resultingDirectory);
            } catch (FileAlreadyExistsException e) {
                logList.get("fileAlreadyRenamed").add(oldName);
            } catch (IOException e) {
                logList.get("nonProcessedFiles").add(oldName);
                logList.get("reasonsOfNonProcessedFiles").add(e.getClass().getSimpleName());
            }
        }
        dataPrinter.printNonProcessedFiles(directoryFiles, logList);
        dataPrinter.exit();
    }

    private static Map<String, List<String>> initLogList() {
        final Map<String, List<String>> map = new HashMap<>();
        map.put("nonProcessedFiles", new ArrayList<>());
        map.put("reasonsOfNonProcessedFiles", new ArrayList<>());
        map.put("notCyrillicSymbols", new ArrayList<>());
        map.put("fileAlreadyRenamed", new ArrayList<>());
        map.put("fileHaveHiddenName", new ArrayList<>());
        map.put("fileHaveAnotherLanguageName", new ArrayList<>());
        map.put("reasonsOfFileHaveAnotherLanguageName", new ArrayList<>());
        return map;
    }
}
