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
import ua.mibal.cyryllicFileRenamer.model.Border;
import ua.mibal.cyryllicFileRenamer.model.DynaStringArray;
import ua.mibal.cyryllicFileRenamer.model.Lang;
import ua.mibal.cyryllicFileRenamer.model.OS;
import ua.mibal.cyryllicFileRenamer.model.exception.IllegalNameException;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static java.lang.String.format;
import static ua.mibal.cyryllicFileRenamer.model.Lang.RU;
import static ua.mibal.cyryllicFileRenamer.model.Lang.UA;
import static ua.mibal.cyryllicFileRenamer.model.OS.UNIX;
import static ua.mibal.cyryllicFileRenamer.model.OS.WINDOWS;

/**
 * @author Michael Balakhon
 * @link http://t.me/mibal_ua
 */
public class Application {

    private final DataPrinter dataPrinter = new ConsoleDataPrinter();

    private final InputReader inputReader = new ConsoleInputReader();


    private static OS OS;

    private static String pathToCatalog;

    private static Lang lang;

    private static Border border;


    public Application(final String[] args) {
        final String TEXT_BOLD = "\033[1m";
        final String TEXT_RESET = "\u001B[0m";
        dataPrinter.printInfoMessage("");
        dataPrinter.printInfoMessage("-------------||" + TEXT_BOLD + " The Cyrillic file renamer application " + TEXT_RESET + "||------------- ");
        dataPrinter.printInfoMessage("""
                -                         made with love ❤                          -
                - #StandWithUkraine🇺🇦                                               -
                - author: @mibal_ua                                                 -
                ---------------------------------------------------------------------
                """);
        if (args.length != 0) {
            ArgumentParser parser = new ArgumentParser();
            parser.parse(args);
            OS = parser.getOS();
            if (OS == null) {
                OS = getOS();
                if (OS == null) {
                    throw new IllegalArgumentException("Unknown OS.");
                }
            }
            pathToCatalog = correctAndTestPath(parser.getPath());
            lang = parser.getLang();
        }
        OS = getOS();
        if (OS == null) {
            throw new IllegalArgumentException("Unknown OS.");
        }
    }

    private OS getOS() {
        String system = System.getProperty("os.name").toLowerCase();
        if (system.contains("win")) {
            return WINDOWS;
        } else if (system.contains("nix") || system.contains("nux")
                   || system.contains("aix") || system.contains("mac")) {
            return UNIX;
        } else {
            dataPrinter.printErrorMessage("Unknown OS System.");
            exit();
            return null;
        }
    }

