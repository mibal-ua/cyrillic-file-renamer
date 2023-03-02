/*
 * Copyright (c) 2023. http://t.me/mibal_ua
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ua.mibal.cyrillicFileRenamer.component.FileManager;
import ua.mibal.cyrillicFileRenamer.model.programMode.LetterStandard;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Mykhailo Balakhon
 * @link t.me/mibal_ua
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ConsoleArgumentParser_UnitTest {

    @Mock
    private FileManager fileManager;

    private ConsoleArgumentParser argumentParser;

    @BeforeEach
    void beforeEach() {
        argumentParser = new ConsoleArgumentParser(fileManager);
    }

    @ParameterizedTest
    @Order(1)
    @ValueSource(
        strings = {"/path/to/catalog", "\\Users\\admin", "Path to catalog"}
    )
    void parse_should_configure_correct_path(final String arg) {
        argumentParser.parse(new String[] {arg});
        assertEquals(arg, argumentParser.getPath());
    }

    @Test
    @Order(2)
    void arse_should_configure_correct_this_arg() {
        when(fileManager.getParentDir(any())).thenReturn("test/dir");

        argumentParser.parse(new String[] {"this"});
        assertEquals("test/dir", argumentParser.getPath());
    }

    @ParameterizedTest
    @Order(3)
    @EnumSource(LetterStandard.class)
    void parse_should_configure_correct_letter_standard(final LetterStandard standard) {
        argumentParser.parse(new String[] {standard.toString()});
        assertEquals(standard, argumentParser.getLetterStandard());
    }

    @ParameterizedTest
    @Order(4)
    @CsvSource({"this,/path/to/directory/,test/dir", "path1,path2,path1"})
    void parse_should_configure_path_in_correct_order(final String arg1, final String arg2, final String expected) {
        when(fileManager.getParentDir(any())).thenReturn("test/dir");

        argumentParser.parse(new String[] {arg1, arg2});
        assertEquals(expected, argumentParser.getPath());
    }

    @ParameterizedTest
    @Order(5)
    @CsvSource({"path,official,path,OFFICIAL", "this,extended,test/dir,EXTENDED"})
    void parse_should_configure_double_args(final String arg1, final String arg2,
                                            final String expectedPath,
                                            final String expectedStandard) {
        when(fileManager.getParentDir(any())).thenReturn("test/dir");

        argumentParser.parse(new String[] {arg1, arg2});
        assertEquals(expectedPath, argumentParser.getPath());
        assertEquals(expectedStandard, argumentParser.getLetterStandard().name());
    }

}
