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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Mykhailo Balakhon
 * @link https://t.me/mibal_ua
 */
public class LocalFileManager implements FileManager {

    private final static String[] IGNORED_FILE_NAMES = {
        "Thumbs.db", "$RECYCLE.BIN", "desktop.ini", "cyrillic-file-renamer-"
    };

    public File[] getFilesFromDirectory(final String pathToCatalog) {
        final File directory = new File(pathToCatalog);
        return directory.listFiles();
    }

    public boolean isIgnoredFile(final String fileName) {
        if (fileName.charAt(0) == '.') {
            return true;
        }
        for (final String ignoredFileName : IGNORED_FILE_NAMES) {
            if (fileName.contains(ignoredFileName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public File createResultingDirectory(final String pathToCatalog) {
        final File file = new File(pathToCatalog + "/renamedToLatin");
        file.mkdir();
        return file;
    }

    @Override
    public void createRenamedFile(final File sourceFile,
                                  final String newName,
                                  final File resultingDirectory) throws IOException {
        Files.copy(sourceFile.toPath(), Path.of((resultingDirectory.toPath() + "/" + newName)));
    }
}
