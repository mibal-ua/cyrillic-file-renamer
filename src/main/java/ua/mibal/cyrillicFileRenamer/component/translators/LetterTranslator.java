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

import static java.lang.String.format;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Border.*;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Lang.RU;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Lang.UA;

/**
 * @author Michael Balakhon
 * @link http://t.me/mibal_ua
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
            throw new IllegalNameException("File don't have cyrillic symbols");
        }
        return newName.append(extension).toString();
    }

    protected String translate(final String word, final int i, final String letter) throws IllegalNameException, IllegalLanguageException {
        return null;
    }

    protected boolean isSpecialLetter(final String letter) {
        return (letter.equalsIgnoreCase("??") ||
                letter.equalsIgnoreCase("??") ||
                letter.equalsIgnoreCase("??") ||
                letter.equalsIgnoreCase("??") ||
                letter.equalsIgnoreCase("??") ||
                letter.equalsIgnoreCase("??"));
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

    protected String convertFromru(final String ch) throws IllegalLanguageException {
        String result = switch (ch.toUpperCase()) {
            case "??" -> "G";
            case "??" -> "E";
            case "??" -> "Yo";
            case "??" -> "I";
            case "??" -> "Y";
            case "??" -> "Y";

            default -> convertUniversal(ch, RU);
        };
        return Character.isUpperCase(ch.charAt(0)) ? result : result.toLowerCase();
    }

    protected String convertFromUA(final String ch) throws IllegalLanguageException {
        String result = switch (ch.toUpperCase()) {
            case "??" -> "H";
            case "??" -> "G";
            case "??" -> "Ie";
            case "??" -> "Y";
            case "??", "??", "??" -> "I";
            default -> convertUniversal(ch, UA);
        };
        return Character.isUpperCase(ch.charAt(0)) ? result : result.toLowerCase();
    }

    protected String convertUniversal(final String ch, final Lang lang) throws IllegalLanguageException {
        String result = switch (ch.toUpperCase()) {
            case "??" -> "A";
            case "??" -> "B";
            case "??" -> "V";
            case "??" -> "D";
            case "??" -> "E";
            case "??" -> "Zh";
            case "??" -> "Z";
            case "??" -> "K";
            case "??" -> "L";
            case "??" -> "M";
            case "??" -> "N";
            case "??" -> "O";
            case "??" -> "P";
            case "??" -> "R";
            case "??" -> "S";
            case "??" -> "T";
            case "??" -> "U";
            case "??" -> "F";
            case "??" -> "Kh";
            case "??" -> "Ts";
            case "??" -> "Ch";
            case "??" -> "Sh";
            case "??" -> "Shch";
            case "??", "'" -> "";
            case "??" -> "Iu";
            case "??" -> "Ia";
            default -> throw new IllegalLanguageException(format("Name has illegal symbol '%s' because language is %s", ch, lang.name()));
        };
        return Character.isUpperCase(ch.charAt(0)) ? result : result.toLowerCase();
    }

    protected String translateSpecialSymbols(final String ch, final Lang lang) throws IllegalNameException {
        String result = switch (ch.toUpperCase()) {
            case "??", "??" -> "Ye";
            case "??" -> "Yi";
            case "??" -> "Y";
            case "??" -> "Yu";
            case "??" -> "Ya";
            default -> throw new IllegalNameException(format("Name has illegal symbol '%s' because language is %s", ch, lang.name()));
        };
        return Character.isUpperCase(ch.charAt(0)) ? result : result.toLowerCase();
    }

    private boolean charIsCyrillic(final String ch) {
        return Character.UnicodeBlock.of(ch.charAt(0)).equals(Character.UnicodeBlock.CYRILLIC) ||
               ch.equals("'") || ch.equals("???");
    }

    protected boolean isGolosnyy(final char ch) {
        char[] golosniChars = {'??', '??', '??', '??', '??', '??', '??', '??', '??', '??', '??', '??'};
        for (final char golosniyChar : golosniChars) {
            if (Character.toUpperCase(ch) == golosniyChar) {
                return true;
            }
        }
        return false;
    }

    protected boolean isZnakMyakshenniaOrElse(final char ch) {
        return (ch == '\'' || ch == '???' || Character.toUpperCase(ch) == '??' || Character.toUpperCase(ch) == '??');
    }

    protected boolean isShyplyachyy(final char charAt) {
        char ch = Character.toUpperCase(charAt);
        return (ch == '??' || ch == '??' || ch == '??');
    }
}
