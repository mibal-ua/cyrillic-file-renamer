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

package ua.mibal.cyryllicFileRenamer.component.console;

import ua.mibal.cyryllicFileRenamer.component.DataPrinter;

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
}
