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

import ua.mibal.cyrillicFileRenamer.model.Border;
import ua.mibal.cyrillicFileRenamer.model.DynaStringArray;
import ua.mibal.cyrillicFileRenamer.model.exceptions.FileNameDontContainCyrillicSymbolsException;
import ua.mibal.cyrillicFileRenamer.model.exceptions.IllegalLanguageException;
import ua.mibal.cyrillicFileRenamer.model.programMode.Lang;
import static java.lang.Character.UnicodeBlock;
import static java.lang.Character.isUpperCase;
import static java.lang.Character.toUpperCase;
import static java.util.Map.entry;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Lang.RU;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Lang.UA;
import java.util.Map;

/**
 * @author Mykhailo Balakhon
 * @link https://t.me/mibal_ua
 */
public abstract class LetterTranslator {

    private final static Map<String, String> ukrainianLetters = Map.of(
        "Г", "H",
        "Ґ", "G",
        "Є", "Ie",
        "И", "Y",
        "І", "I",
        "Ї", "I",
        "Й", "I"
    );

    private final static Map<String, String> russianLetters = Map.of(
        "Г", "G",
        "Э", "E",
        "Ё", "Yo",
        "И", "I",
        "Й", "Y",
        "Ы", "Y"
    );

    private final static Map<String, String> universalLetters = Map.ofEntries(
        entry("А", "A"),
        entry("Б", "B"),
        entry("В", "V"),
        entry("Д", "D"),
        entry("Е", "E"),
        entry("Ж", "Zh"),
        entry("З", "Z"),
        entry("К", "K"),
        entry("Л", "L"),
        entry("М", "M"),
        entry("Н", "N"),
        entry("О", "O"),
        entry("П", "P"),
        entry("Р", "R"),
        entry("С", "S"),
        entry("Т", "T"),
        entry("У", "U"),
        entry("Ф", "F"),
        entry("Х", "Kh"),
        entry("Ц", "Ts"),
        entry("Ч", "Ch"),
        entry("Ш", "Sh"),
        entry("Щ", "Shch"),
        entry("Ь", ""),
        entry("'", ""),
        entry("Ю", "Iu"),
        entry("Я", "Ia")
    );

    private final static Map<String, String> specialLetters = Map.of(
        "Е", "Ye",
        "Є", "Ye",
        "Ї", "Yi",
        "Й", "Y",
        "Ю", "Yu",
        "Я", "Ya"
    );

    public String translateName(final String oldName)
        throws FileNameDontContainCyrillicSymbolsException, IllegalLanguageException {
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
            throw new FileNameDontContainCyrillicSymbolsException("File don't contain cyrillic symbols");
        }
        return newName.append(extension).toString();
    }

    protected abstract String translate(final String word, final int i, final String letter)
        throws FileNameDontContainCyrillicSymbolsException, IllegalLanguageException;

    protected boolean isSpecialLetter(final String letter) {
        final String newCh = specialLetters.get(letter.toUpperCase());
        return newCh != null;
    }

    private String[] getSeparateExtensionAndName(final String oldName) {
        int index = oldName.lastIndexOf(".");
        if (index == -1) {
            return new String[] {oldName, ""};
        }
        return new String[] {oldName.substring(0, index), oldName.substring(index)};
    }

    //TODO return List
    private String[] getWordsFromName(final String oldName) {
        if (oldName.length() <= 1) {
            return oldName.length() == 1 ?
                new String[] {String.valueOf(oldName.charAt(0))} :
                new String[] {""};
        }
        DynaStringArray dynaResult = new DynaStringArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < oldName.length(); i++) {
            boolean isThisABorder = false;
            char cha = oldName.charAt(i);
            String ch = String.valueOf(cha);
            for (final Border border : Border.values()) {
                if (ch.equals(border.getBorder())) {
                    dynaResult.add(stringBuilder.append(ch).toString());
                    stringBuilder = new StringBuilder();
                    isThisABorder = true;
                }
            }
            if (!isThisABorder) {
                if ((i != (oldName.length() - 1)) && isUpperCase(oldName.charAt(i + 1))) {
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

    //TODO make convertFromRu and convertFromUA by one method
    protected String convertFromRu(final String ch) throws IllegalLanguageException {
        final String key = ch.toUpperCase();
        String newCh = russianLetters.get(key);
        if (newCh == null) {
            newCh = universalLetters.get(key);
            if (newCh == null) {
                throw new IllegalLanguageException(ch, RU);
            }
        }
        return isUpperCase(ch.charAt(0)) ? newCh : newCh.toLowerCase();
    }

    protected String convertFromUA(final String ch) throws IllegalLanguageException {
        final String key = ch.toUpperCase();
        String newCh = ukrainianLetters.get(key);
        if (newCh == null) {
            newCh = universalLetters.get(key);
            if (newCh == null) {
                throw new IllegalLanguageException(ch, UA);
            }
        }
        return isUpperCase(ch.charAt(0)) ? newCh : newCh.toLowerCase();
    }

    protected String convertUniversal(final String ch, final Lang lang) throws IllegalLanguageException {
        final String key = ch.toUpperCase();
        final String newCh = universalLetters.get(key);
        if (newCh == null) {
            throw new IllegalLanguageException(ch, lang);
        }
        return isUpperCase(ch.charAt(0)) ? newCh : newCh.toLowerCase();
    }

    protected String translateSpecialSymbols(final String ch, final Lang lang) throws IllegalLanguageException {
        final String newCh = specialLetters.get(ch.toUpperCase());
        if (newCh == null) {
            throw new IllegalLanguageException(ch, lang);
        }
        return isUpperCase(ch.charAt(0)) ? newCh : newCh.toLowerCase();
    }

    private boolean charIsCyrillic(final String ch) {
        return UnicodeBlock.of(ch.charAt(0)).equals(UnicodeBlock.CYRILLIC) ||
               ch.equals("'") || ch.equals("’");
    }

    protected boolean isGolosnyy(char ch) {
        char[] golosniChars = {
            'А', 'Е', 'Є', 'И', 'І', 'Ї', 'О', 'У', 'Ю', 'Я', 'Ы', 'Э'
        };
        ch = toUpperCase(ch);
        for (final char golosniyChar : golosniChars) {
            if (ch == golosniyChar) {
                return true;
            }
        }
        return false;
    }

    protected boolean isZnakMyakshenniaOrElse(final char ch) {
        return (ch == '\'' || ch == '’' || toUpperCase(ch) == 'Ь' || toUpperCase(ch) == 'Ъ');
    }

    protected boolean isShyplyachyy(final char ch) {
        final char cha = toUpperCase(ch);
        return (cha == 'Ж' || cha == 'Ш' || cha == 'Щ');
    }
}
