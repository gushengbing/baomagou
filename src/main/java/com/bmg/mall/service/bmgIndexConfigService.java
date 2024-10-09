package com.bmg.mall.service;

import com.bmg.mall.controller.vo.bmgIndexConfigGoodsVO;
import com.bmg.mall.entity.IndexConfig;
import com.bmg.mall.util.PageQueryUtil;
import com.bmg.mall.util.PageResult;

import java.util.List;

public interface bmgIndexConfigService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getConfigsPage(PageQueryUtil pageUtil);

    String saveIndexConfig(IndexConfig indexConfig);

    String updateIndexConfig(IndexConfig indexConfig);

    IndexConfig getIndexConfigById(Long id);

    /**
     * 返回固定数量的首页配置商品对象(首页调用)
     *
     * @param number
     * @return
     */
    List<bmgIndexConfigGoodsVO> getConfigGoodsesForIndex(int configType, int number);

    Boolean deleteBatch(Long[] ids);
}
