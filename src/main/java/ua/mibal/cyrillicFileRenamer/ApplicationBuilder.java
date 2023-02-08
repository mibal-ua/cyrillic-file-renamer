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

import ua.mibal.cyrillicFileRenamer.component.Application;
import ua.mibal.cyrillicFileRenamer.component.ConsoleArgumentParser;
import ua.mibal.cyrillicFileRenamer.component.DataPrinter;
import ua.mibal.cyrillicFileRenamer.component.DataPrinter.ExitHandler;
import ua.mibal.cyrillicFileRenamer.component.FileManager;
import ua.mibal.cyrillicFileRenamer.component.InputReader;
import ua.mibal.cyrillicFileRenamer.component.LocalFileManager;
import ua.mibal.cyrillicFileRenamer.component.PathOperator;
import ua.mibal.cyrillicFileRenamer.component.console.ConsoleDataPrinter;
import ua.mibal.cyrillicFileRenamer.component.console.ConsoleInputReader;
import ua.mibal.cyrillicFileRenamer.component.translators.LetterTranslator;
import ua.mibal.cyrillicFileRenamer.component.translators.RuExtendedLetterTranslator;
import ua.mibal.cyrillicFileRenamer.component.translators.RuOfficialLetterTranslator;
import ua.mibal.cyrillicFileRenamer.component.translators.UaExtendedLetterTranslator;
import ua.mibal.cyrillicFileRenamer.component.translators.UaOfficialLetterTranslator;
import ua.mibal.cyrillicFileRenamer.model.programMode.Lang;
import ua.mibal.cyrillicFileRenamer.model.programMode.LetterStandard;
import static java.lang.String.format;
import static ua.mibal.cyrillicFileRenamer.component.PathOperator.testAndGetCorrectPath;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Lang.RU;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Lang.UA;
import static ua.mibal.cyrillicFileRenamer.model.programMode.LetterStandard.EXTENDED;
import static ua.mibal.cyrillicFileRenamer.model.programMode.LetterStandard.OFFICIAL;

/**
 * @author Mykhailo Balakhon
 * @link https://t.me/mibal_ua
 */
public class ApplicationBuilder {

    private static String pathToCatalog;

    private static Lang lang;

    private static LetterStandard letterStandard;

    private final ExitHandler exitHandler = () -> System.exit(0);

    private final DataPrinter dataPrinter = new ConsoleDataPrinter(exitHandler);

    private final InputReader inputReader = new ConsoleInputReader();

    private final FileManager fileManager = new LocalFileManager();

    private LetterTranslator letterTranslator;

    public ApplicationBuilder(final String[] args) {
        dataPrinter.printWelcomeMessage();
        if (args.length == 0) {
            return;
        }
        final ConsoleArgumentParser parser = new ConsoleArgumentParser();
        parser.parse(args);
        pathToCatalog = testAndGetCorrectPath(parser.getPath());
        lang = parser.getLang();
        letterStandard = parser.getLetterStandard();
    }

    public Application build() {
        if (pathToCatalog == null) {
            configurePathToCatalog();
        }
        dataPrinter.printInfoMessage("Path: " + pathToCatalog);
        if (lang == null) {
            configureLang();
        }
        dataPrinter.printInfoMessage("Language: " + lang);
        if (letterStandard == null) {
            configureLetterStandard();
        }
        dataPrinter.printInfoMessage("Transliteration standard: " + letterStandard);

        if (lang == UA) {
            if (letterStandard == OFFICIAL) {
                letterTranslator = new UaOfficialLetterTranslator();
            } else if (letterStandard == EXTENDED) {
                letterTranslator = new UaExtendedLetterTranslator();
            }
        }
        if (lang == RU) {
            if (letterStandard == OFFICIAL) {
                letterTranslator = new RuOfficialLetterTranslator();
            } else if (letterStandard == EXTENDED) {
                letterTranslator = new RuExtendedLetterTranslator();
            }
        }

        return new Application(
            dataPrinter,
            fileManager,
            pathToCatalog,
            letterTranslator
        );
    }

    private void configureLetterStandard() {
        boolean infoIsExists = false;
        dataPrinter.printInfoMessage("");
        while (true) {
            dataPrinter.printInfoMessage("Enter standard of transliteration: 'OFFICIAL' or 'EXTENDED'");
            if (!infoIsExists) {
                dataPrinter.printInfoMessage("For more information enter '/info'");
            }
            String userStandard = inputReader.read().trim();
            dataPrinter.printInfoMessage("");
            if (userStandard.equalsIgnoreCase("/exit")) {
                dataPrinter.exit();
            } else if (userStandard.equalsIgnoreCase("/info")) {
                dataPrinter.printInfoMessage("""
                    \033[1mOFFICIAL\u001B[0m transliteration mode is used to transliterate the names of people and places by goverment standards.
                    \033[1mEXTENDED\u001B[0m mode uses all word sound rules for more accurate transliteration.
                    """);
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
    }

    private void configureLang() {
        dataPrinter.printInfoMessage("");
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
    }

    private void configurePathToCatalog() {
        dataPrinter.printInfoMessage("Enter path to catalog with files:");
        while (true) {
            String userPath = inputReader.read().trim();
            dataPrinter.printInfoMessage("");
            if (userPath.equalsIgnoreCase("/exit")) {
                dataPrinter.exit();
            }
            String normalUserPath = testAndGetCorrectPath(userPath);
            if (normalUserPath != null) {
                pathToCatalog = normalUserPath;
                break;
            } else {
                dataPrinter.printErrorMessage(format("You enter incorrect path '%s'.", userPath));
                dataPrinter.printInfoMessage(
                    "Enter path like this: " +
                    PathOperator.getExamplePath());
            }
        }
    }
}
