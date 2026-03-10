package com.example.demo.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileOperation {

	/**
	 * 根据路径删除指定的目录或文件，无论存在与否
	 * 
	 * @param sPath 要删除的目录或文件
	 * @return 删除成功返回 true，否则返回 false。
	 */
	public static boolean DeleteFolder(String sPath) {
		Boolean flag = false;
		File file = new File(sPath);
		// 判断目录或文件是否存在
		if (!file.exists()) { // 不存在返回 false
			return flag;
		} else {
			// 判断是否为文件
			if (file.isFile()) { // 为文件时调用删除文件方法
				return deleteFile(sPath);
			} else { // 为目录时调用删除目录方法
				return deleteDirectory(sPath);
			}
		}
	}

	/**
	 * 删除单个文件
	 * 
	 * @param sPath 被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String sPath) {
		Boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	public static boolean deleteDirectory(String sPath) {

		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);

		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		Boolean flag = true;

		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} else { // 删除子目录
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;

		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @Title: writeToFile
	 * @Description: 将字符串写入文件，若目录不存在则失败，若文件存在，则删除文件后再创建文件并写入新内容
	 * @param @param str
	 * @param @param path 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public static void writeToFile(String str, String path) {
		try {
			File writename = new File(path);
			if (writename.exists())
				writename.delete();
			writename.createNewFile(); // 创建新文件
			BufferedWriter out = new BufferedWriter(new FileWriter(writename));
			out.write(str);
			out.flush(); // 把缓存区内容压入文件
			out.close(); // 最后记得关闭文件
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Title: writeToFile
	 * @Description: 将字符串写入文件，若目录不存在则创建，若文件存在，则删除文件后再创建文件并写入新内容
	 * @param @param str
	 * @param @param rootAddress
	 * @param @param path 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public static void writeToFile(String str, String rootAddress, String path) {
		File dir = new File(rootAddress);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		try {
			File writename = new File(path);
			if (writename.exists())
				writename.delete();
			writename.createNewFile(); // 创建新文件
			BufferedWriter out = new BufferedWriter(new FileWriter(writename));
			out.write(str);
			out.flush(); // 把缓存区内容压入文件
			out.close(); // 最后记得关闭文件
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 强制重命名，若newPath存在，则删除后重命名
	public static void Rename(String oldPath, String newPath) {
		File oldFile = new File(oldPath);
		File newFile = new File(newPath);
		if (newFile.exists())
			newFile.delete();
		oldFile.renameTo(newFile);
	}

	// 对文件求hash值
	public static String getFileSha1(File file) {
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			byte[] buffer = new byte[1024 * 1024 * 10];

			int len = 0;
			while ((len = in.read(buffer)) > 0) {
				digest.update(buffer, 0, len);
			}
			String sha1 = new BigInteger(1, digest.digest()).toString(16);
			int length = 40 - sha1.length();
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					sha1 = "0" + sha1;
				}
			}
			return sha1;
		} catch (IOException e) {
			System.out.println(e);
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}
		return "";
	}

	// 删除文件第一行
	public static boolean deleteQuestionMark(String address, int lineDel) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(address));
			StringBuffer sb = new StringBuffer(4096);
			String temp = null;
			int line = 0;
			while ((temp = br.readLine()) != null) {
				line++;
				if (line == lineDel)
					// temp.replaceAll("?", "");
					continue;
				if (temp == "")
					continue;
				sb.append(temp).append("\r\n ");
			}
			br.close();

			BufferedWriter bw = new BufferedWriter(new FileWriter(address));
			bw.write(sb.toString());
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	public static void fileMove(String from, String to) throws Exception {// 移动指定文件夹内的全部文件
		try {
			File dir = new File(from);
			File[] files = dir.listFiles();// 将文件或文件夹放入文件集
			if (files == null)// 判断文件集是否为空
				return;
			File moveDir = new File(to);// 创建目标目录
			if (!moveDir.exists()) {// 判断目标目录是否存在
				moveDir.mkdirs();// 不存在则创建
			}
			for (int i = 0; i < files.length; i++) {// 遍历文件集
				if (files[i].isDirectory()) {// 如果是文件夹或目录,则递归调用fileMove方法，直到获得目录下的文件
					fileMove(files[i].getPath(), to + "\\" + files[i].getName());// 递归移动文件
					files[i].delete();// 删除文件所在原目录
				}
				File moveFile = new File(moveDir.getPath() + "\\"// 将文件目录放入移动后的目录
						+ files[i].getName());
				if (moveFile.exists()) {// 目标文件夹下存在的话，删除
					moveFile.delete();
				}
				files[i].renameTo(moveFile);// 移动文件
				System.out.println(files[i] + " 移动成功");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// ========================read============================
	public static String readFromFile(String fileName) {
		System.out.println("------------------readFromFile:");
		String res = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String str;
			while ((str = in.readLine()) != null) {
				res += str + "\n";
			}
			System.out.println(res);
			in.close();
		} catch (IOException e) {
		}

		return res;
	}
	// =======================copy=============================

	// 复制目录下的文件（不包括该目录）到指定目录，会连同子目录一起复制过去。
	public static void copyFileFromDir(String toPath, String fromPath) {
		File file = new File(fromPath);
		createFile(toPath, false);// true:创建文件 false创建目录
		if (file.isDirectory()) {// 如果是目录
			copyFileToDir(toPath, listFile(file));
		}
	}

	// 复制目录到指定目录,将目录以及目录下的文件和子目录全部复制到目标目录
	public static void copyDir(String toPath, String fromPath) {
		File targetFile = new File(toPath);// 创建文件
		createFile(targetFile, false);// 创建目录
		File file = new File(fromPath);// 创建文件
		if (targetFile.isDirectory() && file.isDirectory()) {// 如果传入是目录
			copyFileToDir(targetFile.getAbsolutePath() + "/" + file.getName(), listFile(file));// 复制文件到指定目录
		}
	}

	// 复制一组文件到指定目录。targetDir是目标目录，filePath是需要复制的文件路径
	public static void copyFileToDir(String toDir, String[] filePath) {
		if (toDir == null || "".equals(toDir)) {// 目录路径为空
			System.out.println("参数错误，目标路径不能为空");
			return;
		}
		File targetFile = new File(toDir);
		if (!targetFile.exists()) {// 如果指定目录不存在
			targetFile.mkdir();// 新建目录
		} else {
			if (!targetFile.isDirectory()) {// 如果不是目录
				System.out.println("参数错误，目标路径指向的不是一个目录！");
				return;
			}
		}
		for (int i = 0; i < filePath.length; i++) {// 遍历需要复制的文件路径
			File file = new File(filePath[i]);// 创建文件
			if (file.isDirectory()) {// 判断是否是目录
				copyFileToDir(toDir + "/" + file.getName(), listFile(file));// 递归调用方法获得目录下的文件
				System.out.println("复制文件 " + file);
			} else {
				copyFileToDir(toDir, file, "");// 复制文件到指定目录
			}
		}
	}

	// 复制单个文件
	public static void copyFile(String oldName, String newName) {
		File oFile = new File(oldName);
		File tFile = new File(newName);
		copyFile(tFile, oFile);// 调用方法复制文件

	}

	/**
	 * @Title: copyFile
	 * @Description: 复制文件
	 * @param @param file
	 * @param @param newName 包含路径的文件名
	 * @return void 返回类型
	 * @throws
	 */
	public static void copyFile(File file, String newName) {
		File tFile = new File(newName);
		copyFile(tFile, file);// 调用方法复制文件
	}

	public static void copyFileToDir(String toDir, File file, String newName) {// 复制文件到指定目录
		String newFile = "";
		if (newName != null && !"".equals(newName)) {
			newFile = toDir + "/" + newName;
		} else {
			newFile = toDir + "/" + file.getName();
		}
		File tFile = new File(newFile);
		copyFile(tFile, file);// 调用方法复制文件
	}

	public static void copyFile(File toFile, File fromFile) {// 复制文件
		if (toFile.exists()) {// 判断目标目录中文件是否存在
			toFile.delete();
		}
		createFile(toFile, true);// 创建文件

		// System.out.println("复制文件" + fromFile.getAbsolutePath() + "到" +
		// toFile.getAbsolutePath());
		try {
			InputStream is = new FileInputStream(fromFile);// 创建文件输入流
			FileOutputStream fos = new FileOutputStream(toFile);// 文件输出流
			byte[] buffer = new byte[1024];// 字节数组
			int bytesRead;
			while ((bytesRead = is.read(buffer)) > 0) {// 将文件内容写到文件中
				fos.write(buffer, 0, bytesRead);
			}
			is.close();// 输入流关闭
			fos.close();// 输出流关闭

		} catch (FileNotFoundException e) {// 捕获文件不存在异常
			e.printStackTrace();
		} catch (IOException e) {// 捕获异常
			e.printStackTrace();
		}
	}

	public static String[] listFile(File dir) {// 获取文件绝对路径
		String absolutPath = dir.getAbsolutePath();// 声获字符串赋值为路传入文件的路径
		String[] paths = dir.list();// 文件名数组
		String[] files = new String[paths.length];// 声明字符串数组，长度为传入文件的个数
		for (int i = 0; i < paths.length; i++) {// 遍历显示文件绝对路径
			files[i] = absolutPath + "/" + paths[i];
		}
		return files;
	}

	public static void createFile(String path, boolean isFile) {// 创建文件或目录
		createFile(new File(path), isFile);// 调用方法创建新文件或目录
	}

	public static void createFile(File file, boolean isFile) {// 创建文件
		if (!file.exists()) {// 如果文件不存在
			if (!file.getParentFile().exists()) {// 如果文件父目录不存在
				createFile(file.getParentFile(), false);
			} else {// 存在文件父目录
				if (isFile) {// 创建文件
					try {
						file.createNewFile();// 创建新文件
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					file.mkdir();// 创建目录
				}
			}
		}
	}

	// public static void main(String[] args) {// java程序主入口处
	// String fromPath = "E:/createFile";// 目录路径
	// String toPath = "F:/createFile";// 源路径
	// System.out.println("1.移动文件：从路径 " + fromPath + " 移动到路径 " + toPath);
	// try {
	// fileMove(fromPath, toPath);// 调用方法实现文件的移动
	// } catch (Exception e) {
	// System.out.println("移动文件出现问题" + e.getMessage());
	// }
	// System.out.println("2.复制目录 " + toPath + " 下的文件（不包括该目录）到指定目录" + fromPath + "
	// ，会连同子目录一起复制过去。");
	// copyFileFromDir(fromPath, toPath);// 调用方法实现目录复制
	// System.out.println("3.复制目录 " + fromPath + "到指定目录 " + toPath + "
	// ,将目录以及目录下的文件和子目录全部复制到目标目录");
	// copyDir(toPath, fromPath);// 调用方法实现目录以用目录下的文件和子目录全部复制
	// }
}
