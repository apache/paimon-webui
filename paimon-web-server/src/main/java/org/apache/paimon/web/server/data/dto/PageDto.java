package org.apache.paimon.web.server.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PageDto<T> implements Serializable {
    private Integer currentPage=1;
    private Integer pageSize=10;
    private T params;
}
