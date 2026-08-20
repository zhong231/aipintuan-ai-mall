package com.aipintuan.domain.trade.service.settlement.filter;

import com.aipintuan.domain.trade.adapter.repository.ITradeRepository;
import com.aipintuan.domain.trade.model.entity.MarketPayOrderEntity;
import com.aipintuan.domain.trade.model.entity.TradeSettlementRuleCommandEntity;
import com.aipintuan.domain.trade.model.entity.TradeSettlementRuleFilterBackEntity;
import com.aipintuan.domain.trade.model.valobj.TradeOrderStatusEnumVO;
import com.aipintuan.domain.trade.service.settlement.factory.TradeSettlementRuleFilterFactory;
import com.aipintuan.types.enums.ResponseCode;
import com.aipintuan.types.exception.AppException;
import cn.bugstack.wrench.design.framework.link.model2.handler.ILogicHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description 外部交易单号过滤；外部交易单号是否为退单
 * @create 2025-01-29 09:37
 */
@Slf4j
@Service
public class OutTradeNoRuleFilter implements ILogicHandler<TradeSettlementRuleCommandEntity, TradeSettlementRuleFilterFactory.DynamicContext, TradeSettlementRuleFilterBackEntity> {

    @Resource
    private ITradeRepository repository;

    @Override
    public TradeSettlementRuleFilterBackEntity apply(TradeSettlementRuleCommandEntity requestParameter, TradeSettlementRuleFilterFactory.DynamicContext dynamicContext) throws Exception {
        log.info("结算规则过滤-外部单号校验{} outTradeNo:{}", requestParameter.getUserId(), requestParameter.getOutTradeNo());

        // 查询拼团信息
        MarketPayOrderEntity marketPayOrderEntity = repository.queryMarketPayOrderEntityByOutTradeNo(requestParameter.getUserId(), requestParameter.getOutTradeNo());

        if (null == marketPayOrderEntity || TradeOrderStatusEnumVO.CLOSE.equals(marketPayOrderEntity.getTradeOrderStatusEnumVO())) {
            log.error("不存在的外部交易单号或用户已退单，不需要做支付订单结算:{} outTradeNo:{}", requestParameter.getUserId(), requestParameter.getOutTradeNo());
            throw new AppException(ResponseCode.E0104);
        }

        dynamicContext.setMarketPayOrderEntity(marketPayOrderEntity);

        return next(requestParameter, dynamicContext);
    }

}
