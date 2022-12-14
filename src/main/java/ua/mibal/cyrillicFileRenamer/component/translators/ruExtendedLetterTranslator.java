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
import ua.mibal.cyrillicFileRenamer.model.exceptions.IllegalNameException;

import static ua.mibal.cyrillicFileRenamer.model.programMode.Lang.RU;

/**
 * @author Michael Balakhon
 * @link http://t.me/mibal_ua
 */
public class ruExtendedLetterTranslator extends LetterTranslator {

    public ruExtendedLetterTranslator() {
        super();
    }

    @Override
    protected String translate(final String word, final int i, final String letter) throws IllegalNameException, IllegalLanguageException {
        if (isSpecialLetter(letter)) { // if lang ru and official - return false
            if (i == 0) {
                return translateSpecialSymbols(letter, RU);
            } else if (isGolosnyy(word.charAt(i - 1)) || isZnakMyakshenniaOrElse(word.charAt(i - 1))) {
                return translateSpecialSymbols(letter, RU);
            } else {
                return convertFromru(letter);
            }
        } else if (i != 0 && letter.equalsIgnoreCase("И") &&
                   isShyplyachyy(word.charAt(i - 1))) {
            return Character.isUpperCase(letter.charAt(0)) ? "Y" : "y";
        } else {
            return convertFromru(letter);
        }
    }
}
