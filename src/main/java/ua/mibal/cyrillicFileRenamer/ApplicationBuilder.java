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
import ua.mibal.cyrillicFileRenamer.component.ApplicationConfigurator;
import ua.mibal.cyrillicFileRenamer.component.DataPrinter;
import ua.mibal.cyrillicFileRenamer.component.DataPrinter.ExitHandler;
import ua.mibal.cyrillicFileRenamer.component.FileManager;
import ua.mibal.cyrillicFileRenamer.component.InputReader;
import ua.mibal.cyrillicFileRenamer.component.LocalFileManager;
import ua.mibal.cyrillicFileRenamer.component.OSDetector;
import ua.mibal.cyrillicFileRenamer.component.config.ConsoleApplicationConfigurator;
import ua.mibal.cyrillicFileRenamer.component.config.ConsoleArgumentParser;
import ua.mibal.cyrillicFileRenamer.component.console.ConsoleDataPrinter;
import ua.mibal.cyrillicFileRenamer.component.console.ConsoleInputReader;
import ua.mibal.cyrillicFileRenamer.component.translators.LetterTranslator;
import ua.mibal.cyrillicFileRenamer.component.translators.RuExtendedLetterTranslator;
import ua.mibal.cyrillicFileRenamer.component.translators.RuOfficialLetterTranslator;
import ua.mibal.cyrillicFileRenamer.component.translators.UaExtendedLetterTranslator;
import ua.mibal.cyrillicFileRenamer.component.translators.UaOfficialLetterTranslator;
import ua.mibal.cyrillicFileRenamer.model.programMode.Lang;
import ua.mibal.cyrillicFileRenamer.model.programMode.LetterStandard;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Lang.RU;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Lang.UA;
import static ua.mibal.cyrillicFileRenamer.model.programMode.LetterStandard.EXTENDED;
import static ua.mibal.cyrillicFileRenamer.model.programMode.LetterStandard.OFFICIAL;

/**
 * @author Mykhailo Balakhon
 * @link https://t.me/mibal_ua
 */
public class ApplicationBuilder {

    private static String currentPath;

    private static Lang lang;

    private static LetterStandard letterStandard;

    private final InputReader inputReader = new ConsoleInputReader();

    private final ExitHandler exitHandler = () -> System.exit(0);

    private final DataPrinter dataPrinter = new ConsoleDataPrinter(inputReader, exitHandler);

    private final FileManager fileManager = new LocalFileManager(OSDetector.detectOS());

    private final ApplicationConfigurator configurator =
        new ConsoleApplicationConfigurator(dataPrinter, inputReader, fileManager);

    private LetterTranslator letterTranslator;

    public ApplicationBuilder(final String[] args) {
        dataPrinter.printWelcomeMessage();
        if (args.length == 0) {
            return;
        }
        final ConsoleArgumentParser parser = new ConsoleArgumentParser(fileManager);
        parser.parse(args);
        currentPath = fileManager.testAndGetCorrectPath(parser.getPath());
        lang = parser.getLang();
        letterStandard = parser.getLetterStandard();
    }

    public Application build() {
        if (currentPath == null) {
            currentPath = configurator.configureCurrentPath();
        }
        dataPrinter.printInfoMessage("Path: " + currentPath);
        if (lang == null) {
            lang = configurator.configureLang();
        }
        dataPrinter.printInfoMessage("Language: " + lang);
        if (letterStandard == null) {
            letterStandard = configurator.configureLetterStandard();
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
            currentPath,
            letterTranslator
        );
    }
}
