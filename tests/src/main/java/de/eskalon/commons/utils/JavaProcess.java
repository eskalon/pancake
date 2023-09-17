/*
 * Copyright 2023 eskalon
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.eskalon.commons.utils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Simple utilites for dealing with Java processes.
 */
public final class JavaProcess {

	private JavaProcess() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Executes the {@code main()} method of a given java class on another
	 * process.
	 * 
	 * @param clazz
	 *            the java class
	 * @param args
	 *            the start arguments passed to the class's {@code main()}
	 *            method
	 */
	public static void exec(Class clazz, List<String> args) {
		List<String> command = new LinkedList<>();
		command.add(ProcessHandle.current().info().command().get());
		command.add("-cp");
		command.add(System.getProperty("java.class.path"));
		command.add(clazz.getName());
		if (args != null) {
			command.addAll(args);
		}

		try {
			Process process = (new ProcessBuilder(command)).inheritIO().start();
		} catch (IOException e) {
			System.err.println(
					"There was a problem executing the provided class.");
			e.printStackTrace();
		}
	}

}