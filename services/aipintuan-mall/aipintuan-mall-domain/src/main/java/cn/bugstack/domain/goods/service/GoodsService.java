package cn.bugstack.domain.goods.service;

import cn.bugstack.domain.goods.adapter.repository.IGoodsRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 结算服务
 * @create 2025-02-15 09:11
 */
@Service
public class GoodsService implements IGoodsService {

    @Resource
    private IGoodsRepository repository;


    @Override
    public void changeOrderDealDone(String orderId) {
        repository.changeOrderDealDone(orderId);
    }

}
