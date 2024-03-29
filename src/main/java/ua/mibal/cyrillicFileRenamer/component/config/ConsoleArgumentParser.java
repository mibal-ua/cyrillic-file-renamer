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

package ua.mibal.cyrillicFileRenamer.component.config;

import ua.mibal.cyrillicFileRenamer.component.FileManager;
import ua.mibal.cyrillicFileRenamer.model.programMode.LetterStandard;
import static ua.mibal.cyrillicFileRenamer.model.programMode.LetterStandard.EXTENDED;
import static ua.mibal.cyrillicFileRenamer.model.programMode.LetterStandard.OFFICIAL;

/**
 * @author Mykhailo Balakhon
 * @link https://t.me/mibal_ua
 */
public class ConsoleArgumentParser {

    private final FileManager fileManager;

    private String path;

    private LetterStandard letterStandard;

    public ConsoleArgumentParser(final FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public void parse(String[] args) {
        for (final String arg : args) {
            if (arg.equalsIgnoreCase("this")) {
                this.path = fileManager.getParentDir(System.getProperty("user.dir"));
            } else if (arg.equalsIgnoreCase(OFFICIAL.name()) || arg.equalsIgnoreCase(EXTENDED.name())) {
                letterStandard = LetterStandard.valueOf(arg.toUpperCase());
            } else {
                if (this.path == null) {
                    this.path = arg;
                }
            }
        }
    }

    public LetterStandard getLetterStandard() {
        return letterStandard;
    }

    public String getPath() {
        return path;
    }
}
