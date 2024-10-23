/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.api.mall.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 商品详情页VO
 */
@Data
public class NewBeeMallGoodsDetailVO implements Serializable {

    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品简介")
    private String goodsIntro;

    @ApiModelProperty("商品图片地址")
    private String goodsCoverImg;

    @ApiModelProperty("商品价格")
    private Integer sellingPrice;

    @ApiModelProperty("商品标签")
    private String tag;

    @ApiModelProperty("商品图片")
    private String[] goodsCarouselList;

    @ApiModelProperty("商品原价")
    private Integer originalPrice;

    @ApiModelProperty("商品详情字段")
    private String goodsDetailContent;

    @Override
    public String toString() {
        return "NewBeeMallGoodsDetailVO{" +
                "goodsId=" + goodsId +
                ", goodsName='" + goodsName + '\'' +
                ", goodsIntro='" + goodsIntro + '\'' +
                ", goodsCoverImg='" + goodsCoverImg + '\'' +
                ", sellingPrice=" + sellingPrice +
                ", tag='" + tag + '\'' +
                ", goodsCarouselList=" + Arrays.toString(goodsCarouselList) +
                ", originalPrice=" + originalPrice +
                ", goodsDetailContent='" + goodsDetailContent + '\'' +
                '}';
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsIntro() {
        return goodsIntro;
    }

    public void setGoodsIntro(String goodsIntro) {
        this.goodsIntro = goodsIntro;
    }

    public String getGoodsCoverImg() {
        return goodsCoverImg;
    }

    public void setGoodsCoverImg(String goodsCoverImg) {
        this.goodsCoverImg = goodsCoverImg;
    }

    public Integer getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Integer sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String[] getGoodsCarouselList() {
        return goodsCarouselList;
    }

    public void setGoodsCarouselList(String[] goodsCarouselList) {
        this.goodsCarouselList = goodsCarouselList;
    }

    public Integer getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Integer originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getGoodsDetailContent() {
        return goodsDetailContent;
    }

    public void setGoodsDetailContent(String goodsDetailContent) {
        this.goodsDetailContent = goodsDetailContent;
    }
}
