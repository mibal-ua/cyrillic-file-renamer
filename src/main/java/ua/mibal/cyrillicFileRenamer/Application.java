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
import ua.mibal.cyrillicFileRenamer.component.console.translators.*;
import ua.mibal.cyrillicFileRenamer.model.DynaStringArray;
import ua.mibal.cyrillicFileRenamer.model.exceptions.IllegalLanguageException;
import ua.mibal.cyrillicFileRenamer.model.exceptions.IllegalNameException;
import ua.mibal.cyrillicFileRenamer.model.programMode.Lang;
import ua.mibal.cyrillicFileRenamer.model.programMode.LetterStandard;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.String.format;
import static ua.mibal.cyrillicFileRenamer.component.PathOperator.testPath;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Lang.RU;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Lang.UA;
import static ua.mibal.cyrillicFileRenamer.model.programMode.LetterStandard.EXTENDED;
import static ua.mibal.cyrillicFileRenamer.model.programMode.LetterStandard.OFFICIAL;

/**
 * @author Michael Balakhon
 * @link http://t.me/mibal_ua
 */
public class Application {

    private final DataPrinter dataPrinter = new ConsoleDataPrinter();

    private LetterTranslator letterTranslator;

    private static String pathToCatalog;

    private static Lang lang;

    private static LetterStandard letterStandard;

    public Application(final String[] args) {
        dataPrinter.printWelcomeMessage();
        if (args.length != 0) {
            ArgumentParser parser = new ArgumentParser();
            parser.parse(args);
            pathToCatalog = testPath(parser.getPath());
            lang = parser.getLang();
            letterStandard = parser.getLetterStandard();
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
                    dataPrinter.printErrorMessage(format("You enter incorrect path '%s'.", userPath));
                    dataPrinter.printInfoMessage(
                            "Enter path like this: " +
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
                } else if (userLang.equalsIgnoreCase(RU.name()) ||
                           userLang.equalsIgnoreCase(UA.name())) {
                    lang = Lang.valueOf(userLang.toUpperCase());
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
        if (letterStandard == null) {
            boolean infoIsExists = false;
            while (true) {
                dataPrinter.printInfoMessage("Enter standard of transliteration: 'OFFICIAL' or 'EXTENDED'");
                if (!infoIsExists)
                    dataPrinter.printInfoMessage("For more information enter '/info'");

                String userStandard = inputReader.read().trim();
                dataPrinter.printInfoMessage("");
                if (userStandard.equalsIgnoreCase("/exit")) {
                    dataPrinter.exit();
                } else if (userStandard.equalsIgnoreCase("/info")) {
                    dataPrinter.printInfoMessage("The OFFICIAL transliteration mode is used to transliterate the names of people and places.\n" +
                                                 "EXTENDED mode is best used for renaming files, as it uses all the word sounding rules for more accurate transliteration.");
                    infoIsExists = true;
                } else if (userStandard.equalsIgnoreCase(OFFICIAL.name()) ||
                           userStandard.equalsIgnoreCase(EXTENDED.name())) {
                    letterStandard = LetterStandard.valueOf(userStandard.toUpperCase());
                    break;
                } else {
                    dataPrinter.printInfoMessage(format(
                            "You enter unsupported letter standard '%s'." + '\n', userStandard
                    ));
                }
            }
        } else {
            dataPrinter.printInfoMessage("Transliteration standard: " + letterStandard.name());
        }
        letterTranslator = null;
        switch (lang){
            case UA -> {
                if (letterStandard == OFFICIAL) {
                    letterTranslator = new UaOfficialLetterTranslator();
                } else if (letterStandard == EXTENDED) {
                    letterTranslator = new UaExtendedLetterTranslator();
                }
            }
            case RU -> {
                if (letterStandard == OFFICIAL) {
                    letterTranslator = new ruOfficialLetterTranslator();
                } else if (letterStandard == EXTENDED) {
                    letterTranslator = new ruExtendedLetterTranslator();
                }
            }
        }
        if (letterTranslator == null) dataPrinter.printErrorMessage(format(
                "Letter translator component is null because language is '%s' and letter standard is '%s'.",
                lang.name(), letterStandard.name()));
    }

    public void start() {
        File[] directoryFiles = FileManager.getFilesFromDirectory(pathToCatalog, dataPrinter);
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
                    String newName = null;
                    try {
                        newName = letterTranslator.translateName(oldName);
                    } catch (IllegalNameException e) {
                        notCyrillicSymbols.add(oldName);
                        continue;
                    } catch (IllegalLanguageException e) {
                        fileHaveAnotherLanguageName.add(oldName);
                        reasonsOfFileHaveAnotherLanguageName.add(e.getMessage());
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