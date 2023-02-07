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
import static java.util.Objects.requireNonNull;
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

    private final FileManager fileManager;

    private final String pathToCatalog;

    private final LetterTranslator letterTranslator;

    private File resultingDirectory;

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
        resultingDirectory = fileManager.createResultingDirectory(pathToCatalog);

        DynaStringArray nonProcessedFiles = new DynaStringArray();
        DynaStringArray reasonsOfNonProcessedFiles = new DynaStringArray();
        DynaStringArray notCyrillicSymbols = new DynaStringArray();
        DynaStringArray fileAlreadyRenamed = new DynaStringArray();
        DynaStringArray fileHaveHiddenName = new DynaStringArray();
        DynaStringArray fileHaveAnotherLanguageName = new DynaStringArray();
        DynaStringArray reasonsOfFileHaveAnotherLanguageName = new DynaStringArray();

        for (final File file : directoryFiles) {
            String oldName = file.getName(); //this is name with extension
            if (oldName.equals(resultingDirectory.getName())) {
                continue;
            }
            if (!fileManager.isIgnoredFile(oldName)) {
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
                    Files.copy(file.toPath(), Path.of((resultingDirectory.toPath() + "/" + newName)));
                } catch (FileAlreadyExistsException e) {
                    fileAlreadyRenamed.add(oldName);
                } catch (IOException e) {
                    nonProcessedFiles.add(oldName);
                    reasonsOfNonProcessedFiles.add(e.getClass().getSimpleName());
                }
            } else {
                fileHaveHiddenName.add(oldName);
            }
        }

        dataPrinter.printNonProcessedFiles(
            directoryFiles,
            notCyrillicSymbols.toArray(),
            fileAlreadyRenamed.toArray(),
            fileHaveHiddenName.toArray(),
            fileHaveAnotherLanguageName.toArray(),
            reasonsOfFileHaveAnotherLanguageName.toArray(),
            nonProcessedFiles.toArray(),
            reasonsOfNonProcessedFiles.toArray());

        dataPrinter.exit();
    }
}
