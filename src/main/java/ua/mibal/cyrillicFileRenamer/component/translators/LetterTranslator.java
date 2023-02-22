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
import ua.mibal.cyrillicFileRenamer.model.exceptions.FileNameDontContainCyrillicSymbolsException;
import ua.mibal.cyrillicFileRenamer.model.exceptions.IllegalLanguageException;
import static java.lang.Character.UnicodeBlock;
import static java.lang.Character.isUpperCase;
import static java.lang.Character.toUpperCase;
import static java.lang.String.valueOf;
import static java.text.MessageFormat.format;
import static java.util.Map.entry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Mykhailo Balakhon
 * @link https://t.me/mibal_ua
 */
public abstract class LetterTranslator {

    // Regex to find at least one cyrillic character
    public static final String REG_EXP = "^[^а-яёА-ЯЁ]*[а-яёА-ЯЁ].*";

    private final static Map<String, String> specialLetters = Map.of(
        "Е", "Ye",
        "Є", "Ye",
        "Ї", "Yi",
        "Й", "Y",
        "Ю", "Yu",
        "Я", "Ya"
    );

    private final static Map<String, String> letters = Map.ofEntries(
        entry("А", "A"),
        entry("Б", "B"),
        entry("В", "V"),
        entry("Г", "H"),
        entry("Ґ", "G"),
        entry("Д", "D"),
        entry("Е", "E"),
        entry("Є", "Ie"),
        entry("Ж", "Zh"),
        entry("З", "Z"),
        entry("И", "Y"),
        entry("І", "I"),
        entry("Ї", "I"),
        entry("Й", "I"),
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

    private final static List<String> holosni = List.of(
        "А", "Е", "Є", "И", "І", "Ї", "О", "У", "Ю", "Я"
    );

    private static boolean isBorder(final char letter) {
        for (final Border value : Border.values()) {
            if (String.valueOf(letter).equals(value.getBorder())) {
                return true;
            }
        }
        return false;
    }

    public String translate(final String oldName)
        throws IllegalLanguageException, FileNameDontContainCyrillicSymbolsException {
        if (notContainCyrillicLetters(oldName)) {
            throw new FileNameDontContainCyrillicSymbolsException(
                "File don't contain cyrillic symbols");
        }
        final String name = getNameWithoutExtension(oldName);
        final String extension = getExtension(oldName);

        final List<String> words = getWordsFromName(name);
        final StringBuilder newName = new StringBuilder();
        for (final String word : words) {
            if (notContainCyrillicLetters(word)) {
                newName.append(word);
            } else {
                final String translatedWord = translateWord(word);
                newName.append(translatedWord);
            }
        }
        return newName + extension;
    }

    abstract String translateWord(final String word) throws IllegalLanguageException;

    protected String convert(final String ch) throws IllegalLanguageException {
        final String key = ch.toUpperCase();
        String newCh = letters.get(key);
        if (newCh == null) {
            throw new IllegalLanguageException(ch);
        }
        return isUpperCase(ch.charAt(0)) ? newCh : newCh.toLowerCase();
    }

    protected String convertSpecial(final String letter) {
        final String newCh = specialLetters.get(letter.toUpperCase());
        if (newCh == null) {
            throw new IllegalArgumentException(format(
                "Transliterator can't process ''{0}'' letter ", letter
            ));
        }
        return isUpperCase(letter.charAt(0)) ? newCh : newCh.toLowerCase();
    }

    private List<String> getWordsFromName(final String name) {
        final List<String> result = new ArrayList<>();
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            final char letter = name.charAt(i);
            if (isBorder(letter)) {
                stringBuilder.append(letter);
                result.add(stringBuilder.toString());
                stringBuilder.setLength(0);
            } else {
                stringBuilder.append(letter);
            }
        }
        result.add(stringBuilder.toString());
        return List.copyOf(result);
    }

    private String getNameWithoutExtension(final String fullName) {
        int index = fullName.lastIndexOf(".");
        if (index == -1) {
            return fullName;
        }
        return fullName.substring(0, index);
    }

    private String getExtension(final String fullName) {
        int index = fullName.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        return fullName.substring(index);
    }

    private boolean notContainCyrillicLetters(final String string) {
        return !string.matches(REG_EXP);
    }

    protected boolean isSpecialLetter(final String letter) {
        return specialLetters.containsKey(letter.toUpperCase());
    }

    protected boolean letterIsNotCyrillic(final String letter) {
        return !UnicodeBlock.of(letter.charAt(0)).equals(UnicodeBlock.CYRILLIC) &&
               !letter.equals("'") && !letter.equals("’");
    }

    protected boolean isHolosnyy(final char ch) {
        return holosni.contains(valueOf(ch).toUpperCase());
    }

    protected boolean isZnakMiakshennia(final char ch) {
        return (ch == '\'' || ch == '’' || toUpperCase(ch) == 'Ь');
    }
}
