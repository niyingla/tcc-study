package org.mengyun.tcctransaction.sample.dubbo.order.service;

import org.apache.commons.lang3.tuple.Pair;
import org.mengyun.tcctransaction.CancellingException;
import org.mengyun.tcctransaction.ConfirmingException;
import org.mengyun.tcctransaction.sample.order.domain.entity.Order;
import org.mengyun.tcctransaction.sample.order.domain.entity.Shop;
import org.mengyun.tcctransaction.sample.order.domain.repository.ShopRepository;
import org.mengyun.tcctransaction.sample.order.domain.service.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by changming.xie on 4/1/16.
 */
@Service
public class PlaceOrderServiceImpl {

    @Autowired
    ShopRepository shopRepository;

    @Autowired
    OrderServiceImpl orderService;

    @Autowired
    PaymentServiceImpl paymentService;

    public String placeOrder(long payerUserId, long shopId, List<Pair<Long, Integer>> productQuantities, final BigDecimal redPacketPayAmount) {
        Shop shop = shopRepository.findById(shopId);

        final Order order = orderService.createOrder(payerUserId, shop.getOwnerUserId(), productQuantities);

        Boolean result = false;

        try {
//              创建线程池  提交支付任务
//            ExecutorService executorService = Executors.newFixedThreadPool(2);

//            Future future1 = executorService.submit(new Runnable() {
//                @Override
//                public void run() {
                    //直接支付
                    paymentService.makePayment(order.getMerchantOrderNo(), order, redPacketPayAmount, order.getTotalAmount().subtract(redPacketPayAmount));
//                }
//            });
                    //其他事物内操作
//            Future future2 = executorService.submit(new Runnable() {
//                @Override
//                public void run() {
//                    paymentService.makePayment(order.getMerchantOrderNo(), order, redPacketPayAmount, order.getTotalAmount().subtract(redPacketPayAmount));
//                }
//            });
//
            //获取异步等待结果
//            future1.get();
//            future2.get();

        } catch (ConfirmingException confirmingException) {
            //exception throws with the tcc transaction status is CONFIRMING,
            //when tcc transaction is confirming status,
            // the tcc transaction recovery will try to confirm the whole transaction to ensure eventually consistent.

            result = true;
        } catch (CancellingException cancellingException) {
            //exception throws with the tcc transaction status is CANCELLING,
            //when tcc transaction is under CANCELLING status,
            // the tcc transaction recovery will try to cancel the whole transaction to ensure eventually consistent.
        } catch (Throwable e) {
            //other exceptions throws at TRYING stage.
            //you can retry or cancel the operation.
            e.printStackTrace();
        }

        return order.getMerchantOrderNo();
    }
}
