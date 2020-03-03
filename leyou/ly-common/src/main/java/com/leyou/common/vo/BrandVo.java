package com.leyou.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandVo {
    private Long id;
    private String name;
    private String image;
    private List<Long> cids;
    private Character letter;
}
