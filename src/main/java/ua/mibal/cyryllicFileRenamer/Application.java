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
import ua.mibal.cyryllicFileRenamer.model.OS;

import java.io.File;

import static java.lang.String.format;
import static ua.mibal.cyryllicFileRenamer.model.OS.LINUX;
import static ua.mibal.cyryllicFileRenamer.model.OS.WINDOWS;

/**
 * @author Michael Balakhon
 * @link http://t.me/mibal_ua
 */
public class Application {

    private DataPrinter dataPrinter;

    private InputReader inputReader;



    private static String pathToCatalog;

    private static String systemPath;

    private static char borders;

    private static String examplePath;



    private String[] russianAlphabet = {};

    private String[] ukrainianAlphabet;

    private String[] latinAlphabet;

    public Application(final String[] args) {
        if (!(args.length == 0)) {
            pathToCatalog = new ArgumentParser().parse(args);
        }
        constructOSDifferences();

    }

    private void constructOSDifferences() {
        OS os = getOS();
        if (os == LINUX) {
            //
        } else if (os == WINDOWS) {
            //
        } else {
            //error
        }
        //construct borders, examplePath and home
    }

    private OS getOS() {
        //return OS enum
        return null;
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
                    dataPrinter.printInfoMessage(format("""
                            Path '%s' is not valid. You must enter path like this:
                            %s""", examplePath
                    ));
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

    private char convertFromUA(char ch){

        switch (ch){
            case 'і' : return 'i';
        }
        return convertFromRU(ch);
    }

    private char convertFromRU(char ch){
        switch (ch){
            case 'и' : return 'i';
        }
        return ch;
    }
}
