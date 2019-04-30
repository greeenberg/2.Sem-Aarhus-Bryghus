package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import application.model.Customer;
import application.model.Order;
import application.model.OrderLine;
import application.model.Payment;
import application.model.PaymentMethod;
import application.model.Price;
import application.model.PriceList;
import application.model.Product;
import application.service.Service;
import storage.DiskStorage;

public class OrderTest {
    Service service = new Service();

    Customer customer = null;// service.createCustomer("Simon", 88776655);
    LocalDate firstDate = null;//LocalDate.of(2018, 01, 01);
    Order firstOrder = null;//service.createOrder(customer, firstDate);
    Order secondOrder = null;//service.createOrder(null, firstDate);
    Product ip = null;//service.createProduct("Indian Pale Ale", "Fadøl");
    Product kl = null;//service.createProduct("Klosterbryg", "Fadøl");
    Product chips = null;
    Product clip = null;
    Product shirt = null;
    PriceList prices = null;//service.createPriceList("TestPrices");
    Price ipPrice = null;//service.createPrice(prices, ip, 30);
    Price klPrice = null;//service.createPrice(prices, kl, 20);
    Price chipsPrice = null;
    Price clipPrice = null;
    Price shirtPrice = null;
    OrderLine orderLineFirst = null;//firstOrder.createOrderLine(ip, 1, prices);
    OrderLine orderLineSecond = null;//secondOrder.createOrderLine(kl, 2, prices);
    OrderLine orderLineRemove = null;//firstOrder.createOrderLine(ip, 5, prices);

    @Before
    public void init() {
        DiskStorage.clearStorage();
        service.setStorage(DiskStorage.getInstance());
        customer = service.createCustomer("Simon", 88776655);
        firstDate = LocalDate.of(2018, 01, 01);
        firstOrder = service.createOrder(customer, firstDate);
        secondOrder = service.createOrder(null, firstDate);
        ip = service.createProduct("Indian Pale Ale", "Fadøl");
        kl = service.createProduct("Klosterbryg", "Fadøl");
        chips = service.createProduct("Chips", "Fadøl");
        clip = service.createProduct("Test Klipperen", "Klippekort");
        shirt = service.createProduct("XXXL T-shirt", "Beklædning");
        prices = service.createPriceList("TestPrices");
        ipPrice = service.createPrice(prices, ip, 30);
        klPrice = service.createPrice(prices, kl, 20);
        chipsPrice = service.createPrice(prices, chips, 15);
        clipPrice = service.createPrice(prices, clip, 100);
        shirtPrice = service.createPrice(prices, shirt, 100);

    }

    @Test
    public void testOrderConstructorTC1() {
        assertNotNull(firstOrder);
    }

    @Test
    public void testOrderConstructorTC2() {
        assertNotNull(secondOrder);
    }

    @Test
    public void testGetCustomerTC1() {
        assertNotNull(firstOrder.getCustomer());
    }

    @Test
    public void testGetCustomerTC2() {
        assertNull(secondOrder.getCustomer());
    }

    @Test
    public void testGetDateTC1() {
        LocalDate testDate = LocalDate.parse("2018-01-01");
        assertEquals(testDate, firstOrder.getDate());
    }

    @Test
    public void testCreateOrderLineTC1() {
        orderLineFirst = firstOrder.createOrderLine(ip, 1, prices);
        orderLineRemove = firstOrder.createOrderLine(ip, 5, prices);
        assertNotNull(orderLineFirst);
        assertNotNull(orderLineRemove);
        assertEquals(30, orderLineFirst.getPrice(), 0.01);
        assertEquals(30, orderLineRemove.getPrice(), 0.01);
        assertTrue(firstOrder.getOrderLines().contains(orderLineFirst));
        assertTrue(firstOrder.getOrderLines().contains(orderLineRemove));
    }

    @Test
    public void testCreateOrderLineTC2() {
        orderLineSecond = secondOrder.createOrderLine(kl, 2, prices);
        assertNotNull(orderLineSecond);
        assertEquals(20, orderLineSecond.getPrice(), 0.01);
        assertTrue(secondOrder.getOrderLines().contains(orderLineSecond));
    }

