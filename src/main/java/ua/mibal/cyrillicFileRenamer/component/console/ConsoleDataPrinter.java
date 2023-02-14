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

package ua.mibal.cyrillicFileRenamer.component.console;

import ua.mibal.cyrillicFileRenamer.component.DataPrinter;
import ua.mibal.cyrillicFileRenamer.component.InputReader;
import ua.mibal.cyrillicFileRenamer.model.exceptions.FileNameDontContainCyrillicSymbolsException;
import ua.mibal.cyrillicFileRenamer.model.exceptions.IllegalLanguageException;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Mykhailo Balakhon
 * @link https://t.me/mibal_ua
 */
public class ConsoleDataPrinter implements DataPrinter {

    public final static String BOLD = "\u001B[1m";

    public final static String RESET = "\u001B[0m";

    private final ExitHandler exitHandler;

    private final InputReader inputReader;

    public ConsoleDataPrinter(final InputReader inputReader, final ExitHandler exitHandler) {
        this.inputReader = requireNonNull(inputReader);
        this.exitHandler = requireNonNull(exitHandler);
    }

    @Override
    public void printInfoMessage(final String message) {
        System.out.println(message);
    }

    @Override
    public void printErrorMessage(final String message) {
        System.err.println(message);
    }

    @Override
    public void printWelcomeMessage() {
        final String message = format("""
                                
                ----------------------||%s The Cyrillic file renamer v2.0 %s||----------------------
                -                               made with love ❤                               -
                - #StandWithUkraine                                                            -
                - version: 2.0                                                                 -
                - author: @mibal_ua                                                            -
                --------------------------------------------------------------------------------""",
            BOLD, RESET);
        printInfoMessage(message);
    }

    @Override
    public void exit() {
        System.out.println("Press any key to exit...");
        new Scanner(System.in).nextLine();
        exitHandler.exit();
    }

    @Override
    public void printLog(final int dirFilesLength,
                         final Map<String, Exception> logList) {
        final int countOfExceptions = logList.size();
        String mainHeaderMessage = BOLD;
        if (dirFilesLength == 0) {
            mainHeaderMessage += "Directory is empty";
        } else if (dirFilesLength == countOfExceptions) {
            mainHeaderMessage += "All files are not renamed.";
        } else if (countOfExceptions == 0) {
            mainHeaderMessage += "Files renamed successfully.";
        } else {
            mainHeaderMessage += "Files renamed" + RESET + ", but exists a problems.";
        }
        printInfoMessage("");
        printInfoMessage(mainHeaderMessage + RESET);

        printInfoMessage("To see log, enter '/log'");
        if (inputReader.read().equals("")) {
            return;
        }
        printErrorMessage("");
        final Map<Class<? extends Exception>, List<String>> sortedLogList = sortLogs(logList);
        sortedLogList.forEach((e, list) -> {
            if (list.size() == 0) {
                return;
            }
            printErrorMessage(e.getSimpleName() + ":");
            for (int i = 0; i < list.size(); i++) {
                final String message = list.get(i);
                printErrorMessage(format("%s. %s", i + 1, message));
            }
            printErrorMessage("");
        });
    }

    private Map<Class<? extends Exception>, List<String>> sortLogs(final Map<String, Exception> logList) {
        final Map<Class<? extends Exception>, List<String>> map = new HashMap<>();

        map.put(IllegalLanguageException.class, new ArrayList<>());
        map.put(IOException.class, new ArrayList<>());
        map.put(FileNameDontContainCyrillicSymbolsException.class, new ArrayList<>());

        logList.forEach((fileName, e) -> {
            final Class<? extends Exception> clazz = e.getClass();
            if (clazz == IllegalLanguageException.class) {
                map.get(clazz).add(fileName + ": " + e.getMessage());
            } else {
                map.get(clazz).add(fileName);
            }
        });
        return Collections.unmodifiableMap(map);
    }
}
