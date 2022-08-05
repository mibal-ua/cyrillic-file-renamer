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
package ua.mibal.cyryllicFileRenamer;

import ua.mibal.cyryllicFileRenamer.component.ArgumentParser;
import ua.mibal.cyryllicFileRenamer.component.DataPrinter;
import ua.mibal.cyryllicFileRenamer.component.InputReader;
import ua.mibal.cyryllicFileRenamer.component.console.ConsoleDataPrinter;
import ua.mibal.cyryllicFileRenamer.component.console.ConsoleInputReader;
import ua.mibal.cyryllicFileRenamer.model.OS;

import java.io.File;

import static java.lang.String.format;
import static ua.mibal.cyryllicFileRenamer.model.OS.UNIX;
import static ua.mibal.cyryllicFileRenamer.model.OS.WINDOWS;

/**
 * @author Michael Balakhon
 * @link http://t.me/mibal_ua
 */
public class Application {

    private final DataPrinter dataPrinter = new ConsoleDataPrinter();

    private final InputReader inputReader = new ConsoleInputReader();


    private final OS OS = getOS();

    private static String pathToCatalog;


    public Application(final String[] args) {
        if (!(args.length == 0)) {
            pathToCatalog = new ArgumentParser().parse(args);
        }
        if (OS == null)
            throw new IllegalArgumentException();
    }

    private OS getOS() {
        String system = System.getProperty("os.name").toLowerCase();
        if (system.contains("win")) {
            return WINDOWS;
        } else if (system.contains("nix") || system.contains("nux")
                   || system.contains("aix") || system.contains("mac")) {
            return UNIX;
        } else {
            while (true) {
                dataPrinter.printErrorMessage("""
                        Unknown OS System. If this system is Unix, enter 'unix', or 'win' if Windows.""");
                String userSystem = inputReader.read().trim();
                if (userSystem.equalsIgnoreCase("unix")) {
                    return UNIX;
                } else if (userSystem.equalsIgnoreCase("win")) {
                    return WINDOWS;
                } else {
                    dataPrinter.printInfoMessage(format("""
                            OS '%s' is not valid.""", userSystem
                    ));
                    throw new IllegalArgumentException();
                }
            }
        }

    }

    public void start() {
        if (pathToCatalog == null) {
            while (true) {
                dataPrinter.printInfoMessage("Enter path to catalog with files:");
                String userPath = inputReader.read().trim();
                if (userPathIsValid(userPath)) {
                    pathToCatalog = userPath;
                    break;
                } else {
                    dataPrinter.printInfoMessage(format(
                            "Path '%s' is not valid. You must enter path like this:" + '\n' +
                            OS.getExamplePath() + '\n', userPath)
                    );
                }
            }
        }

        File directory = new File(pathToCatalog);
        File[] files = directory.listFiles();
        if (files != null) {
            String[] incorrectNames = new String[files.length];
            int i = 0;
            for (final File file : files) {
                if (isCyrillicName(file.getName())) {
                    //change chars to latin alphabet
                } else {
                    incorrectNames[i++] = file.toString();
                }
            }
        } else {
            dataPrinter.printErrorMessage("//");
        }

    }

    private boolean isCyrillicName(final String name) {
        // if one of symbols is not russian or english or symbol
        return false;
    }

    private boolean userPathIsValid(final String userPath) {

        return false;
    }

    private char convertFromUA(char ch) {

        switch (ch) {
            case 'г':
                return 'h';
            case 'Г':
                return 'H';
            case 'ґ':
                return 'g';
            case 'Ґ':
                return 'G';
            case 'і':
                return 'i';
            case 'І':
                return 'I';
            default:
                return convertFromRU(ch);
        }
    }

    private char convertFromRU(char ch) {
        switch (ch) {
            case 'А':
                return 'A';
            case 'Б':
                return 'B';
            case 'В':
                return 'V';
            case 'Г':
                return 'G';
            case 'Д':
                return 'D';
            case 'Е':
                return 'E';
            //case 'Ё':return 'YO';
            //case 'Ж': return 'ZH';

            case 'и':
                return 'i';
        }
        return ch;
    }
}
