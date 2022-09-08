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

package ua.mibal.cyrillicFileRenamer.component;

import java.io.File;

/**
 * @author Michael Balakhon
 * @link http://t.me/mibal_ua
 */
public class PathOperator {
    public static String testPath(final String userPath) {
        if (userPath == null) {
            return null;
        }
        if (userPath.length() != 0) {
            if (new File(userPath).exists()) {
                return userPath;
            }
            StringBuilder userPathBuilder1 = new StringBuilder(userPath);
            StringBuilder userPathBuilder2 = new StringBuilder(userPath);

            if (userPathBuilder1.charAt(0) != '/' && userPathBuilder1.charAt(0) != '\\') {
                userPathBuilder1.insert(0, '/');
                userPathBuilder2.insert(0, '\\');
            }
            if (new File(userPathBuilder1.toString()).exists()) {
                return userPathBuilder1.toString();
            } else if (new File(userPathBuilder2.toString()).exists()) {
                return userPathBuilder2.toString();
            }
        }
        return null;
    }

    public static String getParentFolder(final String path) {
        String newPath = testPath(path);
        if (newPath != null) {
            File file = new File(newPath);
            if (file.exists()) {
                return file.getAbsoluteFile().getParent();
            }
        }
        return null;
    }
}
