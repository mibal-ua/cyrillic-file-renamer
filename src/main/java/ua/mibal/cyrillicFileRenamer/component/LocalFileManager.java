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

import ua.mibal.cyrillicFileRenamer.model.programMode.OS;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Mykhailo Balakhon
 * @link https://t.me/mibal_ua
 */
public class LocalFileManager implements FileManager {

    private final String pathExample;

    private final static String resultingDirName = "renamedToLatin";

    private final static String[] IGNORED_FILE_NAMES = {
        resultingDirName, "Thumbs.db", "$RECYCLE.BIN", "desktop.ini", "cyrillic-file-renamer-"
    };

    private File resultingDir;

    public LocalFileManager(final OS os) {
        this.pathExample = os.getPathExample();
    }

    @Override
    public File[] getFilesFromDirectory(final String pathToCatalog) {
        final File directory = new File(pathToCatalog);
        return directory.listFiles((dir, file) -> !isIgnoredFile(file));
    }

    private boolean isIgnoredFile(final String fileName) {
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
    public void createResultingDirectory(final String pathToCurrentDir) {
        final File file = new File(pathToCurrentDir + "/" + resultingDirName);
        file.mkdir();
        resultingDir = file;
    }

    @Override
    public void createRenamedFile(final File sourceFile,
                                  final String newName) throws IOException {
        Files.copy(sourceFile.toPath(),
            Path.of((resultingDir.toPath() + "/" + newName)));
    }

    @Override
    public String testAndGetCorrectPath(String userPath) {
        if (userPath == null) {
            return null;
        }
        if (new File(userPath).exists()) {
            return userPath;
        }
        if (userPath.length() == 0) {
            return null;
        }
        if (userPath.charAt(0) != '/') {
            userPath = "/" + userPath;
        }
        if (new File(userPath).exists()) {
            return userPath;
        }
        return null;
    }

    @Override
    public String getParentDir(final String currentPath) {
        final String newPath = testAndGetCorrectPath(currentPath);
        if (newPath == null) {
            return null;
        }
        return new File(newPath).getAbsoluteFile().getParent();
    }

    @Override
    public String getPathExample() {
        return pathExample;
    }
}