    @Test
    public void testGiceDiscountTC1() {
        orderLineFirst = firstOrder.createOrderLine(ip, 1, prices);
        orderLineFirst.giveDiscountPercentage(100);

        Assert.assertEquals(0, orderLineFirst.getPrice(), 0.01);
    }

    @Test
    public void testGiceDiscountTC2() {
        orderLineFirst = firstOrder.createOrderLine(ip, 1, prices);
        orderLineFirst.giveDiscountPercentage(50);
        Assert.assertEquals(15, orderLineFirst.getPrice(), 0.01);
    }

    @Test
    public void testGiceDiscountTC3() {
        orderLineFirst = firstOrder.createOrderLine(ip, 1, prices);
        orderLineFirst.giveDiscountWholeAmount(30);
        Assert.assertEquals(0, orderLineFirst.getPrice(), 0.01);
    }

    @Test
    public void testGiceDiscountTC4() {
        orderLineFirst = firstOrder.createOrderLine(ip, 1, prices);
        orderLineFirst.giveDiscountWholeAmount(31);
        Assert.assertEquals(0, orderLineFirst.getPrice(), 0.01);
    }

    @Test
    public void testCalcTotalPriceTC1() {
        orderLineFirst = firstOrder.createOrderLine(ip, 1, prices);
        orderLineRemove = firstOrder.createOrderLine(ip, 5, prices);
        assertEquals(180, firstOrder.calcTotalPrice(), 0.01);
    }

    @Test
    public void testCalcTotalPriceTC2() {
        orderLineSecond = secondOrder.createOrderLine(kl, 2, prices);
        assertEquals(40, secondOrder.calcTotalPrice(), 0.01);
    }

    @Test
    public void testRemoveOrderLineTC1() {
        orderLineFirst = firstOrder.createOrderLine(ip, 1, prices);
        orderLineRemove = firstOrder.createOrderLine(ip, 5, prices);
        assertTrue(firstOrder.getOrderLines().contains(orderLineRemove));
        firstOrder.removeOrderLine(orderLineRemove);
        assertFalse(firstOrder.getOrderLines().contains(orderLineRemove));
    }

    Payment firstPayment = null;

    @Test
    public void testCreatePaymentTC1() {
        firstPayment = firstOrder.createPayment(30, PaymentMethod.KORT, firstDate);
        assertNotNull(firstPayment);
        assertTrue(firstOrder.getPayments().contains(firstPayment));
    }

    Payment secondPayment = null;

    @Test
    public void testCreatePaymentTC2() {
        orderLineSecond = secondOrder.createOrderLine(kl, 2, prices);
        secondPayment = secondOrder.createPayment(1, PaymentMethod.KLIP, firstDate);
        assertNotNull(secondPayment);
        assertTrue(secondOrder.getPayments().contains(secondPayment));
    }

    OrderLine secondOrderLineForAnotherPayment = null;

    Payment secondPayment2 = null;

    @Test
    public void testCreatePaymentTC3() {
        secondOrderLineForAnotherPayment = secondOrder.createOrderLine(ip, 1, prices);
        secondPayment2 = secondOrder.createPayment(30, PaymentMethod.KORT, firstDate);
        assertNotNull(secondPayment2);
        assertTrue(secondOrder.getPayments().contains(secondPayment2));
    }

    @Test
    public void testPaymentTC1() {
        secondOrderLineForAnotherPayment = secondOrder.createOrderLine(ip, 1, prices);
        secondPayment2 = secondOrder.createPayment(30, PaymentMethod.KORT, firstDate);
        assertNotNull(secondPayment2);
        assertEquals(0, secondOrder.calcPrice(), 0.01);
    }

    @Test
    public void testPaymentTC2() {
        secondOrderLineForAnotherPayment = secondOrder.createOrderLine(ip, 1, prices);
        secondPayment2 = secondOrder.createPayment(29, PaymentMethod.KORT, firstDate);
        assertEquals(1, secondOrder.calcPrice(), 0.01);
    }

