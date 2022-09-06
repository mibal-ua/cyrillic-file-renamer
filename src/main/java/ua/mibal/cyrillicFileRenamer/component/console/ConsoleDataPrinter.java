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

import java.io.File;
import java.util.Scanner;

/**
 * @author Michael Balakhon
 * @link http://t.me/mibal_ua
 */
public class ConsoleDataPrinter implements DataPrinter {

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
        System.exit(0);
    }

    @Override
    public void printNonProcessedFiles(final String[] nonProcessedFiles, final String[] reasonsOfNonProcessedFiles, File[] directoryFiles) {
        int count = 0;
        for (final File directoryFile : directoryFiles) {
            String name = directoryFile.getName();
            if (name.equals("renamedToLatin") || name.equals(".DS_Store")) {
                count++;
            }
        }
        if (nonProcessedFiles.length == directoryFiles.length - count) {
            printErrorMessage("All files are not renamed by the next reasons:");
        } else {
            printInfoMessage("\n\033[1mFiles renamed successfully.\u001B[0m");
        }
        if (nonProcessedFiles.length != 0) {
            printErrorMessage("The next " + nonProcessedFiles.length + " files have problems:");
            for (int i = 0; i < reasonsOfNonProcessedFiles.length; i++) {
                final String name = nonProcessedFiles[i];
                final String reason = reasonsOfNonProcessedFiles[i];
                printErrorMessage((i + 1) + ". " + name + ": " + reason + ";");
            }
            printErrorMessage("");
        }
    }
}
