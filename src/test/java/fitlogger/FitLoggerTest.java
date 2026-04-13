package fitlogger;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FitLoggerTest {
    @Test
    void run_invalidExitThenEndOfInput_terminatesWithoutUnexpectedError() {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setIn(new ByteArrayInputStream("exit now\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output));
            System.setErr(new PrintStream(new ByteArrayOutputStream()));

            assertTimeoutPreemptively(Duration.ofSeconds(1), () -> new FitLogger().run());
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
            System.setErr(originalErr);
        }

        String appOutput = output.toString(StandardCharsets.UTF_8);
        assertTrue(appOutput.contains("[ERROR] Invalid format for exit."));
        assertFalse(appOutput.contains("Unexpected error: No line found"));
    }
}
