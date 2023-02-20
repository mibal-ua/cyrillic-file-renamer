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

import ua.mibal.cyrillicFileRenamer.model.exceptions.IllegalLanguageException;

/**
 * @author Mykhailo Balakhon
 * @link https://t.me/mibal_ua
 */
public final class UaOfficialLetterTranslator extends LetterTranslator {

    public UaOfficialLetterTranslator() {
        super();
    }

    @Override
    protected String translateWord(final String word) throws IllegalLanguageException {
        final StringBuilder newName = new StringBuilder();
        String letter;
        for (int i = 0; i < word.length(); i++) {
            letter = String.valueOf(word.charAt(i));
            if (letterIsNotCyrillic(letter)) {
                newName.append(letter);
                continue;
            }
            String newLetter;
            if (i == 0 && isSpecialLetter(letter)) {
                newLetter = convertSpecial(letter);
            } else if (i != 0 && letter.equalsIgnoreCase("Г") &&
                       String.valueOf(word.charAt(i - 1)).equalsIgnoreCase("З")) {
                newLetter = Character.isUpperCase(letter.charAt(0)) ? "Gh" : "gh";
            } else {
                newLetter = convert(letter);
            }
            newName.append(newLetter);
        }
        return newName.toString();
    }
}