    public void start() {
        boolean success = false;
        do {
            if (pathToCatalog == null) {
                dataPrinter.printInfoMessage("Enter path to catalog with files:");
                while (true) {
                    String userPath = inputReader.read().trim();
                    dataPrinter.printInfoMessage("");
                    if (userPath.equalsIgnoreCase("/exit"))
                        exit();
                    String normalUserPath = correctAndTestPath(userPath);
                    if (normalUserPath != null) {
                        pathToCatalog = normalUserPath;
                        break;
                    } else {
                        dataPrinter.printInfoMessage(
                                "You must enter path like this: " +
                                OS.getExamplePath() + '\n'
                        );
                    }
                }
            }
            if (lang == null) {
                while (true) {
                    dataPrinter.printInfoMessage("Enter language of files: 'RU' or 'UA'");
                    String userLang = inputReader.read().trim();
                    dataPrinter.printInfoMessage("");
                    if (userLang.equalsIgnoreCase("/exit")) {
                        exit();
                    } else if (userLang.equalsIgnoreCase(RU.name()) || userLang.equalsIgnoreCase(UA.name())) {
                        lang = Lang.valueOf(userLang.toUpperCase());
                        break;
                    } else {
                        dataPrinter.printInfoMessage(format(
                                "You enter unsupported language '%s'." + '\n', userLang
                        ));
                    }
                }
            }

            File directory = new File(pathToCatalog);
            File[] directoryFiles = directory.listFiles();
            File newDirectory = new File(pathToCatalog + "/renamedToLatin");
            if (newDirectory.mkdir()) {
                //TODO out successful
            } else {
                //TODO out unsuccessful
            }
            DynaStringArray nonProcessedFiles = new DynaStringArray();
            DynaStringArray reasonsOfNonProcessedFiles = new DynaStringArray();

            if (directoryFiles != null) {
                for (final File file : directoryFiles) {
                    String oldName = file.getName(); //this is name with extension
                    if (!oldName.equals(newDirectory.getName())) {
                        if (oldName.charAt(0) != '.') {
                            String newName = null;
                            try {
                                newName = translateName(oldName);
                            } catch (IllegalNameException e) {
                                nonProcessedFiles.add(oldName);
                                reasonsOfNonProcessedFiles.add(e.getMessage());
                                continue;
                            }
                            try {
                                Files.copy(file.toPath(), Path.of((newDirectory.toPath() + "/" + newName)));
                            } catch (IOException e) {
                                nonProcessedFiles.add(oldName);
                                String exceptionReason = e.getClass().getSimpleName();
                                if (e.getClass() == FileAlreadyExistsException.class) {
                                    reasonsOfNonProcessedFiles.add("File already renamed");
                                } else {
                                    reasonsOfNonProcessedFiles.add(exceptionReason);
                                }
                            }
                        } else {
                            nonProcessedFiles.add(oldName);
                            reasonsOfNonProcessedFiles.add("File have hidden name");
                        }

                    }
                }
            } else {
                dataPrinter.printErrorMessage(format(
                        "Problems with files in directory '%s'." + '\n', pathToCatalog));
                pathToCatalog = null;
                continue;
            }
            if (nonProcessedFiles.length() == directoryFiles.length - 1) {
                dataPrinter.printErrorMessage("All files are not renamed by the next reasons:" + '\n');
            } else {
                dataPrinter.printInfoMessage("Files renamed successfully." + '\n');
                success = true;
            }
            if (nonProcessedFiles.length() != 0) {
                dataPrinter.printErrorMessage("The next " + nonProcessedFiles.length() + " files have problems:");
                String[] nonProcessedFilesArray = nonProcessedFiles.toArray();
                String[] reasonsOfNonProcessedFilesArray = reasonsOfNonProcessedFiles.toArray();
                for (int i = 0; i < reasonsOfNonProcessedFiles.length(); i++) {
                    final String name = nonProcessedFilesArray[i];
                    final String reason = reasonsOfNonProcessedFilesArray[i];
                    dataPrinter.printErrorMessage((i + 1) + ". " + name + ": " + reason + ";");
                }
                dataPrinter.printErrorMessage("");
            }
            if (!success) {
                dataPrinter.printInfoMessage("You can exit with '/exit' command.");
            }
            pathToCatalog = null;
        } while (!success);
        exit();
    }

    private String correctAndTestPath(final String userPath) {
        if (userPath != null) {
            StringBuilder userPathBuilder = new StringBuilder(userPath);
            char border = OS.getBorder();
            if (userPathBuilder.charAt(0) != border) {
                userPathBuilder.insert(0, border);
            }
            if (new File(userPathBuilder.toString()).exists()) {
                dataPrinter.printInfoMessage("Directory path successfully set." + '\n');
                return userPathBuilder.toString();
            } else {
                dataPrinter.printErrorMessage(format(
                        "Directory '%s' is not exists.", userPathBuilder));
                return null;
            }
        } else {
            return null;
        }
    }

