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
import static ua.mibal.cyryllicFileRenamer.model.Border.*;
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

    final String TEXT_BOLD = "\033[1m";
    final String TEXT_RESET = "\u001B[0m";

    public Application(final String[] args) {
        dataPrinter.printWelcomeMessage();
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
            } else {
                dataPrinter.printInfoMessage("Path: " + pathToCatalog);
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
            } else {
                dataPrinter.printInfoMessage("Language: " + lang.name());
            }

            File directory = new File(pathToCatalog);
            File[] directoryFiles = directory.listFiles();
            File newDirectory = new File(pathToCatalog + "/renamedToLatin");
            newDirectory.mkdir();
            DynaStringArray nonProcessedFiles = new DynaStringArray();
            DynaStringArray reasonsOfNonProcessedFiles = new DynaStringArray();

            if (directoryFiles != null) {
                for (final File file : directoryFiles) {
                    String oldName = file.getName(); //this is name with extension
                    if (!oldName.equals(newDirectory.getName()) && !oldName.equals(".DS_Store")) {
                        if (oldName.charAt(0) != '.') {
                            String newName;
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
                dataPrinter.printInfoMessage('\n' + TEXT_BOLD + "Files renamed successfully." + TEXT_RESET);
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

    private String translateName(String oldName) throws IllegalNameException {
        String[] result = getSeparateExtensionAndName(oldName);
        String name = result[0];
        String extension = result[1];
        String[] words = getWordsFromName(name);
        Lambda translatorForUsualCases;
        if (lang == RU) {
            translatorForUsualCases = this::convertFromRU;
        } else if (lang == UA) {
            translatorForUsualCases = this::convertFromUA;
        } else {
            throw new IllegalArgumentException("Incorrect Language.");
        }
        String[] newNameArray = new String[words.length];
        int count = 0;
        for (final String word : words) {
            StringBuilder newWord = new StringBuilder();
            String newLetter;
            for (int i = 0; i < word.length(); i++) {
                String letter = String.valueOf(word.charAt(i));
                if (!charIsCyrillic(letter)) { // checking if letter is latin
                    newLetter = letter;
                } else {
                    if (letter.equalsIgnoreCase("Є") ||
                        letter.equalsIgnoreCase("Ї") ||
                        letter.equalsIgnoreCase("Е") ||
                        letter.equalsIgnoreCase("Й") ||
                        letter.equalsIgnoreCase("Ю") ||
                        letter.equalsIgnoreCase("Я")) {
                        if (i == 0) {
                            newLetter = translateSpecialSymbols(letter);
                        } else if (isGolosnyy(word.charAt(i - 1)) || isZnakMyakshenniaOrElse(word.charAt(i - 1))) {
                            newLetter = translateSpecialSymbols(letter);
                        } else {
                            newLetter = translatorForUsualCases.translate(letter);
                        }
                    } else if (i != 0 && lang == RU && letter.equalsIgnoreCase("И") &&
                               isShyplyachyy(word.charAt(i - 1))) {
                        newLetter = Character.isUpperCase(letter.charAt(0)) ? "Y" : "y";
                    } else if (i != 0 && lang == UA && letter.equalsIgnoreCase("Г") &&
                               String.valueOf(word.charAt(i - 1)).equalsIgnoreCase("З")) {
                        newLetter = Character.isUpperCase(letter.charAt(0)) ? "Gh" : "gh";
                    } else {
                        newLetter = translatorForUsualCases.translate(letter);
                    }
                }
                newWord.append(newLetter);
            }
            newNameArray[count++] = newWord.toString();
        }
        StringBuilder newName = new StringBuilder();
        for (final String word : newNameArray) {
            newName.append(word);
        }
        if (newName.toString().equals(name)) {
            throw new IllegalNameException(format("File '%s' already renamed.", name));
        }
        return newName.append(extension).toString();
    }

    private boolean isZnakMyakshenniaOrElse(final char charAt) {
        return (charAt == '\'' || Character.toLowerCase(charAt) == 'ь' || Character.toLowerCase(charAt) == 'ъ');
    }

    private boolean isShyplyachyy(final char charAt) {
        char ch = Character.toUpperCase(charAt);
        return (ch == 'Ж' || ch == 'Ш' || ch == 'Щ');
    }

    private String[] getSeparateExtensionAndName(final String oldName) {
        String name;
        String extension;
        int index = -1;
        for (int i = 0; i < oldName.length(); i++) {
            if (oldName.charAt(i) == '.') {
                index = i;
            }
        }
        if (index == -1) {
            name = oldName;
            extension = "";
        } else {
            name = oldName.substring(0, index);
            extension = oldName.substring(index);
        }
        return new String[]{name, extension};
    }


    private String[] getWordsFromName(final String oldName) {
        if (oldName.length() <= 1) {
            return oldName.length() == 1 ?
                    new String[]{String.valueOf(oldName.charAt(0))} :
                    new String[]{""};
        }

        String[] borders = {HYPHENMINUS.getBorder(), ENDASH.getBorder(), EMDASH.getBorder(),
                MINUS.getBorder(), SPACE.getBorder(), UNDERSCORE.getBorder(), DOT.getBorder()};

        DynaStringArray dynaResult = new DynaStringArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < oldName.length(); i++) {
            boolean isThisABorder = false;
            char cha = oldName.charAt(i);
            String ch = String.valueOf(cha);
            for (final String border : borders) {
                if (ch.equals(border)) {
                    dynaResult.add(stringBuilder.append(ch).toString());
                    stringBuilder = new StringBuilder();
                    isThisABorder = true;
                }
            }
            if (!isThisABorder) {
                if ((i != (oldName.length() - 1)) && Character.isUpperCase(oldName.charAt(i + 1))) {
                    dynaResult.add(stringBuilder.append(ch).toString());
                    stringBuilder = new StringBuilder();
                } else {
                    stringBuilder.append(ch);
                }
            }

        }
        dynaResult.add(stringBuilder.toString());
        return dynaResult.toArray();
    }

    private boolean isGolosnyy(final char ch) {
        String character = String.valueOf(ch);
        String[] golosniChars = {"Є", "Е", "Є", "И", "І", "Ї", "О", "У", "Ю", "Я", "Ы", "Э"};
        for (final String golosniChar : golosniChars) {
            if (character.equalsIgnoreCase(golosniChar)) {
                return true;
            }
        }
        return false;
    }

    //Ukrainian an ru
    private String translateSpecialSymbols(String ch) {
        boolean isUpper = Character.isUpperCase(ch.charAt(0));
        ch = ch.toUpperCase();
        String result = switch (ch) {
            case "Е", "Э" -> "Ye";
            case "Ї" -> "Yi";
            case "Й" -> "Y";
            case "Ю" -> "Yu";
            case "Я" -> "Ya";
            default -> null;
        };
        return isUpper ? result : result.toLowerCase();

    }

    private boolean charIsCyrillic(final String character) {
        char ch = character.charAt(0);
        return (('А' <= ch && ch <= 'я') ||
                (ch == 'ґ') || (ch == 'Ґ') || (ch == 'і') || (ch == 'І') || (ch == 'ї') ||
                (ch == 'Ї') || (ch == 'ё') || (ch == 'Ё') || (ch == 'є') || (ch == 'Є'));
    }

    @FunctionalInterface
    private interface Lambda {

        String translate(String ch);
    }

    private String convertFromUA(String ch) {
        boolean isUpper = Character.isUpperCase(ch.charAt(0));
        String character = ch.toUpperCase();
        String result = switch (character) {
            case "А" -> "A";
            case "Б" -> "B";
            case "В" -> "V";
            case "Г" -> "H";
            case "Ґ" -> "G";
            case "Д" -> "D";
            case "Е" -> "E";
            case "Є" -> "Ie";
            case "Ж" -> "Zh";
            case "З" -> "Z";
            case "И" -> "Y";
            case "І", "Ї", "Й" -> "I";
            case "К" -> "K";
            case "Л" -> "L";
            case "М" -> "M";
            case "Н" -> "N";
            case "О" -> "O";
            case "П" -> "P";
            case "Р" -> "R";
            case "С" -> "S";
            case "Т" -> "T";
            case "У" -> "U";
            case "Ф" -> "F";
            case "Х" -> "Kh";
            case "Ц" -> "Ts";
            case "Ч" -> "Ch";
            case "Ш" -> "Sh";
            case "Щ" -> "Shch";
            case "Ь" -> "";
            case "Ю" -> "Iu";
            case "Я" -> "Ia";
            default -> null;
        };
        return isUpper ? result : result.toLowerCase();
    }

    private String convertFromRU(final String ch) {
        boolean isUpper = Character.isUpperCase(ch.charAt(0));
        String character = ch.toUpperCase();
        String result = switch (character) {
            case "Г" -> "G";
            case "Е", "Э" -> "E";
            case "Ё" -> "Yo";
            case "И" -> "I";
            case "Й" -> "Y";
            default -> convertFromUA(ch);
        };
        return isUpper ? result : result.toLowerCase();
    }

    private void exit() {
        System.out.println("Press any key to exit...");
        new Scanner(System.in).nextLine();
        System.exit(0);
    }
}