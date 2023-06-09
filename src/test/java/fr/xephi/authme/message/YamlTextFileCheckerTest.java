package fr.xephi.authme.message;

import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.resource.YamlFileReader;
import fr.xephi.authme.TestHelper;
import fr.xephi.authme.command.help.HelpSection;
import fr.xephi.authme.util.ExceptionUtils;
import fr.xephi.authme.util.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static fr.xephi.authme.message.MessagePathHelper.MESSAGES_FOLDER;
import static org.junit.Assert.fail;
import static tools.utils.FileIoUtils.listFilesOrThrow;

/**
 * Tests that all YML text files can be loaded.
 */
public class YamlTextFileCheckerTest {

    /** Contains all files of the MESSAGES_FOLDER. */
    private static File[] messageFiles;

    @BeforeClass
    public static void loadMessagesFiles() {
        File folder = TestHelper.getJarFile("/" + MESSAGES_FOLDER);
        messageFiles = listFilesOrThrow(folder);
    }

    @Test
    public void testAllMessagesYmlFiles() {
        checkFiles(
            MessagePathHelper::isMessagesFile,
            MessageKey.LOGIN_MESSAGE.getKey());
    }

    @Test
    public void testAllHelpYmlFiles() {
        checkFiles(
            MessagePathHelper::isHelpFile,
            HelpSection.ALTERNATIVES.getKey());
    }

    /**
     * Checks all files in the messages folder that match the given pattern.
     *
     * @param isRelevantFilePredicate predicate determining which files should be tested
     * @param mandatoryKey key present in all matched files
     */
    private void checkFiles(Predicate<String> isRelevantFilePredicate, String mandatoryKey) {
        List<String> errors = new ArrayList<>();

        boolean hasMatch = false;
        for (File file : messageFiles) {
            if (isRelevantFilePredicate.test(file.getName())) {
                checkFile(file, mandatoryKey, errors);
                hasMatch = true;
            }
        }

        if (!errors.isEmpty()) {
            fail("Errors while checking files\n-" + String.join("\n-", errors));
        } else if (!hasMatch) {
            fail("Could not find any files matching criteria");
        }
    }

    /**
     * Checks that the provided YAML file can be loaded and that it contains a non-empty text
     * for the provided mandatory key.
     *
     * @param file the file to check
     * @param mandatoryKey the key for which text must be present
     * @param errors collection of errors to add to if the verification fails
     */
    private void checkFile(File file, String mandatoryKey, List<String> errors) {
        try {
            PropertyReader reader = new YamlFileReader(file);
            if (StringUtils.isBlank(reader.getString(mandatoryKey))) {
                errors.add("Message for '" + mandatoryKey + "' is empty");
            }
        } catch (Exception e) {
            errors.add("Could not load file: " + ExceptionUtils.formatException(e));
        }
    }
}