    private String translateName(final String oldName) throws IllegalNameException {
        String[] words = getWordsFromName(oldName);

        Lambda universalTranslator = null;
        if (lang == RU) {
            universalTranslator = this::convertFromRU;
        } else if (lang == UA) {
            universalTranslator = this::convertFromUA;
        } else {
            throw new IllegalArgumentException("Incorrect Language.");
        }

        String[] newNameArray = new String[words.length];
        int count = 0;
        for (final String word : words) {
            StringBuilder newWord = new StringBuilder();
            // StringBuilder illegalSymbols = new StringBuilder();
            String newLetter = "";
            for (int i = 0; i < word.length(); i++) {
                String letter = String.valueOf(word.charAt(i));
                if (!charIsCyrillic(letter)) { // checking if letter is latin
                    newLetter = letter;
                } else if (lang == RU) {
                    if (letter.equalsIgnoreCase("Е") ||
                        letter.equalsIgnoreCase("Ё") ||
                        letter.equalsIgnoreCase("Й") ||
                        letter.equalsIgnoreCase("Ю") ||
                        letter.equalsIgnoreCase("Я")) {
                        if (i == 0) {
                            newLetter = translateSpecialRussianSymbolsOnTheStartAndAfterGolosnykh(letter);
                        } else if (isGolosnyy(oldName.charAt(i - 1))) {
                            newLetter = translateSpecialRussianSymbolsOnTheStartAndAfterGolosnykh(letter);
                        } else {
                            newLetter = universalTranslator.translate(letter);
                        }
                    } else {
                        //usual algorithm
                        newLetter = universalTranslator.translate(letter);
                    }
                } else if (lang == UA) {
                    if (letter.equalsIgnoreCase("Є") ||
                        letter.equalsIgnoreCase("Ї") ||
                        letter.equalsIgnoreCase("Й") ||
                        letter.equalsIgnoreCase("Ю") ||
                        letter.equalsIgnoreCase("Я")) {
                        if (i == 0) {
                            newLetter = translateSpecialUkrainianSymbolsOnTheStartAndAfterGolosnykh(letter);
                        } else if (isGolosnyy(oldName.charAt(i - 1))) {
                            newLetter = translateSpecialUkrainianSymbolsOnTheStartAndAfterGolosnykh(letter);
                        } else {
                            newLetter = universalTranslator.translate(letter);
                        }
                    } else {
                        //usual algorithm
                        newLetter = universalTranslator.translate(letter);
                    }
                }
                newWord.append(newLetter);
            }
            newNameArray[count++] = newWord.toString();
        }
        StringBuilder newName = new StringBuilder();
        String borderString = border.getBorder();
        for (int i = 0; i < newNameArray.length; i++) {
            final String name = newNameArray[i];
            newName.append(name);
            if (i != newNameArray.length - 1) {
                newName.append(borderString);
            }
        }
        if(newName.toString().equals(oldName)){
            throw new IllegalNameException(format("Name '%s' already renamed.", oldName));
        }
        return newName.toString();
    }


    private String[] getWordsFromName(final String oldName) {
        // TODO logic of bordering name to names array
        // set border value and return array
        return new String[0];
    }

    private boolean isGolosnyy(final char ch) {
        String character = String.valueOf(ch);
        String[] golosniChars = {"Є", "Е", "Є", "И", "І", "Ї", "О", "У", "Ю", "Я", "Ы", "Э"};
        for (final String golosniChar : golosniChars) {
            if(character.equalsIgnoreCase(golosniChar)){
                return true;
            }
        }
        return false;
    }

    private String translateSpecialUkrainianSymbolsOnTheStartAndAfterGolosnykh(final String ch) {
        //TODO
        return "";
    }

    private String translateSpecialRussianSymbolsOnTheStartAndAfterGolosnykh(final String ch) {
        //TODO
        return "";
    }

    private boolean charIsCyrillic(final String character) {
        char ch = character.charAt(0);
        return (('А' <= ch & ch <= 'п') || ('р' <= ch && ch <= 'ї'));
    }

    @FunctionalInterface
    private interface Lambda {

        String translate(String ch);
    }


    //TODO usual chars
    private String convertFromUA(String ch) {
/*
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
        }


    */return null;
    }

    private String convertFromRU(String ch) {
        /*
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

         */
        return null;
    }

    private void exit() {
        System.out.println("Press any key to exit...");
        new Scanner(System.in).nextLine();
        System.exit(0);
    }
}