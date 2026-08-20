package cn.bugstack.test;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

@Slf4j
public class AliPayTest {

    // 「沙箱环境」应用ID - 您的APPID，收款账号既是你的APPID对应支付宝账号。获取地址；https://open.alipay.com/develop/sandbox/app
    public static String app_id = "demo-app-id";
    // 「沙箱环境」商户私钥，你的PKCS8格式RSA2私钥
    public static String merchant_private_key = "test-placeholder";
    // 「沙箱环境」支付宝公钥
    public static String alipay_public_key = "test-placeholder";
    // 「沙箱环境」服务器异步通知回调地址
    public static String notify_url = "http://localhost/disabled";
    // 「沙箱环境」页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost/disabled";
    // 「沙箱环境」
    public static String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    // 签名方式
    public static String sign_type = "RSA2";
    // 字符编码格式
    public static String charset = "utf-8";

    private AlipayClient alipayClient;

    @Before
    public void init() {
        this.alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id,
                merchant_private_key,
                "json",
                charset,
                alipay_public_key,
                sign_type);
    }

    @Test
    public void test_aliPay_pageExecute() throws AlipayApiException {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();  // 发送请求的 Request类
        request.setNotifyUrl(notify_url);
        request.setReturnUrl(return_url);

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", "xfg000091001902");  // 我们自己生成的订单编号
        bizContent.put("total_amount", "0.01"); // 订单的总金额
        bizContent.put("subject", "测试商品");   // 支付的名称
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");  // 固定配置
        request.setBizContent(bizContent.toString());

        String form = alipayClient.pageExecute(request).getBody();
        log.info("测试结果：{}", form);
        // 支付表单内容由沙箱环境动态生成，不在仓库中保存。
    }

    /**
     * 查询订单
     */
    @Test
    public void test_alipay_certificateExecute() throws AlipayApiException {

        AlipayTradeQueryModel bizModel = new AlipayTradeQueryModel();
        bizModel.setOutTradeNo("571486993823");

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizModel(bizModel);

        String body = alipayClient.execute(request).getBody();
        log.info("测试结果：{}", body);
    }

    /**
     * 退款接口
     */
    @Test
    public void test_alipay_refund() throws AlipayApiException {
        AlipayTradeRefundRequest request =new AlipayTradeRefundRequest();
        AlipayTradeRefundModel refundModel =new AlipayTradeRefundModel();
        refundModel.setOutTradeNo("daniel82AAAA000032333361X03");
        refundModel.setRefundAmount("1.00");
        refundModel.setRefundReason("退款说明");
        request.setBizModel(refundModel);

        AlipayTradeRefundResponse execute = alipayClient.execute(request);
        log.info("测试结果：{}", execute.isSuccess());
    }

    public static void main(String[] args) {
        System.out.println(new BigDecimal("9.99").doubleValue());
    }

}

