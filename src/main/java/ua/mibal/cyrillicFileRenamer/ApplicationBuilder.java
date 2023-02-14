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
import ua.mibal.cyrillicFileRenamer.component.console.ConsoleDataPrinter;
import ua.mibal.cyrillicFileRenamer.component.console.ConsoleInputReader;
import ua.mibal.cyrillicFileRenamer.component.translators.LetterTranslator;
import ua.mibal.cyrillicFileRenamer.component.translators.RuExtendedLetterTranslator;
import ua.mibal.cyrillicFileRenamer.component.translators.RuOfficialLetterTranslator;
import ua.mibal.cyrillicFileRenamer.component.translators.UaExtendedLetterTranslator;
import ua.mibal.cyrillicFileRenamer.component.translators.UaOfficialLetterTranslator;
import ua.mibal.cyrillicFileRenamer.model.programMode.Lang;
import ua.mibal.cyrillicFileRenamer.model.programMode.LetterStandard;
import ua.mibal.cyrillicFileRenamer.model.programMode.OS;
import static java.lang.String.format;
import static ua.mibal.cyrillicFileRenamer.component.console.ConsoleDataPrinter.BOLD;
import static ua.mibal.cyrillicFileRenamer.component.console.ConsoleDataPrinter.RESET;
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

    private final InputReader inputReader = new ConsoleInputReader();

    private final DataPrinter dataPrinter = new ConsoleDataPrinter(inputReader, exitHandler);

    private final FileManager fileManager = new LocalFileManager(OS.UNIX);

    private LetterTranslator letterTranslator;

    public ApplicationBuilder(final String[] args) {
        dataPrinter.printWelcomeMessage();
        if (args.length == 0) {
            return;
        }
        final ConsoleArgumentParser parser = new ConsoleArgumentParser(fileManager);
        parser.parse(args);
        pathToCatalog = fileManager.testAndGetCorrectPath(parser.getPath());
        lang = parser.getLang();
        letterStandard = parser.getLetterStandard();
    }

    private void clearLines(final int count) {
        for (int i = 0; i < count; i++) {
            System.out.print("\033[F"); // go to previous line
            System.out.print("\033[2K"); // clear current line
        }
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
            }
            if (letterStandard == EXTENDED) {
                letterTranslator = new UaExtendedLetterTranslator();
            }
        }
        if (lang == RU) {
            if (letterStandard == OFFICIAL) {
                letterTranslator = new RuOfficialLetterTranslator();
            }
            if (letterStandard == EXTENDED) {
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
        dataPrinter.printInfoMessage("");
        dataPrinter.printInfoMessage("Select standard of transliteration:");
        boolean infoIsExists = false;
        int count = 2;
        while (true) {
            dataPrinter.printInfoMessage("""
                1 - EXTENDED
                2 - OFFICIAL""");
            count += 2;
            if (!infoIsExists) {
                dataPrinter.printInfoMessage("Questions? Enter '/info'");
                count++;
            }
            dataPrinter.printInfoMessage("");
            final String userStandard = inputReader.read().trim();
            count += 2;
            if (userStandard.equals("1")) {
                letterStandard = EXTENDED;
                break;
            }
            if (userStandard.equals("2")) {
                letterStandard = EXTENDED;
                break;
            }
            if (userStandard.equalsIgnoreCase("/info")) {
                clearLines(count - 1);
                count = 1;
                dataPrinter.printInfoMessage(format("""
                        %sEXTENDED%s transliteration mode uses all word
                                 sound rules for more accurate
                                 transliteration.
                        %sOFFICIAL%s mode is used to
                                 transliterate the names of people
                                 and places by government standards.""",
                    BOLD, RESET, BOLD, RESET));
                infoIsExists = true;
                count += 6;
                continue;
            }
            clearLines(count - 1);
            count = 1;
            dataPrinter.printInfoMessage(format(
                "You enter unsupported letter standard '%s'.", userStandard
            ));
            count += 1;
        }
        clearLines(count);
    }

    private void configureLang() {
        dataPrinter.printInfoMessage("");
        dataPrinter.printInfoMessage("Select language:");
        int count = 2;
        while (true) {
            dataPrinter.printInfoMessage("""
                1 - UA
                2 - RU
                """);
            final String userLang = inputReader.read().trim();
            count += 4;
            if (userLang.equals("1")) {
                lang = UA;
                break;
            }
            if (userLang.equals("2")) {
                lang = RU;
                break;
            }
            clearLines(count - 1);
            count = 1;
            dataPrinter.printInfoMessage(format(
                "You enter unsupported language '%s'.", userLang
            ));
            count += 1;
        }
        clearLines(count);
    }

    private void configurePathToCatalog() {
        dataPrinter.printInfoMessage("");
        dataPrinter.printInfoMessage("Enter path to catalog with files:");
        int count = 2;
        while (true) {
            final String userPath = inputReader.read().trim();
            count += 1;
            final String normalUserPath = fileManager.testAndGetCorrectPath(userPath);
            if (normalUserPath != null) {
                pathToCatalog = normalUserPath;
                break;
            }
            clearLines(count - 1);
            count = 1;
            dataPrinter.printInfoMessage(format("You enter incorrect path '%s'.", userPath));
            dataPrinter.printInfoMessage("Enter path like this: " + fileManager.getPathExample());
            dataPrinter.printInfoMessage("");
            count += 3;
        }
        clearLines(count);
    }
}
