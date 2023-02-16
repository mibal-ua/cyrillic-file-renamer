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

package ua.mibal.cyrillicFileRenamer.component.config;

import ua.mibal.cyrillicFileRenamer.component.ArgumentConfigurator;
import ua.mibal.cyrillicFileRenamer.component.DataPrinter;
import ua.mibal.cyrillicFileRenamer.component.FileManager;
import ua.mibal.cyrillicFileRenamer.component.InputReader;
import ua.mibal.cyrillicFileRenamer.model.programMode.Lang;
import ua.mibal.cyrillicFileRenamer.model.programMode.LetterStandard;
import static java.lang.String.format;
import static ua.mibal.cyrillicFileRenamer.component.console.ConsoleDataPrinter.BOLD;
import static ua.mibal.cyrillicFileRenamer.component.console.ConsoleDataPrinter.RESET;
import static ua.mibal.cyrillicFileRenamer.component.console.ConsoleDataPrinter.clearLines;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Lang.RU;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Lang.UA;
import static ua.mibal.cyrillicFileRenamer.model.programMode.LetterStandard.EXTENDED;

/**
 * @author Mykhailo Balakhon
 * @link t.me/mibal_ua
 */
public class ConsoleArgumentConfigurator implements ArgumentConfigurator {


    private final DataPrinter dataPrinter;

    private final InputReader inputReader;

    private final FileManager fileManager;

    public ConsoleArgumentConfigurator(final DataPrinter dataPrinter,
                                       final InputReader inputReader,
                                       final FileManager fileManager) {
        this.dataPrinter = dataPrinter;
        this.inputReader = inputReader;
        this.fileManager = fileManager;
    }

    @Override
    public LetterStandard configureLetterStandard() {
        dataPrinter.printInfoMessage("");
        dataPrinter.printInfoMessage("Select standard of transliteration:");
        boolean infoIsExists = false;
        int count = 2;
        LetterStandard resultLetterStandard;
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
                resultLetterStandard = EXTENDED;
                break;
            }
            if (userStandard.equals("2")) {
                resultLetterStandard = EXTENDED;
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
        return resultLetterStandard;
    }

    @Override
    public Lang configureLang() {
        dataPrinter.printInfoMessage("");
        dataPrinter.printInfoMessage("Select language:");
        int count = 2;
        Lang resultLang;
        while (true) {
            dataPrinter.printInfoMessage("""
                1 - UA
                2 - RU
                """);
            final String userLang = inputReader.read().trim();
            count += 4;
            if (userLang.equals("1")) {
                resultLang = UA;
                break;
            }
            if (userLang.equals("2")) {
                resultLang = RU;
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
        return resultLang;
    }

    @Override
    public String configureCurrentPath() {
        dataPrinter.printInfoMessage("");
        dataPrinter.printInfoMessage("Enter path to catalog with files:");
        int count = 2;
        String resultPath;
        while (true) {
            final String userPath = inputReader.read().trim();
            count += 1;
            final String normalUserPath = fileManager.testAndGetCorrectPath(userPath);
            if (normalUserPath != null) {
                resultPath = normalUserPath;
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
        return resultPath;
    }
}
