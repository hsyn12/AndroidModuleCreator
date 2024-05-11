package com.tr.xyz.modulecreator;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;

public final class Dev {
	
	private Dev() {
		throw new IllegalStateException("Utility class");
	}
	
	/**
	 * Prints the given object to the console after formatting it with the provided arguments.
	 *
	 * @param obj  the object to be printed (can be null)
	 * @param args the arguments to be formatted into the string representation of the object
	 */
	public static void print(Object obj, Object... args) {
		System.out.println(obj != null ? String.format(obj.toString(), args) : null);
	}
	
	/**
	 * Reads the content of the given file and returns.
	 *
	 * @param file the file to be read
	 * @return contents of the file
	 */
	public static String readFile(File file) {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			return reader.lines().collect(Collectors.joining("\n"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Write a string to a file.
	 *
	 * @param file the file to write to
	 * @param str  the string to write
	 */
	public static boolean writeFile(File file, String str) {
		try {
			var writer = new FileWriter(file);
			writer.write(str);
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Appends a string to a file at the end.
	 *
	 * @param file file to write to
	 * @param str  string to write
	 * @return true if successful
	 */
	public static boolean appendFile(File file, String str) {
		try {
			var writer = new FileWriter(file, true);
			writer.write(str);
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static boolean isNullOrBlank(String str) {
		return str == null || str.isBlank();
	}
	
	public static boolean isNotNullOrBlank(String str) {
		return str != null && !str.isBlank();
	}
	
	@NotNull
	public static String format(String str, Object... args) {
		return String.format(str, args);
	}
	
}