    @Test
    public void testPaymentClipperCardTC1() {

        firstOrder.createOrderLine(ip, 1, prices);
        firstOrder.createOrderLine(chips, 1, prices);

        firstOrder.createPayment(1, PaymentMethod.KLIP, firstDate);
        assertEquals(15, firstOrder.calcPrice(), 0.01);

    }

    @Test
    public void testPaymentClipperCardTC2() {

        firstOrder.createOrderLine(ip, 2, prices);

        firstOrder.createPayment(1, PaymentMethod.KLIP, firstDate);
        assertEquals(30, firstOrder.calcPrice(), 0.01);

    }

    @Test
    public void testPaymentClipperCardTC3() {

        firstOrder.createOrderLine(ip, 1, prices);
        firstOrder.createOrderLine(clip, 1, prices);

        firstOrder.createPayment(1, PaymentMethod.KLIP, firstDate);
        assertEquals(100, firstOrder.calcPrice(), 0.01);

    }

    @Test
    public void testPaymentClipperCardTC4() {

        firstOrder.createOrderLine(ip, 1, prices);
        firstOrder.createOrderLine(shirt, 1, prices);

        firstOrder.createPayment(1, PaymentMethod.KLIP, firstDate);
        assertEquals(100, firstOrder.calcPrice(), 0.01);

    }

    @Test
    public void testPaymentClipperCardTC5() {

        firstOrder.createOrderLine(ip, 1, prices);
        firstOrder.createOrderLine(chips, 1, prices);

        firstOrder.createPayment(2, PaymentMethod.KLIP, firstDate);
        assertEquals(0, firstOrder.calcPrice(), 0.01);

    }

    @Test
    public void testCalcPriceTC1() {
        assertEquals(0, firstOrder.calcPrice(), 0.01);
    }

    @Test
    public void testCalcPriceTC2() {
        assertEquals(0, secondOrder.calcPrice(), 0.01);
    }

    @Test
    public void testIsPaidTC1() {
        firstOrder.calcPrice();
        assertTrue(firstOrder.isPaid());
    }

    @Test
    public void testIsPaidTC2() {
        assertFalse(secondOrder.isPaid());
    }

    @Test
    public void testGetOrderLinesTC1() {
        orderLineFirst = firstOrder.createOrderLine(ip, 1, prices);
        assertTrue(firstOrder.getOrderLines().contains(orderLineFirst));
    }

    @Test
    public void testGetOrderLinesTC2() {
        orderLineSecond = secondOrder.createOrderLine(kl, 2, prices);
        assertTrue(secondOrder.getOrderLines().contains(orderLineSecond));
    }

    @Test
    public void testGetPaymentsTC1() {
        firstPayment = firstOrder.createPayment(30, PaymentMethod.KORT, firstDate);
        assertTrue(firstOrder.getPayments().contains(firstPayment));
    }

    @Test
    public void testGetPaymentsTC2() {
        secondPayment = secondOrder.createPayment(1, PaymentMethod.KLIP, firstDate);
        secondPayment2 = secondOrder.createPayment(30, PaymentMethod.KORT, firstDate);
        assertTrue(secondOrder.getPayments().contains(secondPayment));
        assertTrue(secondOrder.getPayments().contains(secondPayment2));
    }

    OrderLine secondOrderLine2 = null;

    @Test
    public void testCalcRemainingPaymentTC1() {
        secondOrderLine2 = secondOrder.createOrderLine(ip, 1, prices);
        secondOrder.createPayment(30, PaymentMethod.KONTANT, firstDate);
        assertEquals(0, secondOrder.calcRemainingPayment(), 0.01);
    }

    @Test
    public void testCalcRemainingPaymentTC2() {
        secondOrderLine2 = secondOrder.createOrderLine(ip, 1, prices);
        assertEquals(30, secondOrder.calcRemainingPayment(), 0.01);
    }

}
