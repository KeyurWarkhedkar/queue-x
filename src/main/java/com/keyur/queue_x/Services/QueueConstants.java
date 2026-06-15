package com.keyur.queue_x.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class QueueConstants {

    // Queue name constants — unchanged, rest of code uses these
    public static final String orderQueue = "order_queue";
    public static final String outOfStock = "out_of_stock";
    public static final String notification = "notification";
    public static final String paymentQueue = "payment_queue";
    public static final String paymentSuccessQueue = "payment_success_queue";
    public static final String paymentFailedQueue = "payment_failed_queue";
    public static final String paymentFailedForOrderApiQueue = "payment_failed_for_order_api_queue";

    // SQS URL map — name → full SQS URL
    @Value("${sqs.url.order_queue}")
    private String orderQueueUrl;

    @Value("${sqs.url.out_of_stock}")
    private String outOfStockUrl;

    @Value("${sqs.url.notification}")
    private String notificationUrl;

    @Value("${sqs.url.payment_queue}")
    private String paymentQueueUrl;

    @Value("${sqs.url.payment_success_queue}")
    private String paymentSuccessQueueUrl;

    @Value("${sqs.url.payment_failed_queue}")
    private String paymentFailedQueueUrl;

    @Value("${sqs.url.payment_failed_for_order_api_queue}")
    private String paymentFailedForOrderApiQueueUrl;

    public Map<String, String> urlMap() {
        return Map.of(
                orderQueue,                     orderQueueUrl,
                outOfStock,                     outOfStockUrl,
                notification,                   notificationUrl,
                paymentQueue,                   paymentQueueUrl,
                paymentSuccessQueue,            paymentSuccessQueueUrl,
                paymentFailedQueue,             paymentFailedQueueUrl,
                paymentFailedForOrderApiQueue,  paymentFailedForOrderApiQueueUrl
        );
    }
}