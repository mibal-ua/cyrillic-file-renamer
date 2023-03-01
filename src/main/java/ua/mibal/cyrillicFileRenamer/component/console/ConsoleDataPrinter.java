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
import ua.mibal.cyrillicFileRenamer.model.exceptions.DontContainCyrillicSymbolsException;
import ua.mibal.cyrillicFileRenamer.model.exceptions.IllegalLanguageException;
import ua.mibal.cyrillicFileRenamer.model.programMode.OS;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mykhailo Balakhon
 * @link https://t.me/mibal_ua
 */
public class ConsoleDataPrinter implements DataPrinter {

    public static String BOLD = "\033[1m";

    public static String RESET = "\033[0m";

    private static String EMOJI = "❤";

    private static String GO_TO_PREVIOUS_LINE_ESC = "\033[F";

    private static String CLEAR_CURRENT_LINE_ESC = "\033[2K";

    private static String WELCOME_MESSAGE = format("""
                            
            ----------------------||%s The Cyrillic file renamer v2.0 %s||----------------------
            -                               made with love %s                               -
            - #StandWithUkraine                                                            -
            - version: 2.0                                                                 -
            - author: @mibal_ua                                                            -
            --------------------------------------------------------------------------------""",
        BOLD, RESET, EMOJI);

    private final ExitHandler exitHandler;

    private final InputReader inputReader;

    private Map<String, Exception> logList;

    public ConsoleDataPrinter(final InputReader inputReader,
                              final ExitHandler exitHandler,
                              final OS os) {
        this.inputReader = requireNonNull(inputReader);
        this.exitHandler = requireNonNull(exitHandler);
        if (os == OS.WINDOWS) {
            BOLD = "";
            RESET = "";
            EMOJI = " ";
            GO_TO_PREVIOUS_LINE_ESC = "";
            CLEAR_CURRENT_LINE_ESC = "";
            WELCOME_MESSAGE = format("""
                                    
                    ----------------------||%s The Cyrillic file renamer v2.0 %s||----------------------
                    -                               made with love %s                               -
                    - #StandWithUkraine                                                            -
                    - version: 2.0                                                                 -
                    - author: @mibal_ua                                                            -
                    --------------------------------------------------------------------------------""",
                BOLD, RESET, EMOJI);
        }
    }

    public static void clearLines(final int count) {
        for (int i = 0; i < count; i++) {
            System.out.print(GO_TO_PREVIOUS_LINE_ESC);
            System.out.print(CLEAR_CURRENT_LINE_ESC);
        }
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
        printInfoMessage(WELCOME_MESSAGE);
    }

    @Override
    public void exit() {
        printInfoMessage("Press any key to exit... ('/log')");
        final Map<Class<? extends Exception>, List<String>> sortedLogList
            = sortLogs(logList);
        while (true) {
            final String input = inputReader.read();
            if (input.length() == 0 || input.charAt(0) != '/') {
                exitHandler.exit();
            }
            if (input.equals("/log")) {
                break;
            }
            clearLines(1);
        }
        clearLines(2);
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
        printInfoMessage("Press any key to exit...");
        inputReader.read();
        exitHandler.exit();
    }

    @Override
    public void outInfo(final int dirFilesLength,
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
        printInfoMessage("");
        this.logList = logList;
    }

    private Map<Class<? extends Exception>, List<String>> sortLogs(final Map<String, Exception> logList) {
        final Map<Class<? extends Exception>, List<String>> map = new HashMap<>();

        map.put(IllegalLanguageException.class, new ArrayList<>());
        map.put(DontContainCyrillicSymbolsException.class, new ArrayList<>());
        map.put(FileAlreadyExistsException.class, new ArrayList<>());
        map.put(IOException.class, new ArrayList<>());

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
