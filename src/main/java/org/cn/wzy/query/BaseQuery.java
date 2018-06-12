package org.cn.wzy.query;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author wzy
 * @Date 2018/4/7 13:14
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class BaseQuery<Q> {
    private Integer start;
    private Integer rows;
    private Q query;
    public BaseQuery(Class clazz) {
        try {
            this.query = (Q)clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
