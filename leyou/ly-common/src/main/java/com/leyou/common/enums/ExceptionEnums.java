package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ExceptionEnums {
    PRICE_CANNOT_BE_NULL("价格不能为空", 400),
    CATEGORY_NOT_FIND("商品分类列表没有发现",404),
    BRAND_NOT_FIND("商品品牌没有被发现",404),
    SAVE_BRAND_FAILE("商品品牌新增失败",404),
    DELETE_BRAND_FAILE("商品品牌删除失败",500),
    UPDATE_BRAND_FAILE("商品品牌更新失败",500),
    FILE_TYPE_NOT_PITCH("上传文件类型不匹配",500),
    FILE_CONTENT_NOT_PITCH("上传文件内容不匹配",500),
    UPLOAD_FILE_ERROR("上传文件失败",500),
    SPEC_GROUP_NOT_FIND("规格组未发现",404),
    ADD_SPEC_GROUP_FILE("新增规格组失败",500),
    UPDATE_SPECCGROUP_FILE("更新规格组失败",500),
    GOODS_SAVE_ERROR("新增商品失败",500),
    SAVE_SKU_ERROR("新增商品SKU失败",500),
    UPDAE_SPU_ERROR("更新商品spu失败",500),
    UPDAE_SPUDETAIL_ERROR("更新商品spudetail失败",500),
    UPDATE_SKU_FAILE("新增商品SKU失败",500),
    DELETE_STOCK_FAILE("删除商品STOCK失败",500),
    DELE_SPU_ERROR("删除商品失败",500),
    SAVE_SPUDETAIL_ERROR("新增商品SPUDETAIL失败",500),
    SPUDETAIL_NOT_FIND("SPUDETAIL未发现",500),
    SAVE_STOCK_ERROR("新增商品STOCK失败",500),
    SPECPARAMS_NOT_FIND("规格参数未被发现",500),
    GOODS_NOT_FIND("商品列表未发现",404),
    SPU_NOT_FIND("商品SPU未发现",500),
    INVALID_PARAM("无价值的key",500),
    ;
    private String msg;
    private int code;
}
