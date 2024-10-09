package com.bmg.mall.service.impl;

import com.bmg.mall.common.ServiceResultEnum;
import com.bmg.mall.controller.vo.bmgSearchGoodsVO;
import com.bmg.mall.dao.bmgGoodsMapper;
import com.bmg.mall.entity.bmgGoods;
import com.bmg.mall.service.bmgGoodsService;
import com.bmg.mall.util.BeanUtil;
import com.bmg.mall.util.PageQueryUtil;
import com.bmg.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class bmgGoodsServiceImpl implements bmgGoodsService {

    @Autowired
    private bmgGoodsMapper goodsMapper;

    @Override
    public PageResult getbmgGoodsPage(PageQueryUtil pageUtil) {
        List<bmgGoods> goodsList = goodsMapper.findbmgGoodsList(pageUtil);
        int total = goodsMapper.getTotalbmgGoods(pageUtil);
        PageResult pageResult = new PageResult(goodsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String savebmgGoods(bmgGoods goods) {
        if (goodsMapper.insertSelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public void batchSavebmgGoods(List<bmgGoods> bmgGoodsList) {
        if (!CollectionUtils.isEmpty(bmgGoodsList)) {
            goodsMapper.batchInsert(bmgGoodsList);
        }
    }

    @Override
    public String updatebmgGoods(bmgGoods goods) {
        bmgGoods temp = goodsMapper.selectByPrimaryKey(goods.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        goods.setUpdateTime(new Date());
        if (goodsMapper.updateByPrimaryKeySelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public bmgGoods getbmgGoodsById(Long id) {
        return goodsMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return goodsMapper.batchUpdateSellStatus(ids, sellStatus) > 0;
    }

    @Override
    public PageResult searchbmgGoods(PageQueryUtil pageUtil) {
        List<bmgGoods> goodsList = goodsMapper.findbmgGoodsListBySearch(pageUtil);
        int total = goodsMapper.getTotalbmgGoodsBySearch(pageUtil);
        List<bmgSearchGoodsVO> bmgSearchGoodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            bmgSearchGoodsVOS = BeanUtil.copyList(goodsList, bmgSearchGoodsVO.class);
            for (bmgSearchGoodsVO bmgSearchGoodsVO : bmgSearchGoodsVOS) {
                String goodsName = bmgSearchGoodsVO.getGoodsName();
                String goodsIntro = bmgSearchGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    bmgSearchGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    bmgSearchGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(bmgSearchGoodsVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
