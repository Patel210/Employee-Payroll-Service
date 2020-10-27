package com.capgemini.fileniotest; 

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.capgemini.filenioapi.WatcherService;

public class WatcherServiceTest {
	private static final String HOME = System.getProperty("user.home");
	private static String PLAY_WITH_FILES = "\\eclipse-workspace\\EmployeePayrollService\\TempPlayFiles";

	@Test
	public void givenADirectoryWhenWatchedListsAllTheActivities() throws IOException {
		Path dir = Paths.get(HOME + PLAY_WITH_FILES);
		Files.list(dir).filter(Files::isRegularFile).forEach(System.out::println);
		new WatcherService(dir).processEvents();
	}

}
