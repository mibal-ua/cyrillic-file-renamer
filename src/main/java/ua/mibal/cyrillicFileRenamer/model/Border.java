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

package ua.mibal.cyrillicFileRenamer.model;

/**
 * @author Mykhailo Balakhon
 * @link https://t.me/mibal_ua
 */
public enum Border {

    HYPHENMINUS("-"),

    ENDASH("–"),

    EMDASH("—"),

    MINUS("−"),

    SPACE(" "),

    UNDERSCORE("_"),

    DOT(".");

    private final String border;

    private static String borders = "";

    static {
        for (final Border value : values()) {
            borders += value.getBorder();
        }
    }

    Border(final String border) {
        this.border = border;
    }

    public String getBorder() {
        return border;
    }

    public static String getBorders() {
        return borders;
    }
}
