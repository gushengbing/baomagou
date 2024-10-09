package com.bmg.mall.service;

import com.bmg.mall.entity.bmgGoods;
import com.bmg.mall.util.PageQueryUtil;
import com.bmg.mall.util.PageResult;

import java.util.List;

public interface bmgGoodsService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getbmgGoodsPage(PageQueryUtil pageUtil);

    /**
     * 添加商品
     *
     * @param goods
     * @return
     */
    String savebmgGoods(bmgGoods goods);

    /**
     * 批量新增商品数据
     *
     * @param bmgGoodsList
     * @return
     */
    void batchSavebmgGoods(List<bmgGoods> bmgGoodsList);

    /**
     * 修改商品信息
     *
     * @param goods
     * @return
     */
    String updatebmgGoods(bmgGoods goods);

    /**
     * 获取商品详情
     *
     * @param id
     * @return
     */
    bmgGoods getbmgGoodsById(Long id);

    /**
     * 批量修改销售状态(上架下架)
     *
     * @param ids
     * @return
     */
    Boolean batchUpdateSellStatus(Long[] ids,int sellStatus);

    /**
     * 商品搜索
     *
     * @param pageUtil
     * @return
     */
    PageResult searchbmgGoods(PageQueryUtil pageUtil);
}
