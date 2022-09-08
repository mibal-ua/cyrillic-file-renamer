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
package ua.mibal.cyrillicFileRenamer;

import ua.mibal.cyrillicFileRenamer.component.*;
import ua.mibal.cyrillicFileRenamer.component.console.ConsoleDataPrinter;
import ua.mibal.cyrillicFileRenamer.component.console.ConsoleInputReader;
import ua.mibal.cyrillicFileRenamer.model.DynaStringArray;
import ua.mibal.cyrillicFileRenamer.model.Lang;
import ua.mibal.cyrillicFileRenamer.model.exception.IllegalNameException;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.String.format;
import static ua.mibal.cyrillicFileRenamer.component.PathOperator.testPath;
import static ua.mibal.cyrillicFileRenamer.model.Lang.RU;
import static ua.mibal.cyrillicFileRenamer.model.Lang.UA;

/**
 * @author Michael Balakhon
 * @link http://t.me/mibal_ua
 */
public class Application {

    private final DataPrinter dataPrinter = new ConsoleDataPrinter();

    private LetterTranslator letterTranslator;

    private static String pathToCatalog;

    private static Lang lang;

    public Application(final String[] args) {
        dataPrinter.printWelcomeMessage();
        if (args.length != 0) {
            ArgumentParser parser = new ArgumentParser();
            parser.parse(args);
            pathToCatalog = testPath(parser.getPath());
            lang = parser.getLang();
        }
        InputReader inputReader = new ConsoleInputReader();
        if (pathToCatalog == null) {
            dataPrinter.printInfoMessage("Enter path to catalog with files:");
            while (true) {
                String userPath = inputReader.read().trim();
                dataPrinter.printInfoMessage("");
                if (userPath.equalsIgnoreCase("/exit"))
                    dataPrinter.exit();
                String normalUserPath = testPath(userPath);
                if (normalUserPath != null) {
                    pathToCatalog = normalUserPath;
                    break;
                } else {
                    dataPrinter.printErrorMessage(format("Incorrect path '%s'", userPath));
                    dataPrinter.printInfoMessage(
                            "You must enter path like this: " +
                            OSDetector.detectOS().getExamplePath());
                }
            }
        } else {
            dataPrinter.printInfoMessage("Path: " + pathToCatalog);
        }
        if (lang == null) {
            while (true) {
                dataPrinter.printInfoMessage("Enter language of files: 'RU' or 'UA'");
                String userLang = inputReader.read().trim();
                dataPrinter.printInfoMessage("");
                if (userLang.equalsIgnoreCase("/exit")) {
                    dataPrinter.exit();
                } else if (userLang.equalsIgnoreCase(RU.name()) || userLang.equalsIgnoreCase(UA.name())) {
                    lang = Lang.valueOf(userLang.toUpperCase());
                    letterTranslator = new LetterTranslator(lang);
                    break;
                } else {
                    dataPrinter.printInfoMessage(format(
                            "You enter unsupported language '%s'." + '\n', userLang
                    ));
                }
            }
        } else {
            dataPrinter.printInfoMessage("Language: " + lang.name());
        }
    }

    public void start() {
        File[] directoryFiles = FileManager.getFilesFromDirectory(pathToCatalog, dataPrinter);
        File newDirectory = new File(pathToCatalog + "/renamedToLatin");
        newDirectory.mkdir();
        DynaStringArray nonProcessedFiles = new DynaStringArray();
        DynaStringArray reasonsOfNonProcessedFiles = new DynaStringArray();
        for (final File file : directoryFiles) {
            String oldName = file.getName(); //this is name with extension
            if (!(isIgnoreFile(file) || oldName.equals(newDirectory.getName()))) {
                if (oldName.charAt(0) != '.') {
                    String newName;
                    try {
                        newName = letterTranslator.translateName(oldName);
                    } catch (IllegalNameException e) {
                        nonProcessedFiles.add(oldName);
                        reasonsOfNonProcessedFiles.add(e.getMessage());
                        continue;
                    }
                    try {
                        Files.copy(file.toPath(), Path.of((newDirectory.toPath() + "/" + newName)));
                    } catch (IOException e) {
                        nonProcessedFiles.add(oldName);
                        if (e.getClass() == FileAlreadyExistsException.class) {
                            reasonsOfNonProcessedFiles.add("File already renamed");
                        } else {
                            reasonsOfNonProcessedFiles.add(e.getClass().getSimpleName());
                        }
                    }
                } else {
                    nonProcessedFiles.add(oldName);
                    reasonsOfNonProcessedFiles.add("File have hidden name");
                }
            }
        }
        dataPrinter.printNonProcessedFiles(nonProcessedFiles.toArray(),
                reasonsOfNonProcessedFiles.toArray(), directoryFiles);
        dataPrinter.exit();
    }

    private boolean isIgnoreFile(final File file) {
        String name = file.getName();
        return (name.contains("cyrillic-file-renamer-") ||
                name.equalsIgnoreCase(".DS_Store") ||
                name.equalsIgnoreCase("Thumbs.db") ||
                name.equalsIgnoreCase("$RECYCLE.BIN") ||
                name.equalsIgnoreCase("desktop.ini")||
                name.equalsIgnoreCase(".localized"));
    }
}