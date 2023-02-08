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
import ua.mibal.cyrillicFileRenamer.component.FileManager;
import ua.mibal.cyrillicFileRenamer.component.LocalFileManager;
import static java.util.Objects.requireNonNull;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Mykhailo Balakhon
 * @link https://t.me/mibal_ua
 */
public class ConsoleDataPrinter implements DataPrinter {

    private final ExitHandler exitHandler;

    public ConsoleDataPrinter(final ExitHandler exitHandler) {
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
        System.out.println("""
                            
            -------------||\033[1m The Cyrillic file renamer application \u001B[0m||-------------
            -                         made with love ❤                          -
            -                                                                   -
            - #StandWithUkraine                                                 -
            - author: @mibal_ua                                                 -
            ---------------------------------------------------------------------""");
    }

    @Override
    public void exit() {
        System.out.println("Press any key to exit...");
        new Scanner(System.in).nextLine();
        exitHandler.exit();
    }

    @Override
    public void printNonProcessedFiles(final File[] directoryFiles,
                                       final Map<String, List<String>> list) {
        FileManager fileManager = new LocalFileManager(this); //TODO call fileManager in another way
        int countOfIgnoredFiles = 0;
        for (final File directoryFile : directoryFiles) {
            if (fileManager.isIgnoredFile(directoryFile.getName())) {
                countOfIgnoredFiles++;
            }
        }
        int countOfAllFilesInDirectory = (directoryFiles.length - countOfIgnoredFiles);
        int countOfExceptionNames = (list.get("notCyrillicSymbols").size() +
                                     list.get("fileAlreadyRenamed").size() +
                                     list.get("fileHaveHiddenName").size() +
                                     list.get("fileHaveAnotherLanguageName").size() +
                                     list.get("nonProcessedFiles").size());

        if (countOfExceptionNames == countOfAllFilesInDirectory) {
            printErrorMessage("\n\033[1mAll files are not renamed by the next reasons:\u001B[0m");
        } else if (countOfExceptionNames == 0) {
            printInfoMessage("\n\033[1mFiles renamed successfully.\u001B[0m");
        } else {
            printInfoMessage("\n\033[1mFiles renamed\u001B[0m, but exists a problems.");
        }
        outListsWithProblems(list.get("fileAlreadyRenamed"), "already renamed");
        outListsWithProblems(list.get("fileHaveHiddenName"), "have hidden name");
        outListsWithProblems(list.get("notCyrillicSymbols"), "don't have cyrillic symbols");
        outListsWithProblems(list.get("fileHaveAnotherLanguageName"),
            list.get("reasonsOfFileHaveAnotherLanguageName"), "have language problem");
        outListsWithProblems(list.get("nonProcessedFiles"),
            list.get("reasonsOfNonProcessedFiles"), "have other problem");
    }

    private void outListsWithProblems(final List<String> list, final List<String> reasonsList, final String message) {
        if (list.size() == 0) {
            return;
        }
        printErrorMessage("The next " + (list.size() == 1 ? "file" : list.size() + " files") + " " + message + ":");
        for (int i = 0; i < list.size(); i++) {
            printErrorMessage((i + 1) + ". " + list.get(i) + ": " + reasonsList.get(i) + ";");
        }
        printErrorMessage("");
    }

    private void outListsWithProblems(final List<String> list, final String message) {
        if (list.size() != 0) {
            String files = list.size() == 1 ? "file" : list.size() + " files";
            printErrorMessage("The next " + files + " " + message + ":");
            for (int i = 0; i < list.size(); i++) {
                printErrorMessage((i + 1) + ". " + list.get(i) + ";");
            }
            printErrorMessage("");
        }
    }
}
