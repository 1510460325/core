package org.cn.wzy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ResultModel {

    public static final String SUCCESS = "success";
    public static final String FAILED = "failed";
    public static final String ERROR = "error";

    private Object data;

    private String code;

    private String token;

    private int total;

}
