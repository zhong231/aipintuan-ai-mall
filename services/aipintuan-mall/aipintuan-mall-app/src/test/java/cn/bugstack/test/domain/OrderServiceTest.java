package cn.bugstack.test.domain;

import cn.bugstack.domain.order.model.entity.PayOrderEntity;
import cn.bugstack.domain.order.model.entity.ShopCartEntity;
import cn.bugstack.domain.order.model.valobj.MarketTypeVO;
import cn.bugstack.domain.order.service.IOrderService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTest {

    @Resource
    private IOrderService orderService;

    @Test
    public void test_createOrder_NO_MARKET() throws Exception {
        ShopCartEntity shopCartEntity = new ShopCartEntity();
        shopCartEntity.setUserId("xiaofuge01");
        shopCartEntity.setProductId("9890001");
        shopCartEntity.setTeamId(null);
        shopCartEntity.setActivityId(100123L);
        shopCartEntity.setMarketTypeVO(MarketTypeVO.NO_MARKET);

        PayOrderEntity payOrderEntity = orderService.createOrder(shopCartEntity);

        log.info("请求参数:{}", JSON.toJSONString(shopCartEntity));
        log.info("测试结果:{}", JSON.toJSONString(payOrderEntity));
    }

    @Test
    public void test_createOrder_GROUP_BUY_MARKET() throws Exception {
        ShopCartEntity shopCartEntity = new ShopCartEntity();
        shopCartEntity.setUserId("xiaofuge20");
        shopCartEntity.setProductId("9890001");
        shopCartEntity.setTeamId(null);
        shopCartEntity.setActivityId(100123L);
        shopCartEntity.setMarketTypeVO(MarketTypeVO.GROUP_BUY_MARKET);

        PayOrderEntity payOrderEntity = orderService.createOrder(shopCartEntity);

        log.info("请求参数:{}", JSON.toJSONString(shopCartEntity));
        log.info("测试结果:{}", JSON.toJSONString(payOrderEntity));
    }

}
