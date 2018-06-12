package org.cn.wzy.dao;


import org.cn.wzy.query.BaseQuery;

import java.util.List;

/**
 * @author wzy
 * @Date 2018/4/7 15:49
 */
public interface BaseDao<Q> {
    String getNameSpace();

    int deleteByPrimaryKey(Integer id);

    int insert(Q record);

    int insertSelective(Q record);

    Q selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Q record);

    int updateByPrimaryKeyWithBLOBs(Q record);

    int updateByPrimaryKey(Q record);

    List<Q> selectByCondition(BaseQuery<Q> record);

    Integer selectCountByCondition(BaseQuery<Q> record);

    int insertList(List<Q> list);

    int deleteByIdsList(List<Integer> ids);

    List<Q> selectByIds(List<Integer> ids);
}
