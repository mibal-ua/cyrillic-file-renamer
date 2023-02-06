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
import ua.mibal.cyrillicFileRenamer.model.DynaStringArray;
import ua.mibal.cyrillicFileRenamer.model.exceptions.IllegalLanguageException;
import ua.mibal.cyrillicFileRenamer.model.exceptions.IllegalNameException;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Mykhailo Balakhon
 * @link t.me/mibal_ua
 */
public class Application {

    private final DataPrinter dataPrinter;

    private final LocalFileManager localFileManager;

    private final String pathToCatalog;

    private final LetterTranslator letterTranslator;

    public Application(final DataPrinter dataPrinter, final LocalFileManager localFileManager,
                       final String pathToCatalog, final LetterTranslator letterTranslator) {

        this.dataPrinter = dataPrinter;
        this.localFileManager = localFileManager;
        this.pathToCatalog = pathToCatalog;
        this.letterTranslator = letterTranslator;
    }

    public void start() {
        File[] directoryFiles = localFileManager.getFilesFromDirectory(pathToCatalog);
        File newDirectory = new File(pathToCatalog + "/renamedToLatin");
        newDirectory.mkdir();
        DynaStringArray nonProcessedFiles = new DynaStringArray();
        DynaStringArray reasonsOfNonProcessedFiles = new DynaStringArray();
        DynaStringArray notCyrillicSymbols = new DynaStringArray();
        DynaStringArray fileAlreadyRenamed = new DynaStringArray();
        DynaStringArray fileHaveHiddenName = new DynaStringArray();
        DynaStringArray fileHaveAnotherLanguageName = new DynaStringArray();
        DynaStringArray reasonsOfFileHaveAnotherLanguageName = new DynaStringArray();
        String[] ignoredFileNames = {newDirectory.getName(), ".DS_Store",
            "Thumbs.db", "$RECYCLE.BIN", "desktop.ini", ".localized"
        };
        for (final File file : directoryFiles) {
            String oldName = file.getName(); //this is name with extension
            if (!isIgnoreFile(file, ignoredFileNames)) {
                if (oldName.charAt(0) != '.') {
                    String newName;
                    try {
                        newName = letterTranslator.translateName(oldName);
                    } catch (IllegalNameException e) {
                        notCyrillicSymbols.add(oldName);
                        continue;
                    } catch (IllegalLanguageException e) {
                        fileHaveAnotherLanguageName.add(oldName);
                        reasonsOfFileHaveAnotherLanguageName.add(e.getMessage());
                        continue;
                    }
                    try {
                        Files.copy(file.toPath(), Path.of((newDirectory.toPath() + "/" + newName)));
                    } catch (IOException e) {
                        if (e.getClass() == FileAlreadyExistsException.class) {
                            fileAlreadyRenamed.add(oldName);
                        } else {
                            nonProcessedFiles.add(oldName);
                            reasonsOfNonProcessedFiles.add(e.getClass().getSimpleName());
                        }
                    }
                } else {
                    fileHaveHiddenName.add(oldName);
                }
            }
        }
        dataPrinter.printNonProcessedFiles(directoryFiles, ignoredFileNames,
            notCyrillicSymbols.toArray(),
            fileAlreadyRenamed.toArray(),
            fileHaveHiddenName.toArray(),
            fileHaveAnotherLanguageName.toArray(),
            reasonsOfFileHaveAnotherLanguageName.toArray(),
            nonProcessedFiles.toArray(),
            reasonsOfNonProcessedFiles.toArray());
        dataPrinter.exit();
    }

    private boolean isIgnoreFile(final File file, final String[] ignoredFileNames) {
        String name = file.getName();
        for (final String ignoredFileName : ignoredFileNames) {
            if (name.equalsIgnoreCase(ignoredFileName)) {
                return true;
            }
        }
        return name.contains("cyrillic-file-renamer-");
    }

}
