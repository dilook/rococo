package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.model.allure.AllureResult;
import guru.qa.rococo.model.allure.AllureResults;
import guru.qa.rococo.service.impl.AllureApiClient;
import lombok.extern.java.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Log
public class AllureSenderResultExtension implements SuiteExtension {

    private static final boolean isDocker = "docker".equals(System.getProperty("test.env"));
    private static final String EXECUTOR = System.getenv("HEAD_COMMIT_MESSAGE");
    private static final String PROJECT_ID = "dusoltsev";
    private static final String EXECUTION_TYPE = System.getenv("EXECUTION_TYPE");
    private static final String BUILD_URL = System.getenv("BUILD_URL");
    private static final String ALLURE_RESULTS_PATH = "./rococo-e-2-e-tests/build/allure-results";

    private final AllureApiClient allureApiClient = new AllureApiClient(System.getenv("ALLURE_DOCKER_API"));

    @Override
    public void afterSuite() {
        if (!isDocker) {
            return;
        }

        log.info("Start sending allure results to Allure server...");

        allureApiClient.getOrCreateProject(PROJECT_ID);
        allureApiClient.sendResults(PROJECT_ID, getResults());
        allureApiClient.generateReport(PROJECT_ID, EXECUTOR, BUILD_URL, EXECUTION_TYPE);

        log.info("Allure results sent successfully!");
    }


    private AllureResults getResults() {
        File resultsDirectory = new File(ALLURE_RESULTS_PATH);
        log.info("Allure results directory: " + resultsDirectory.getAbsolutePath());

        File[] resultFiles = resultsDirectory.getAbsoluteFile().listFiles(File::isFile);
        if (resultFiles == null || resultFiles.length == 0) {
            throw new RuntimeException("No allure results found in " + ALLURE_RESULTS_PATH);
        }
        List<AllureResult> results = createAllureResults(resultFiles);
        return new AllureResults(results);
    }

    private List<AllureResult> createAllureResults(File[] files) {
        return Arrays.stream(files).map(file -> new AllureResult(
                file.getName(),
                getContentBase64(file)
        )).toList();
    }

    private String getContentBase64(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] fileBytes = new byte[(int) file.length()];
            if (fis.read(fileBytes) != fileBytes.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }
            byte[] encodedBytes = java.util.Base64.getEncoder().encode(fileBytes);
            return new String(encodedBytes, java.nio.charset.StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to encode file to Base64: " + file.getName(), e);
        }
    }
}
