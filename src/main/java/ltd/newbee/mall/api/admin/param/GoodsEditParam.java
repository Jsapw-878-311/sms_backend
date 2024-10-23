/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.api.admin.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class GoodsEditParam {

    @ApiModelProperty("待修改商品id")
    @NotNull(message = "商品id不能为空")
    @Min(value = 1, message = "商品id不能为空")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    @NotEmpty(message = "商品名称不能为空")
    @Length(max = 128,message = "商品名称内容过长")
    private String goodsName;

    @ApiModelProperty("商品简介")
    @NotEmpty(message = "商品简介不能为空")
    @Length(max = 200,message = "商品简介内容过长")
    private String goodsIntro;

    @ApiModelProperty("分类id")
    @NotNull(message = "分类id不能为空")
    @Min(value = 1, message = "分类id最低为1")
    private Long goodsCategoryId;

    @ApiModelProperty("商品主图")
    @NotEmpty(message = "商品主图不能为空")
    private String goodsCoverImg;

    @ApiModelProperty("originalPrice")
    @NotNull(message = "originalPrice不能为空")
    @Min(value = 1, message = "originalPrice最低为1")
    @Max(value = 1000000, message = "originalPrice最高为1000000")
    private Integer originalPrice;

    @ApiModelProperty("sellingPrice")
    @NotNull(message = "sellingPrice不能为空")
    @Min(value = 1, message = "sellingPrice最低为1")
    @Max(value = 1000000, message = "sellingPrice最高为1000000")
    private Integer sellingPrice;

    @ApiModelProperty("库存")
    @NotNull(message = "库存不能为空")
    @Min(value = 1, message = "库存最低为1")
    @Max(value = 100000, message = "库存最高为100000")
    private Integer stockNum;

    @ApiModelProperty("商品标签")
    @NotEmpty(message = "商品标签不能为空")
    @Length(max = 16,message = "商品标签内容过长")
    private String tag;

    private Byte goodsSellStatus;

    @ApiModelProperty("商品详情")
    @NotEmpty(message = "商品详情不能为空")
    private String goodsDetailContent;

    @Override
    public String toString() {
        return "GoodsEditParam{" +
                "goodsId=" + goodsId +
                ", goodsName='" + goodsName + '\'' +
                ", goodsIntro='" + goodsIntro + '\'' +
                ", goodsCategoryId=" + goodsCategoryId +
                ", goodsCoverImg='" + goodsCoverImg + '\'' +
                ", originalPrice=" + originalPrice +
                ", sellingPrice=" + sellingPrice +
                ", stockNum=" + stockNum +
                ", tag='" + tag + '\'' +
                ", goodsSellStatus=" + goodsSellStatus +
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

    public Long getGoodsCategoryId() {
        return goodsCategoryId;
    }

    public void setGoodsCategoryId(Long goodsCategoryId) {
        this.goodsCategoryId = goodsCategoryId;
    }

    public String getGoodsCoverImg() {
        return goodsCoverImg;
    }

    public void setGoodsCoverImg(String goodsCoverImg) {
        this.goodsCoverImg = goodsCoverImg;
    }

    public Integer getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Integer originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Integer getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Integer sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Integer getStockNum() {
        return stockNum;
    }

    public void setStockNum(Integer stockNum) {
        this.stockNum = stockNum;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Byte getGoodsSellStatus() {
        return goodsSellStatus;
    }

    public void setGoodsSellStatus(Byte goodsSellStatus) {
        this.goodsSellStatus = goodsSellStatus;
    }

    public String getGoodsDetailContent() {
        return goodsDetailContent;
    }

    public void setGoodsDetailContent(String goodsDetailContent) {
        this.goodsDetailContent = goodsDetailContent;
    }
}