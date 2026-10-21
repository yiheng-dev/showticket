package com.showticket.order.constants;

public interface MQConstants {
    String DELAY_EXCHANGE_NAME = "ticket.delay.direct";
    String DELAY_ORDER_QUEUE_NAME = "ticket.delay.order.queue";
    String DELAY_ORDER_KEY = "delay.order.expire";
}
