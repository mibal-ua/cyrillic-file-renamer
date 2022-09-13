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

import ua.mibal.cyrillicFileRenamer.model.programMode.Lang;

import static java.lang.String.format;
import static ua.mibal.cyrillicFileRenamer.component.PathOperator.testPath;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Lang.RU;
import static ua.mibal.cyrillicFileRenamer.model.programMode.Lang.UA;

/**
 * @author Michael Balakhon
 * @link http://t.me/mibal_ua
 */
public class ArgumentParser {

    private String path;

    private Lang lang;

    public void parse(String[] args) {
        for (final String arg : args) {
            if (arg.equalsIgnoreCase("this")) {
                this.path = PathOperator.getParentFolder(System.getProperty("user.dir"));
            } else if (arg.equalsIgnoreCase(UA.name()) || arg.equalsIgnoreCase(RU.name())) {
                lang = Lang.valueOf(arg.toUpperCase());
            } else {
                this.path = testPath(arg);
                if (path == null) {
                    throw new IllegalArgumentException(format("Incorrect argument '%s'.", arg));
                }
            }
        }
    }

    public String getPath() {
        return path;
    }

    public Lang getLang() {
        return lang;
    }
}
