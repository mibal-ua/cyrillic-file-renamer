/*
 * Copyright (c) 2023. http://t.me/mibal_ua
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ua.mibal.cyrillicFileRenamer.model.exceptions.IllegalLanguageException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Mykhailo Balakhon
 * @link t.me/mibal_ua
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExtendedLetterTranslator_UnitTest {

    private ExtendedLetterTranslator translator;

    @BeforeEach
    void beforeEach() {
        translator = new ExtendedLetterTranslator();
    }

    @ParameterizedTest
    @Order(1)
    @CsvSource({
        // Regular
        "А,A", "Б,B", "В,V", "Г,H", "Ґ,G", "Д,D", "Е,E", "Є,Ye,", "Ж,Zh", "З,Z",
        "І,I", "Ї,Yi", "Й,Y", "К,K", "Л,L", "М,M", "Н,N", "О,O", "П,P", "Р,R", "С,S",
        "Т,T", "У,U", "Ф,F", "Х,Kh", "Ц,Ts", "Ч,Ch", "Ш,Sh", "Щ,Shch", "Ю,Yu", "Я,Ya",

        // Special
        "ПЄ,PIe", "ПЇ,PI", "ПЙ,PI", "ПЮ,PIu", "ПЯ,PIa", "МЬ,M",

        // Special cases
        "ЗГ,ZGh" /* not "ZH" */,
        /*
            the next 2 test is about: if previous letter is 'Holosna',
            then we use special form of next letter
         */
        "СІМ'Я,SIMYa" /* not "SIMIa" */,
        "АЄЇЙОЮЯ,AYeYiYOYuYa" /* not "AEIIOIuIa" */
    })
    void translateWord_should_translate_according_to_official_regulations(final String arg,
                                                                          final String excepted)
        throws IllegalLanguageException {
        assertEquals(excepted, translator.translateWord(arg));
    }

    @ParameterizedTest
    @Order(2)
    @CsvSource({"ё", "э", "ы", "ъ"})
    void translateWord_should_throw_IllegalLanguageException_if_unsupported_lang(final String letter) {
        assertThrows(IllegalLanguageException.class, () -> translator.translateWord(letter));
    }
}
