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
import ua.mibal.cyrillicFileRenamer.model.exceptions.IllegalLanguageException;
import ua.mibal.cyrillicFileRenamer.model.programMode.Lang;
import static java.lang.Character.UnicodeBlock;
import static java.lang.Character.isUpperCase;
import static java.lang.Character.toUpperCase;
import static java.lang.String.valueOf;
import static java.util.Map.entry;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Lang.RU;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Lang.UA;
import java.util.ArrayList;
import java.util.List;
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

    private final static Map<String, String> russianOfficialLetters = Map.of(
        "Ё", "E",
        "Й", "I",
        "Ъ", "Ie"
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

    private final static List<String> holosni = List.of(
        "А", "Е", "Є", "И", "І", "Ї", "О", "У", "Ю", "Я", "Ы", "Э"
    );

    private final static List<String> shypliachi = List.of(
        "Ж", "Ш", "Щ"
    );

    public String translate(final String oldName) throws IllegalLanguageException {
        final String name = getNameWithoutExtension(oldName);
        final String extension = getExtension(oldName);

        final String[] words = getWordsFromName(name);
        final List<String> newName = new ArrayList<>();
        for (final String word : words) {
            if (!word.matches("^[^а-яёА-ЯЁ]*[а-яёА-ЯЁ].*")) { // Regex to find at least one cyrillic character
                newName.add(word);
            } else {
                final String translatedWord = translateWord(word);
                newName.add(translatedWord);
            }
        }
//        if (newName.toString().equals(name)) {
//            throw new FileNameDontContainCyrillicSymbolsException("File don't contain cyrillic symbols");
//        }
        return newName.stream().reduce((prev, current) -> prev + current).get() + extension;
    }

    protected abstract String translateWord(final String word) throws IllegalLanguageException;

    protected boolean isSpecialLetter(final String letter) {
        return specialLetters.containsKey(letter.toUpperCase());
    }

    private String getNameWithoutExtension(final String oldName) {
        int index = oldName.lastIndexOf(".");
        if (index == -1) {
            return oldName;
        }
        return oldName.substring(0, index);
    }

    private String getExtension(final String oldName) {
        int index = oldName.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        return oldName.substring(index);
    }

    private String[] getWordsFromName(final String oldName) {
        return oldName.split("([" + Border.getBorders() + "])");
    }

    //TODO make convertFromRu and convertFromUA by one method
    protected String convertFromRu(final String ch) throws IllegalLanguageException {
        final String key = ch.toUpperCase();
        String newCh = russianLetters.get(key);
        if (newCh == null) {
            newCh = convertUniversal(ch, RU);
        }
        return isUpperCase(ch.charAt(0)) ? newCh : newCh.toLowerCase();
    }

    protected String convertFromUA(final String ch) throws IllegalLanguageException {
        final String key = ch.toUpperCase();
        String newCh = ukrainianLetters.get(key);
        if (newCh == null) {
            newCh = convertUniversal(ch, UA);
        }
        return isUpperCase(ch.charAt(0)) ? newCh : newCh.toLowerCase();
    }

    protected String convertFromOfficialRu(final String ch) throws IllegalLanguageException {
        final String key = ch.toUpperCase();
        String newCh = russianOfficialLetters.get(key);
        if (newCh == null) {
            newCh = convertFromRu(ch);
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

    protected boolean charIsCyrillic(final String ch) {
        return UnicodeBlock.of(ch.charAt(0)).equals(UnicodeBlock.CYRILLIC) ||
               ch.equals("'") || ch.equals("’");
    }

    protected boolean isHolosnyy(char ch) {
        return holosni.contains(valueOf(ch).toUpperCase());
    }

    protected boolean isZnakMyakshenniaOrElse(final char ch) {
        return (ch == '\'' || ch == '’' || toUpperCase(ch) == 'Ь' || toUpperCase(ch) == 'Ъ');
    }

    protected boolean isShypliachyy(final char ch) {
        return shypliachi.contains(valueOf(ch).toUpperCase());
    }
}
