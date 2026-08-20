package cn.bugstack.api;

import cn.bugstack.api.dto.CreatePayRequestDTO;
import cn.bugstack.api.dto.NotifyRequestDTO;
import cn.bugstack.api.dto.QueryOrderListRequestDTO;
import cn.bugstack.api.dto.QueryOrderListResponseDTO;
import cn.bugstack.api.dto.RefundOrderRequestDTO;
import cn.bugstack.api.dto.RefundOrderResponseDTO;
import cn.bugstack.api.response.Response;

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
