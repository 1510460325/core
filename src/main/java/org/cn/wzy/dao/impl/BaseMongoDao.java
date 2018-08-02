package org.cn.wzy.dao.impl;


import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import lombok.extern.log4j.Log4j;
import org.bson.Document;
import org.cn.wzy.annotation.MGColName;
import org.cn.wzy.query.BaseQuery;
import org.cn.wzy.util.MapUtil;
import org.cn.wzy.util.PropertiesUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Create by Wzy
 * on 2018/7/28 18:15
 * 不短不长八字刚好
 */
@Log4j
public class BaseMongoDao {

    private static final MongoClient mongoClient;

    private static final MongoDatabase mongo;

    static {
        MongoClientOptions options = MongoClientOptions.builder()
                .connectionsPerHost(150)
                .maxWaitTime(2000)
                .socketTimeout(10000)
                .maxConnectionLifeTime(20000)
                .connectTimeout(5000).build();
        ServerAddress serverAddress = new ServerAddress(PropertiesUtil.StringValue("mongo.host"),
                PropertiesUtil.IntegerValue("mongo.port"));
        List<ServerAddress> addrs = new ArrayList<>();
        addrs.add(serverAddress);
        MongoCredential credential = MongoCredential.createScramSha1Credential(
                PropertiesUtil.StringValue("mongo.user")
                , "admin"
                , PropertiesUtil.StringValue("mongo.pwd").toCharArray());
        mongoClient = new MongoClient(addrs, credential, options);
        mongo = mongoClient.getDatabase(PropertiesUtil.StringValue("mongo.db"));
    }

    private String collection;

    private MongoCollection<Document> thisCollection() {
        return mongo.getCollection(collection);
    }

    public <Q> List<Q> queryByCondition(BaseQuery<Q> query, String sortName, boolean up, Map<String, Object>... additional) {
        Q record = query.getQuery();
        try {
            changeCollection(record.getClass());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.info("queryByCondition方法报错:param" + query + "," + sortName + "," + up  + "," + additional);
            return null;
        }
        BasicDBObject cond = new BasicDBObject(MapUtil.parseEntity(record));
        if (additional != null && additional.length > 0) {
            cond.putAll(additional[0]);
        }
        BasicDBObject sort = null;
        if (sortName != null && !sortName.trim().equals("")) {
            sort = new BasicDBObject(sortName, up ? 1 : -1);
        }
        FindIterable<Document> findIterable;
        if (query.getStart() != null && query.getRows() != null)
            findIterable = thisCollection().find(cond)
                    .sort(sort)
                    .skip((query.getStart() - 1) * query.getRows())
                    .limit(query.getRows());
        else
            findIterable = thisCollection().find(cond);
        MongoCursor<Document> iterator = findIterable.iterator();
        List<Q> result = new ArrayList<>();
        while (iterator.hasNext()) {
            Document document = iterator.next();
            result.add((Q) MapUtil.castToEntity(document, record.getClass()));
        }
        iterator.close();
        return result;
    }

    public <Q> Integer queryCoditionCount(BaseQuery<Q> query, Map<String, Object>... additional) {
        Q record = query.getQuery();
        try {
            changeCollection(record.getClass());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.info("queryCoditionCount报错:param" + query + "," + additional);
            return null;
        }
        BasicDBObject cond = new BasicDBObject(MapUtil.parseEntity(record));
        if (additional != null && additional.length > 0) {
            cond.putAll(additional[0]);
        }
        return (int) thisCollection().countDocuments(cond);
    }

    public <Q> boolean insertOne(Q record) {
        BasicDBObject cond = new BasicDBObject(MapUtil.parseEntity(record));
        try {
            changeCollection(record.getClass());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.info("queryCoditionCount报错:param" + record);
            return false;
        }
        try {
            thisCollection().insertOne(new Document(cond));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("queryCoditionCount报错:param" + record);
            return false;
        }
    }

    public <Q> boolean insertList(List<Q> records) {
        try {
            changeCollection(records.get(0).getClass());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.info("insertList报错:param" + records);
            return false;
        }
        try {
            List<Document> list = new ArrayList<>(records.size());
            for (Q record : records) {
                list.add(new Document(MapUtil.parseEntity(record)));
            }
            thisCollection().insertMany(list);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("insertList报错:param" + records);
            return false;
        }
    }

    public <Q> boolean delete(Q record, boolean onlyOne) {
        try {
            changeCollection(record.getClass());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.info("delete报错:param" + record + "," + onlyOne);
            return false;
        }
        try {
            if (onlyOne)
                thisCollection().deleteOne(new BasicDBObject(MapUtil.parseEntity(record)));
            else
                thisCollection().deleteMany(new BasicDBObject(MapUtil.parseEntity(record)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("delete报错:param" + record + "," + onlyOne);
            return false;
        }
    }

    public <Q> boolean deleteList(List<Q> list, String field, Class<?> clazz) {
        try {
            changeCollection(clazz);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.info("deleteList报错:param" + list + "," + field + "," + clazz);
            return false;
        }
        BasicDBObject cond = new BasicDBObject(field, new BasicDBObject("$in", list.toArray()));
        try {
            thisCollection().deleteMany(cond);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("deleteList报错:param" + list + "," + field + "," + clazz);
            return false;
        }
    }

    public <Q> boolean updateByFeild(Q record, String field) {
        try {
            changeCollection(record.getClass());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.info("updateByFeild报错:param" + record + "," + field);
            return false;
        }
        Map<String, Object> cond = MapUtil.parseEntity(record);
        BasicDBObject update = new BasicDBObject("$set", cond);
        BasicDBObject query = new BasicDBObject(field, cond.get(field));
        thisCollection().updateOne(query, update);
        return true;
    }


    private void changeCollection(Class clazz) throws Throwable {
        MGColName mgColName = (MGColName) clazz.getAnnotation(MGColName.class);
        if (mgColName == null) {
            throw new Throwable("The entity must map a mongodb collection");
        }
        Method colName = mgColName.annotationType().getDeclaredMethod("value");
        String name = (String) colName.invoke(mgColName);
        if (name.trim().equals("")) {
            throw new Throwable("mongodb collection must not be null");
        }
        this.collection = name;
    }
}
