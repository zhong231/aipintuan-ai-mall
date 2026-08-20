package cn.bugstack.infrastructure.adapter.repository;

import cn.bugstack.domain.goods.adapter.repository.IGoodsRepository;
import cn.bugstack.infrastructure.dao.IOrderDao;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 结算仓储服务
 * @create 2025-02-15 09:13
 */
@Repository
public class GoodsRepository implements IGoodsRepository {

    @Resource
    private IOrderDao orderDao;

    @Override
    public void changeOrderDealDone(String orderId) {
        orderDao.changeOrderDealDone(orderId);
    }

}
