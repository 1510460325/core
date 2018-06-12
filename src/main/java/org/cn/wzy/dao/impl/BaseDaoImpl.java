package org.cn.wzy.dao.impl;

import lombok.extern.log4j.Log4j;
import org.apache.ibatis.session.SqlSession;
import org.cn.wzy.dao.BaseDao;
import org.cn.wzy.query.BaseQuery;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import java.util.Date;
import java.util.List;

/**
 * @author wzy
 * @Date 2018/4/7 15:55
 */
@Log4j
public abstract class BaseDaoImpl<Q> extends SqlSessionDaoSupport implements BaseDao<Q> {


    @Override
    public int deleteByPrimaryKey(Integer id) {
        if (id == null)
            return -1;
        return this.getSqlSession()
                .delete(getNameSpace() + ".deleteByPrimaryKey", id);
    }

    @Override
    public int insert(Q record) {
        if (record == null)
            return -1;
        return this.getSqlSession()
                .insert(getNameSpace() + ".insert",record);
    }

    @Override
    public int insertSelective(Q record) {
        if (record == null)
            return -1;
        try {
            return this.getSqlSession()
                    .insert(getNameSpace() + ".insertSelective",record);
        } catch (Exception e) {
            log.error(new Date()+ "--insertSelective--param:" + record + " failed");
        }
        return -1;
    }

    @Override
    public Q selectByPrimaryKey(Integer id) {
        if (id == null)
            return null;
        return this.getSqlSession().selectOne(getNameSpace() + ".selectByPrimaryKey",id);
    }

    @Override
    public int updateByPrimaryKeySelective(Q record) {
        if (record == null)
            return -1;
        try {
            return this.getSqlSession()
                    .update(getNameSpace() + ".updateByPrimaryKeySelective",record);
        } catch (Exception e) {
            log.error(new Date()+ "--updateByPrimaryKeySelective--param:" + record + " failed");
        }
        return -1;
    }

    @Override
    public int updateByPrimaryKeyWithBLOBs(Q record) {
        if (record == null)
            return -1;
        return this.getSqlSession()
                .update(getNameSpace() + ".updateByPrimaryKeyWithBLOBs",record);
    }

    @Override
    public int updateByPrimaryKey(Q record) {
        if (record == null)
            return -1;
        return this.getSqlSession().update(getNameSpace() + ".updateByPrimaryKey",record);
    }

    @Override
    public List<Q> selectByCondition(BaseQuery<Q> record) {
        if (record == null)
            return null;
        try {
            return this.getSqlSession()
                    .selectList(getNameSpace() + ".selectByCondition",record);
        } catch (Exception e) {
            log.error(new Date()+ "--selectByCondition--param:" + record + " failed");
        }
        return null;
    }

    @Override
    public Integer selectCountByCondition(BaseQuery<Q> record) {
        if (record == null)
            return -1;
        try {
            return this.getSqlSession()
                    .selectOne(getNameSpace() + ".selectCountByCondition",record);
        } catch (Exception e) {
            log.error(new Date()+ "--selectCountByCondition--param:" + record + " failed");
        }
        return -1;
    }

    @Override
    public int insertList(List<Q> list) {
        if (list == null)
            return -1;
        SqlSession nowSession = this.getSqlSession();
        try {
            return nowSession
                    .insert(getNameSpace() + ".insertList",list);
        } catch (Exception e) {
            log.error(new Date()+ "--insertList--param:" + list + " failed");
            nowSession.rollback();
        }
        return -1;
    }

    @Override
    public int deleteByIdsList(List<Integer> ids) {
        if (ids == null)
            return -1;
        SqlSession nowSession = this.getSqlSession();
        try {
            return this.getSqlSession()
                    .delete(getNameSpace() + ".deleteByIdsList",ids);
        } catch (Exception e) {
            log.error(new Date()+ "--insertList--param:" + ids + " failed");
            nowSession.rollback();
        }
        return -1;
    }

    @Override
    public List<Q> selectByIds(List<Integer> ids) {
        if (ids == null)
            return null;
        try {
            return this.getSqlSession()
                    .selectList(getNameSpace() + ".selectByIds",ids);
        } catch (Exception e) {
            log.error(new Date()+ "--selectByIds--param:" + ids + " failed");
        }
        return null;
    }
}
