package org.cn.wzy.util;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Setter
public class MapperingGenerator {
    public static String oldPath;
    public static String implPath;
    public static String sql;

    public static void run() {
        File oldPathFile = new File(oldPath);
        File[] files = oldPathFile.listFiles();
        int count = 0;
        for (File file : files) {
            if (file.isDirectory()) {
                continue;
            }
            if (!judge(implPath + "/" + file.getName())) {
                continue;
            }
            Element rootElement = readXml(file.getPath());
            Model model = getModel(rootElement);
            System.out.println(JSON.toJSONString(model));
            writeImpl(model, file.getName());

            if (!judge(sql + "/" + file.getName())) {
                continue;
            }
            writeCondition(model, file.getName());
        }
    }

    public static void writeImpl(Model model, String filename) {
        File file = new File(implPath + "/" + filename);
        PrintWriter print = null;
        try {
            print = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String resultMap = model.isBlob() ? "ResultMapWithBLOBs" : "BaseResultMap";
        print.printf("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n" +
                "<mapper namespace=\"%s\" > \n", model.getNamespace());
        //生成selectBYCondition语句
        print.printf("  <select id=\"selectByCondition\" parameterType=\"org.cn.wzy.query.BaseQuery\" resultMap=\"%s\"> \n", resultMap);
        print.printf("      SELECT\n");
        print.printf("      <include refid=\"Base_Column_List\"/>\n");
        if (model.isBlob()) {
            print.printf("      ,\n");
            print.printf("      <include refid=\"Blob_Column_List\"/>\n");
        }
        print.printf("      FROM %s\n", model.getTableName());
        print.printf("      <include refid=\"condition\"/>\n");
        print.printf("      <include refid=\"order\" />\n");
        print.printf("      <include refid=\"limit\" />\n");
        print.printf("  </select>\n");
        //生成selectCountByCondition语句
        print.printf("  <select id=\"selectCountByCondition\" parameterType=\"org.cn.wzy.query.BaseQuery\" resultType=\"java.lang.Integer\"> \n");
        print.printf("      SELECT\n");
        print.printf("      COUNT(*)\n");
        print.printf("      FROM %s\n", model.getTableName());
        print.printf("      <include refid=\"condition\"/>\n");
        print.printf("  </select>\n");
        //插入一个List<Q>集合
        print.printf("  <insert id=\"insertList\" parameterType=\"java.util.List\" useGeneratedKeys=\"false\">\n");
        print.printf("      <foreach collection=\"list\" item=\"item\" index=\"index\" separator=\";\" >\n");
        print.printf("      INSERT into %s\n", model.getTableName());
        printColumns(print, model);
        print.printf("      VALUES\n");
        printValues(print, model);
        print.printf("      </foreach>\n");
        print.printf("  </insert>\n");
        //删除一个ids集合
        print.printf("  <delete id=\"deleteByIdsList\" parameterType=\"java.util.List\">\n");
        print.printf("      DELETE FROM %s\n", model.getTableName());
        print.printf("      WHERE %s IN\n", model.getIdColumn());
        print.printf("      <foreach collection=\"list\" item=\"item\" index=\"index\" open=\"(\" close=\")\" separator=\",\">\n");
        print.printf("          #{item}\n");
        print.printf("      </foreach>\n");
        print.printf("  </delete>\n");
        //生成selectByIds语句
        print.printf("  <select id=\"selectByIds\" parameterType=\"java.util.List\" resultMap=\"%s\"> \n", resultMap);
        print.printf("      SELECT\n");
        print.printf("      <include refid=\"Base_Column_List\"/>\n");
        if (model.isBlob()) {
            print.printf("      ,\n");
            print.printf("      <include refid=\"Blob_Column_List\"/>\n");
        }
        print.printf("      FROM %s\n", model.getTableName());
        print.printf("      WHERE %s In\n", model.getIdColumn());
        print.printf("      <foreach collection=\"list\" item=\"item\" index=\"index\" open=\"(\" close=\")\" separator=\",\">\n");
        print.printf("          #{item}\n");
        print.printf("      </foreach>\n");
        print.printf("  </select>\n");
        print.printf("</mapper>");
        print.flush();
        print.close();
    }

    public static void printColumns(PrintWriter print, Model model) {
        print.printf("      (");
        for (int i = 0; i < model.getFieldList().size(); ++i) {
            print.printf("%s", model.getFieldList().get(i).getColumn());
            if (i == model.getFieldList().size() - 1)
                break;
            print.printf(",");
        }
        if (model.isBlob()) {
            print.printf(",");
            for (int i = 0; i < model.getBlobFieldList().size(); ++i) {
                print.printf("%s", model.getBlobFieldList().get(i).getColumn());
                if (i == model.getBlobFieldList().size() - 1)
                    break;
                print.printf(",");
            }
        }
        print.printf(")\n");
    }

    public static void printValues(PrintWriter print, Model model) {
        print.printf("      (");
        for (int i = 0; i < model.getFieldList().size(); ++i) {
            print.printf("#{item.%s,jdbcType=%s}", model.getFieldList().get(i).getProperty(), model.getFieldList().get(i).getJdbcType());
            if (i == model.getFieldList().size() - 1)
                break;
            print.printf(",");
        }
        if (model.isBlob()) {
            print.printf(",");
            for (int i = 0; i < model.getBlobFieldList().size(); ++i) {
                print.printf("#{item.%s,jdbcType=%s}", model.getBlobFieldList().get(i).getProperty(), model.getBlobFieldList().get(i).getJdbcType());
                if (i == model.getBlobFieldList().size() - 1)
                    break;
                print.printf(",");
            }
        }
        print.printf(")\n");
    }

    public static void writeCondition(Model model, String fileName) {
        File file = new File(sql + "/" + fileName);
        PrintWriter print = null;
        try {
            print = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        print.printf("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n" +
                "<mapper namespace=\"%s\" > \n", model.getNamespace());
        print.printf("  <sql id=\"condition\" >\n");
        printFields(print, model);
        print.printf("  </sql>\n");
        print.printf("  <sql id=\"order\" >\n\n");
        print.printf("  </sql>\n");

        print.printf("  <sql id=\"limit\" >\n");
        print.printf("      <if test=\"start != null and rows != null\" >\n");
        print.printf("          LIMIT #{start}, #{rows}\n");
        print.printf("      </if>\n");
        print.printf("  </sql>\n");
        print.printf("</mapper>");
        print.flush();
        print.close();
    }

    private static void printFields(PrintWriter print, Model model) {
        print.printf("      WHERE 1 = 1 \n");
        for (Field field : model.getFieldList()) {
            print.printf("      <if test=\"query.%s != null\" >\n", field.getProperty());
            print.printf("          AND %s = #{query.%s,jdbcType=%s}\n", field.getColumn(), field.getProperty(), field.getJdbcType());
            print.printf("      </if>\n");
        }
        if (model.isBlob()) {
            for (Field field : model.getBlobFieldList()) {
                print.printf("      <if test=\"query.%s != null\" >\n", field.getProperty());
                print.printf("          AND %s = #{query.%s,jdbcType=%s}\n", field.getColumn(), field.getProperty(), field.getJdbcType());
                print.printf("      </if>\n");
            }
        }
        print.printf("      <if test=\"query.%s != null\" >\n", model.getIdProperty());
        print.printf("          AND %s = #{query.%s,jdbcType=%s}\n", model.getIdColumn(), model.getIdProperty(), model.getIdJdbcType());
        print.printf("      </if>\n");
    }

    private static boolean judge(String path) {
        File newFile = new File(path);
        if (newFile.exists()) {
            return false;
        } else {
            try {
                newFile.createNewFile();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Element readXml(String filePath) {
        InputStream in = null;
        Element rootElement = null;
        try {
            SAXReader reader = new SAXReader();
            in = new FileInputStream(new File(filePath));
            Document doc = reader.read(in);
            rootElement = doc.getRootElement();
            System.out.println("XMLUtil.readXml root name:" + rootElement.getName());
        } catch (Exception e) {
            System.err.println("XMLUtil.readXml error: " + e);
            return null;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rootElement;
    }

    public static Model getModel(Element rootElement) {
        Model model = new Model();
        model.setNamespace(rootElement.attributeValue("namespace"));
        List<Element> resultMapList = rootElement.elements("resultMap");
        Element firltMap = resultMapList.get(0);
        // 设置type
        model.setType(firltMap.attributeValue("type"));
        // 设置主键
        Element id = firltMap.element("id");
        model.setIdColumn(id.attributeValue("column"));
        model.setIdProperty(id.attributeValue("property"));
        model.setIdJdbcType(id.attributeValue("jdbcType"));
        // 设置字段
        model.setFieldList(getField(firltMap));
        if (resultMapList.size() != 1) {
            model.setBlob(true);
            model.setBlobFieldList(getField(resultMapList.get(1)));
        }
        Element delete = rootElement.element("delete");
        String text = delete.getText().replaceAll("\n", "");
        String result = text.split("\\s+")[3];
        model.setTableName(result);
        return model;
    }

    public static List<Field> getField(Element rootElement) {
        List<Field> fieldList = new ArrayList<>();
        List<Element> elementList = rootElement.elements("result");
        for (Element element : elementList) {
            Field field = new Field();
            field.setColumn(element.attributeValue("column"));
            field.setProperty(element.attributeValue("property"));
            field.setJdbcType(element.attributeValue("jdbcType"));
            fieldList.add(field);
        }
        return fieldList;
    }

    @Setter
    @Getter
    private static class Field {
        private String column;
        private String property;
        private String jdbcType;
    }

    @Getter
    @Setter
    private static class Model {
        private String namespace;
        private String tableName;
        private String type;
        private String idColumn;
        private String idProperty;
        private String idJdbcType;
        private List<Field> fieldList = new ArrayList<>();
        private boolean isBlob = false;
        private List<Field> blobFieldList = new ArrayList<>();
    }
}
