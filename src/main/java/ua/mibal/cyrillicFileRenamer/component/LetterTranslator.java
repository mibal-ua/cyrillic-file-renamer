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

package ua.mibal.cyrillicFileRenamer.component;

import ua.mibal.cyrillicFileRenamer.model.DynaStringArray;
import ua.mibal.cyrillicFileRenamer.model.Lang;
import ua.mibal.cyrillicFileRenamer.model.exception.IllegalNameException;

import static java.lang.String.format;
import static ua.mibal.cyrillicFileRenamer.model.Border.*;
import static ua.mibal.cyrillicFileRenamer.model.Lang.RU;
import static ua.mibal.cyrillicFileRenamer.model.Lang.UA;

/**
 * @author Michael Balakhon
 * @link http://t.me/mibal_ua
 */
public class LetterTranslator {

    private final Lang lang;

    public LetterTranslator(final Lang lang) {
        this.lang = lang;
    }

    public String translateName(final String oldName) throws IllegalNameException {
        String[] result = getSeparateExtensionAndName(oldName);
        String name = result[0];
        String extension = result[1];
        String[] words = getWordsFromName(name);
        Lambda translatorForUsualCases = switch (lang) {
            case RU -> this::convertFromRU;
            case UA -> this::convertFromUA;
        };
        DynaStringArray newNameArray = new DynaStringArray(words.length);
        for (final String word : words) {
            StringBuilder newWord = new StringBuilder();
            String newLetter;
            for (int i = 0; i < word.length(); i++) {
                String letter = String.valueOf(word.charAt(i));
                if (!charIsCyrillic(letter)) { // checking if letter is latin
                    newLetter = letter;
                } else {
                    if (isSpecialLetter(letter)) {
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
            newNameArray.add(newWord.toString());
        }
        StringBuilder newName = new StringBuilder();
        for (final String word : newNameArray.toArray()) {
            newName.append(word);
        }
        if (newName.toString().equals(name)) {
            throw new IllegalNameException("File don't have cyrillic symbols");
        }
        return newName.append(extension).toString();
    }

    private boolean isSpecialLetter(final String letter) {
        return (letter.equalsIgnoreCase("Є") ||
                letter.equalsIgnoreCase("Ї") ||
                letter.equalsIgnoreCase("Е") ||
                letter.equalsIgnoreCase("Й") ||
                letter.equalsIgnoreCase("Ю") ||
                letter.equalsIgnoreCase("Я"));
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

    @FunctionalInterface
    private interface Lambda {

        String translate(String ch) throws IllegalNameException;
    }

    private String convertFromRU(final String ch) throws IllegalNameException {
        String result = switch (ch.toUpperCase()) {
            case "Г" -> "G";
            case "Э" -> "E";
            case "Ё" -> "Yo";
            case "И" -> "I";
            case "Й" -> "Y";
            default -> convertUniversal(ch);
        };
        return Character.isUpperCase(ch.charAt(0)) ? result : result.toLowerCase();
    }

    private String convertFromUA(final String ch) throws IllegalNameException {
        String result = switch (ch.toUpperCase()) {
            case "Г" -> "H";
            case "Ґ" -> "G";
            case "Є" -> "Ie";
            case "И" -> "Y";
            case "І", "Ї", "Й" -> "I";
            default -> convertUniversal(ch);
        };
        return Character.isUpperCase(ch.charAt(0)) ? result : result.toLowerCase();
    }

    private String convertUniversal(final String ch) throws IllegalNameException {
        String result = switch (ch.toUpperCase()) {
            case "А" -> "A";
            case "Б" -> "B";
            case "В" -> "V";
            case "Д" -> "D";
            case "Е" -> "E";
            case "Ж" -> "Zh";
            case "З" -> "Z";
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
            case "Ь", "'" -> "";
            case "Ю" -> "Iu";
            case "Я" -> "Ia";
            default -> null;
        };
        if (result == null) {
            throw new IllegalNameException(format("Name has illegal symbol '%s' because language is %s", ch, lang.name()));
        }
        return Character.isUpperCase(ch.charAt(0)) ? result : result.toLowerCase();
    }

    private String translateSpecialSymbols(final String ch) throws IllegalNameException {
        String result = switch (ch.toUpperCase()) {
            case "Е", "Є" -> "Ye";
            case "Ї" -> "Yi";
            case "Й" -> "Y";
            case "Ю" -> "Yu";
            case "Я" -> "Ya";
            default -> null;
        };
        if (result == null) {
            throw new IllegalNameException(format("Name has illegal symbol '%s' because language is %s", ch, lang.name()));
        }
        return Character.isUpperCase(ch.charAt(0)) ? result : result.toLowerCase();
    }

    private boolean charIsCyrillic(final String ch) {
        return Character.UnicodeBlock.of(ch.charAt(0)).equals(Character.UnicodeBlock.CYRILLIC) ||
               ch.equals("'") || ch.equals("’");
    }

    private boolean isGolosnyy(final char ch) {
        char[] golosniChars = {'А', 'Е', 'Є', 'И', 'І', 'Ї', 'О', 'У', 'Ю', 'Я', 'Ы', 'Э'};
        for (final char golosniyChar : golosniChars) {
            if (Character.toUpperCase(ch) == golosniyChar) {
                return true;
            }
        }
        return false;
    }

    private boolean isZnakMyakshenniaOrElse(final char ch) {
        return (ch == '\'' || ch == '’' || Character.toUpperCase(ch) == 'Ь' || Character.toUpperCase(ch) == 'Ъ');
    }

    private boolean isShyplyachyy(final char charAt) {
        char ch = Character.toUpperCase(charAt);
        return (ch == 'Ж' || ch == 'Ш' || ch == 'Щ');
    }
}
