package org.cn.wzy.util;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Create by Wzy
 * on 2018/7/16 15:08
 * 不短不长八字刚好
 */
public class DaoAndEntityGenerator {
    public static String entityPath;
    public static String packagePath;
    public static String packageName;
    public static void entityGenerator() throws IOException {
        File file = new File(entityPath);
        if (!file.isDirectory()) {
            System.out.println(file + " is not a directory.");
            return;
        }
        File dist = new File(packagePath + "\\entity");
        if (!dist.isDirectory()) {
            System.out.println(dist + " is not a directory.");
            return;
        }
        File[] entities = file.listFiles();
        for (File f : entities) {
            String name = f.getName();
            System.out.println("generate the " + name);
            File tem = new File(dist + "\\" + name);
            if (tem.exists()) {
                System.out.println(tem.getName() + " is exists. skipping...");
                continue;
            }
            tem.createNewFile();
            print(f,tem);
        }
    }

    private static void print(File source, File dist) throws FileNotFoundException {
        Scanner scanner = new Scanner(source);
        PrintWriter printWriter = new PrintWriter(dist);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.length() >= 7 && line.indexOf("public") != -1) {
                if (line.indexOf("public class") != -1) {
                    printWriter.print("import lombok.*;\n" +
                            "import lombok.experimental.Accessors;\n\n" +
                             "/**\n" +
                             " * Create by WzyGenerator\n" +
                             " * on " + new Date() + "\n" + " * 不短不长八字刚好\n" +
                             " */\n\n" +
                            "@Data\n" +
                            "@AllArgsConstructor\n" +
                            "@NoArgsConstructor\n" +
                            "@Accessors(chain = true)\n");
                }
                else {
                    System.out.println("........\ngenerate the " + dist.getName() + " is completed.\n\n\n");
                    printWriter.print("}");
                    break;
                }
            }
            printWriter.print(line + "\n");
        }
        printWriter.close();
        scanner.close();
    }

    public static void daoGenerator() throws IOException {
        File dao = new File(packagePath + "\\dao");
        if (!dao.exists()) {
            System.out.println("mkdir " + dao.getName());
            dao.mkdir();
        }
        File impl = new File(dao + "\\impl");
        if (!impl.exists()) {
            System.out.println("mkdir " + impl.getName());
            impl.mkdir();
        }
        File file = new File(packagePath+ "\\entity");
        if (!file.exists() || file.listFiles() == null || file.listFiles().length == 0) {
            System.out.println(file + " is null or empty!");
            return;
        }
        File[] files = file.listFiles();
        File one = files[0];
        Scanner scanner = new Scanner(one);
        packageName = scanner.nextLine().replaceAll("entity","dao");
        for (File f : files) {
            String daoName = f.getName().replaceAll(".java","Dao.java");
            System.out.println("generate the " + daoName);
            File tem = new File(dao + "\\" + daoName);
            if (tem.exists()) {
                System.out.println(tem + " is exists, skipping...");
                continue;
            }
            tem.createNewFile();

            String implName = f.getName().replaceAll(".java","DaoImpl.java");
            System.out.println("generate the " + implName);
            File impltem = new File(impl + "\\" + implName);
            if (impltem.exists()) {
                System.out.println(impltem + " is exists, skipping...");
                continue;
            }
            impltem.createNewFile();

            printDao(tem,packageName.replaceAll(".dao;",".entity"),f.getName().replaceAll(".java",""));
            printImpl(impltem,packageName.replaceAll(".dao;","."),f.getName().replaceAll(".java",""));
        }
    }
    private static void printImpl(File file,String packagePreName, String entity) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(file);
        printWriter.print(packageName.replaceAll("dao","dao.impl") + "\n");
        printWriter.print(packagePreName.replaceAll("package","import") + "entity." + entity + ";\n");
        printWriter.print(packagePreName.replaceAll("package","import") + "dao." + entity + "Dao;\n");
        printWriter.print("");
        printWriter.print("import org.cn.wzy.dao.impl.BaseDaoImpl;\n" +
                "import org.springframework.stereotype.Repository;\n");
        printWriter.print("/**\n" +
                " * Create by WzyGenerator\n" +
                " * on " + new Date() + "\n" +
                " * 不短不长八字刚好\n" +
                " */\n\n");
        printWriter.print("@Repository\n" +
                "public class " + entity +
                "DaoImpl" +
                " extends BaseDaoImpl<" + entity + "> " +
                "implements " + entity +  "Dao" +
                " {\n");
        printWriter.print("    @Override\n" +
                "    public String getNameSpace() {\n" +
                "        return " + "\"" +
                packagePreName.replaceAll("package ","") + "dao." + entity + "Mapper" + "\"" +
                ";\n" +
                "    }\n}");
        printWriter.close();
    }
    private static void printDao(File file,String packagePreName, String entity) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(file);
        printWriter.print(packageName + "\n");
        printWriter.print(packagePreName.replaceAll("package","import") + "." + entity + ";\n");
        printWriter.print("import org.cn.wzy.dao.BaseDao;\n");
        printWriter.print("/**\n" +
                " * Create by WzyGenerator\n" +
                " * on " + new Date() + "\n" +
                " * 不短不长八字刚好\n" +
                " */\n\n");
        printWriter.print("public interface " + entity +
                "Dao" +
                " extends BaseDao<" +
                entity +
                "> {\n}");
        printWriter.close();
    }

}
