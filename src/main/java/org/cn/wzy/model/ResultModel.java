package org.cn.wzy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

/**
 * Created by Wzy
 * on 2018/5/8
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ResultModel {

    public static final String SUCCESS = "success";
    public static final String FAILED = "failed";

    private Object data;

    private String code;

    private String token;

    private int total;

}
