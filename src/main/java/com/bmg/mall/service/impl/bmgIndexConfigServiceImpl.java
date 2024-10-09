package com.bmg.mall.service.impl;

import com.bmg.mall.common.ServiceResultEnum;
import com.bmg.mall.controller.vo.bmgIndexConfigGoodsVO;
import com.bmg.mall.dao.IndexConfigMapper;
import com.bmg.mall.dao.bmgGoodsMapper;
import com.bmg.mall.entity.IndexConfig;
import com.bmg.mall.entity.bmgGoods;
import com.bmg.mall.service.bmgIndexConfigService;
import com.bmg.mall.util.BeanUtil;
import com.bmg.mall.util.PageQueryUtil;
import com.bmg.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class bmgIndexConfigServiceImpl implements bmgIndexConfigService {

    @Autowired
    private IndexConfigMapper indexConfigMapper;

    @Autowired
    private bmgGoodsMapper goodsMapper;

    @Override
    public PageResult getConfigsPage(PageQueryUtil pageUtil) {
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigList(pageUtil);
        int total = indexConfigMapper.getTotalIndexConfigs(pageUtil);
        PageResult pageResult = new PageResult(indexConfigs, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveIndexConfig(IndexConfig indexConfig) {
        //todo 判断是否存在该商品
        if (indexConfigMapper.insertSelective(indexConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateIndexConfig(IndexConfig indexConfig) {
        //todo 判断是否存在该商品
        IndexConfig temp = indexConfigMapper.selectByPrimaryKey(indexConfig.getConfigId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        if (indexConfigMapper.updateByPrimaryKeySelective(indexConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public IndexConfig getIndexConfigById(Long id) {
        return null;
    }

    @Override
    public List<bmgIndexConfigGoodsVO> getConfigGoodsesForIndex(int configType, int number) {
        List<bmgIndexConfigGoodsVO> bmgIndexConfigGoodsVOS = new ArrayList<>(number);
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigsByTypeAndNum(configType, number);
        if (!CollectionUtils.isEmpty(indexConfigs)) {
            //取出所有的goodsId
            List<Long> goodsIds = indexConfigs.stream().map(IndexConfig::getGoodsId).collect(Collectors.toList());
            List<bmgGoods> bmgGoods = goodsMapper.selectByPrimaryKeys(goodsIds);
            bmgIndexConfigGoodsVOS = BeanUtil.copyList(bmgGoods, bmgIndexConfigGoodsVO.class);
            for (bmgIndexConfigGoodsVO bmgIndexConfigGoodsVO : bmgIndexConfigGoodsVOS) {
                String goodsName = bmgIndexConfigGoodsVO.getGoodsName();
                String goodsIntro = bmgIndexConfigGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 30) {
                    goodsName = goodsName.substring(0, 30) + "...";
                    bmgIndexConfigGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 22) {
                    goodsIntro = goodsIntro.substring(0, 22) + "...";
                    bmgIndexConfigGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        return bmgIndexConfigGoodsVOS;
    }

    @Override
    public Boolean deleteBatch(Long[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除数据
        return indexConfigMapper.deleteBatch(ids) > 0;
    }
}
