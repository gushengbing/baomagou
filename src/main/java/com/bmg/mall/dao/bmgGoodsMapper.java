package com.bmg.mall.dao;

import com.bmg.mall.entity.StockNumDTO;
import com.bmg.mall.entity.bmgGoods;
import com.bmg.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface bmgGoodsMapper {
    int deleteByPrimaryKey(Long goodsId);

    int insert(bmgGoods record);

    int insertSelective(bmgGoods record);

    bmgGoods selectByPrimaryKey(Long goodsId);

    int updateByPrimaryKeySelective(bmgGoods record);

    int updateByPrimaryKeyWithBLOBs(bmgGoods record);

    int updateByPrimaryKey(bmgGoods record);

    List<bmgGoods> findbmgGoodsList(PageQueryUtil pageUtil);

    int getTotalbmgGoods(PageQueryUtil pageUtil);

    List<bmgGoods> selectByPrimaryKeys(List<Long> goodsIds);

    List<bmgGoods> findbmgGoodsListBySearch(PageQueryUtil pageUtil);

    int getTotalbmgGoodsBySearch(PageQueryUtil pageUtil);

    int batchInsert(@Param("bmgGoodsList") List<bmgGoods> bmgGoodsList);

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int batchUpdateSellStatus(@Param("orderIds")Long[] orderIds,@Param("sellStatus") int sellStatus);

}
