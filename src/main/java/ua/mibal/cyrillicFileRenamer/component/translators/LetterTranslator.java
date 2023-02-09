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

package ua.mibal.cyrillicFileRenamer.component.translators;

import ua.mibal.cyrillicFileRenamer.model.DynaStringArray;
import ua.mibal.cyrillicFileRenamer.model.exceptions.IllegalLanguageException;
import ua.mibal.cyrillicFileRenamer.model.exceptions.IllegalNameException;
import ua.mibal.cyrillicFileRenamer.model.programMode.Lang;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Border.DOT;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Border.EMDASH;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Border.ENDASH;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Border.HYPHENMINUS;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Border.MINUS;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Border.SPACE;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Border.UNDERSCORE;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Lang.RU;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Lang.UA;

/**
 * @author Mykhailo Balakhon
 * @link https://t.me/mibal_ua
 */
public abstract class LetterTranslator {

    public String translateName(final String oldName) throws IllegalNameException, IllegalLanguageException {
        String[] result = getSeparateExtensionAndName(oldName);
        String name = result[0];
        String extension = result[1];
        String[] words = getWordsFromName(name);
        DynaStringArray newNameArray = new DynaStringArray(words.length);
        for (final String word : words) {
            StringBuilder newWord = new StringBuilder();
            String newLetter;
            for (int i = 0; i < word.length(); i++) {
                String letter = String.valueOf(word.charAt(i));
                if (!charIsCyrillic(letter)) {
                    newLetter = letter;
                } else {
                    newLetter = translate(word, i, letter);
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
            throw new IllegalNameException("File don't contain cyrillic symbols");
        }
        return newName.append(extension).toString();
    }

    protected String translate(final String word, final int i, final String letter)
        throws IllegalNameException, IllegalLanguageException {
        return null;
    }

    protected boolean isSpecialLetter(final String letter) {
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
        return new String[] {name, extension};
    }

    private String[] getWordsFromName(final String oldName) {
        if (oldName.length() <= 1) {
            return oldName.length() == 1 ?
                new String[] {String.valueOf(oldName.charAt(0))} :
                new String[] {""};
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

    protected String convertFromru(final String ch) throws IllegalLanguageException {
        String result = switch (ch.toUpperCase()) {
            case "Г" -> "G";
            case "Э" -> "E";
            case "Ё" -> "Yo";
            case "И" -> "I";
            case "Й" -> "Y";
            case "Ы" -> "Y";

            default -> convertUniversal(ch, RU);
        };
        return Character.isUpperCase(ch.charAt(0)) ? result : result.toLowerCase();
    }

    protected String convertFromUA(final String ch) throws IllegalLanguageException {
        String result = switch (ch.toUpperCase()) {
            case "Г" -> "H";
            case "Ґ" -> "G";
            case "Є" -> "Ie";
            case "И" -> "Y";
            case "І", "Ї", "Й" -> "I";
            default -> convertUniversal(ch, UA);
        };
        return Character.isUpperCase(ch.charAt(0)) ? result : result.toLowerCase();
    }

    protected String convertUniversal(final String ch, final Lang lang) throws IllegalLanguageException {
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
            default -> throw new IllegalLanguageException(ch, lang);
        };
        return Character.isUpperCase(ch.charAt(0)) ? result : result.toLowerCase();
    }

    protected String translateSpecialSymbols(final String ch, final Lang lang) throws IllegalLanguageException {
        String result = switch (ch.toUpperCase()) {
            case "Е", "Є" -> "Ye";
            case "Ї" -> "Yi";
            case "Й" -> "Y";
            case "Ю" -> "Yu";
            case "Я" -> "Ya";
            default -> throw new IllegalLanguageException(ch, lang);
        };
        return Character.isUpperCase(ch.charAt(0)) ? result : result.toLowerCase();
    }

    private boolean charIsCyrillic(final String ch) {
        return Character.UnicodeBlock.of(ch.charAt(0)).equals(Character.UnicodeBlock.CYRILLIC) ||
               ch.equals("'") || ch.equals("’");
    }

    protected boolean isGolosnyy(final char ch) {
        char[] golosniChars = {'А', 'Е', 'Є', 'И', 'І', 'Ї', 'О', 'У', 'Ю', 'Я', 'Ы', 'Э'};
        for (final char golosniyChar : golosniChars) {
            if (Character.toUpperCase(ch) == golosniyChar) {
                return true;
            }
        }
        return false;
    }

    protected boolean isZnakMyakshenniaOrElse(final char ch) {
        return (ch == '\'' || ch == '’' || Character.toUpperCase(ch) == 'Ь' || Character.toUpperCase(ch) == 'Ъ');
    }

    protected boolean isShyplyachyy(final char charAt) {
        char ch = Character.toUpperCase(charAt);
        return (ch == 'Ж' || ch == 'Ш' || ch == 'Щ');
    }
}
