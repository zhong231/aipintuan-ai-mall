package com.aipintuan.api;

import com.aipintuan.api.dto.CreatePayRequestDTO;
import com.aipintuan.api.dto.NotifyRequestDTO;
import com.aipintuan.api.dto.QueryOrderListRequestDTO;
import com.aipintuan.api.dto.QueryOrderListResponseDTO;
import com.aipintuan.api.dto.RefundOrderRequestDTO;
import com.aipintuan.api.dto.RefundOrderResponseDTO;
import com.aipintuan.api.response.Response;

public interface IPayService {

    Response<String> createPayOrder(CreatePayRequestDTO createPayRequestDTO);

    /**
     * 拼团结算回调
     *
     * @param requestDTO 请求对象
     * @return 返参，success 成功
     */
    String groupBuyNotify(NotifyRequestDTO requestDTO);

    /**
     * 查询用户订单列表
     *
     * @param requestDTO 请求对象
     * @return 订单列表
     */
    Response<QueryOrderListResponseDTO> queryUserOrderList(QueryOrderListRequestDTO requestDTO);

    /**
     * 用户退单
     *
     * @param requestDTO 请求对象
     * @return 退单结果
     */
    Response<RefundOrderResponseDTO> refundOrder(RefundOrderRequestDTO requestDTO);

    Response<String> activePayNotify(String outTradeNo);
}
