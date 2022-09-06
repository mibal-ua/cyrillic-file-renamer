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

import java.util.Arrays;

/**
 * @author Michael Balakhon
 * @link http://t.me/mibal_ua
 */
public class DynaStringArray {

    private String[] result;

    private int count;

    public DynaStringArray() {
        this(5);
    }

    public DynaStringArray(int size) {
        result = new String[size];
    }

    public void add(String value) {
        if (count == result.length) {
            grow(result.length == 0 ? 5 : result.length * 2);
        }
        result[count++] = value;
    }

    private void grow(int newLength) {
        String[] newArray = new String[newLength];
        System.arraycopy(result, 0, newArray, 0, result.length);
        result = newArray;
    }

    public int length() {
        return count;
    }

    public String[] toArray() {
        return Arrays.copyOf(result, count);
    }
}
