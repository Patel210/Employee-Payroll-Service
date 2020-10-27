package com.capgemini.fileniotest;

import static org.junit.Assert.assertTrue; 

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;

import org.junit.Test;

import com.capgemini.filenioapi.FileUtils;

public class FileNIOAPITest {
	private static final String HOME = System.getProperty("user.home");
	private static String PLAY_WITH_FILES = "\\eclipse-workspace\\EmployeePayrollService\\TempPlayFiles";

	@Test
	public void givenPathWhenCheckedThenConfirm() throws IOException {
		Path homePath = Paths.get(HOME);
		assertTrue(Files.exists(homePath));

		Path playPath = Paths.get(HOME + PLAY_WITH_FILES);
		if (Files.exists(playPath)) {
			FileUtils.deleteFiles(playPath.toFile());
		}
		assertTrue(Files.notExists(playPath));

		Files.createDirectories(playPath);
		assertTrue(Files.exists(playPath));

		IntStream.range(1, 10).forEach(counter -> {
			Path tempPath = Paths.get(playPath + "/tempFile" + counter);
			assertTrue(Files.notExists(tempPath));
			try {
				Files.createFile(tempPath);
			} catch (IOException e) {

			}
			assertTrue(Files.exists(tempPath));
		});

		Files.newDirectoryStream(playPath).forEach(System.out::println);
		Files.newDirectoryStream(playPath, path -> path.toFile().isFile() && path.toString().startsWith("tempFile"))
				.forEach(System.out::println);
	}
}
