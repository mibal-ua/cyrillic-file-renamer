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

package ua.mibal.cyrillicFileRenamer.model.programMode;

import ua.mibal.cyrillicFileRenamer.component.translators.AbstractLetterTranslator;
import ua.mibal.cyrillicFileRenamer.component.translators.ExtendedLetterTranslator;
import ua.mibal.cyrillicFileRenamer.component.translators.OfficialLetterTranslator;

/**
 * @author Mykhailo Balakhon
 * @link https://t.me/mibal_ua
 */
public enum LetterStandard {

    OFFICIAL(new OfficialLetterTranslator()),

    EXTENDED(new ExtendedLetterTranslator());

    private final AbstractLetterTranslator letterTranslator;

    LetterStandard(final AbstractLetterTranslator letterTranslator) {
        this.letterTranslator = letterTranslator;
    }

    public AbstractLetterTranslator getLetterTranslator() {
        return letterTranslator;
    }
}
