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
package ua.mibal.cyryllicFileRenamer.component;

import ua.mibal.cyryllicFileRenamer.model.Lang;
import ua.mibal.cyryllicFileRenamer.model.OS;

import static ua.mibal.cyryllicFileRenamer.model.Lang.RU;
import static ua.mibal.cyryllicFileRenamer.model.Lang.UA;
import static ua.mibal.cyryllicFileRenamer.model.OS.UNIX;
import static ua.mibal.cyryllicFileRenamer.model.OS.WINDOWS;

/**
 * @author Michael Balakhon
 * @link http://t.me/mibal_ua
 */
public class ArgumentParser {

    private String path;

    private Lang lang;

    private OS OS;

    public void parse(String[] args) {
        if (args[0].equalsIgnoreCase("this")) {
            this.path = System.getProperty("user.dir");
        } else {
            this.path = args[0];
        }
        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase(UA.name())) {
                lang = UA;
            } else if (args[1].equalsIgnoreCase(RU.name())) {
                lang = RU;
            }
        }
        if (args.length >= 3) {
            if (args[2].equalsIgnoreCase(UNIX.name())) {
                OS = UNIX;
            } else if (args[1].equalsIgnoreCase(WINDOWS.name())) {
                OS = WINDOWS;
            }
        }
        // add some parameters in future
    }

    public String getPath() {
        return path;
    }

    public Lang getLang() {
        return lang;
    }

    public OS getOS() {
        return OS;
    }
}
