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
public class ruOfficialLetterTranslator extends LetterTranslator {

    @Override
    protected String translate(final String word, final int i, final String letter) throws IllegalLanguageException, IllegalNameException {
        return convertFromOfficialru(letter);
    }

    private String convertFromOfficialru(final String ch) throws IllegalLanguageException {
        String result = switch (ch.toUpperCase()) {
            case "Г" -> "G";
            case "Ё", "Э" -> "E";
            case "И", "Й" -> "I";
            case "Ы" -> "Y";
            case "Ъ" -> "Ie";
            default -> convertUniversal(ch, RU);
        };
        return Character.isUpperCase(ch.charAt(0)) ? result : result.toLowerCase();
    }
}
