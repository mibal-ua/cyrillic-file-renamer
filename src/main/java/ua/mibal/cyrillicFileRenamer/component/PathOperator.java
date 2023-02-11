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
 * @author Mykhailo Balakhon
 * @link https://t.me/mibal_ua
 */
public class PathOperator {

    public static String testAndGetCorrectPath(String userPath) {
        if (userPath == null) {
            return null;
        }
        if (new File(userPath).exists()) {
            return userPath;
        }
        if (userPath.length() == 0) {
            return null; //TODO throw exception
        }
        if (userPath.charAt(0) != '/') {
            userPath = "/" + userPath;
        }
        if (new File(userPath).exists()) {
            return userPath;
        }
        return null; //TODO throw exception
    }

    public static String getParentFolder(final String path) {
        final String newPath = testAndGetCorrectPath(path);
        if (newPath == null) {
            return null;
        }
        return new File(newPath).getAbsoluteFile().getParent();
    }

    public static String getExamplePath() {
        return "/Users/home/path/to/catalog/";
    }
}
