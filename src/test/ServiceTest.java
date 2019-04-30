package test;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import application.model.Customer;
import application.model.GuidedTourReservation;
import application.model.Order;
import application.model.PaymentMethod;
import application.model.PriceList;
import application.model.Product;
import application.service.Service;
import storage.DiskStorage;

public class ServiceTest {
    Service service = new Service();
    Customer cust = null;

    @Before
    public void init() {
        service.setStorage(DiskStorage.getInstance());
        cust = new Customer("Terkil Tester", 78978978);
    }

    @Test
    public void testReserveGuidedTourTC1() {
        LocalDateTime ldt = LocalDateTime.of(LocalDate.parse("2018-01-01"), LocalTime.of(13, 00));
        GuidedTourReservation GTReservation = (GuidedTourReservation) service.reserveGuidedTour(ldt, 20, 100,
                cust);
        Assert.assertNotNull(GTReservation);
    }

    @Test(expected = DateTimeException.class)
    public void testReserveGuidedTourTC2() {
        LocalDateTime ldt = LocalDateTime.of(LocalDate.parse("2018-01-01"), LocalTime.of(11, 00));
        service.reserveGuidedTour(ldt, 20, 100, cust);
    }

    @Test(expected = DateTimeException.class)
    public void testReserveGuidedTourTC3() {
        LocalDateTime ldt = LocalDateTime.of(LocalDate.parse("2018-01-01"), LocalTime.of(18, 01));
        service.reserveGuidedTour(ldt, 20, 100, cust);
    }

    @Test
    public void testReserveGuidedTourTC4() {
        LocalDateTime ldt = LocalDateTime.of(LocalDate.parse("2018-01-01"), LocalTime.of(13, 00));
        GuidedTourReservation GTReservation = (GuidedTourReservation) service.reserveGuidedTour(ldt, 20, 100,
                cust);
        Assert.assertNotNull(GTReservation);
        LocalDateTime ldt2 = LocalDateTime.of(LocalDate.parse("2018-01-01"), LocalTime.of(17, 00));
        GuidedTourReservation GTReservation2 = (GuidedTourReservation) service.reserveGuidedTour(ldt2, 30, 100,
                cust);
        Assert.assertNotNull(GTReservation2);
    }

    @Test
    public void testReserveGuidedTourTC5() {
        LocalDateTime ldt = LocalDateTime.of(LocalDate.parse("2018-01-01"), LocalTime.of(13, 00));
        GuidedTourReservation GTReservation = (GuidedTourReservation) service.reserveGuidedTour(ldt, 20, 100,
                cust);
        LocalDateTime ldt2 = LocalDateTime.of(LocalDate.parse("2018-01-01"), LocalTime.of(14, 00));
        service.reserveGuidedTour(ldt2, 30, 100, cust);
        Assert.assertNotNull(GTReservation);
    }

    @Test
    public void testGetAmountSoldByTypeInPeriod() {
        Assert.assertEquals(4, service.getAmountSoldByTypeInPeriod("Fadøl", LocalDate.parse("2018-04-05"),
                LocalDate.parse("2018-04-07")));
        Assert.assertEquals(1, service.getAmountSoldByTypeInPeriod("Klippekort", LocalDate.parse("2018-01-04"),
                LocalDate.parse("2018-04-01")));
        Assert.assertEquals(3, service.getAmountSoldByTypeInPeriod("Klippekort", LocalDate.parse("2018-01-04"),
                LocalDate.parse("2018-04-10")));
    }

    @Test
    public void testRemainingClipsInPeriodTC1() {
        Product ip = service.createProduct("Indian Pale Ale", "Fadøl");
        Product kl = service.createProduct("Klosterbryg", "Fadøl");
        Product clip = service.createProduct("4 klip Klippekort", "Klippekort");
        PriceList priceList = service.createPriceList("TestListe");
        service.createPrice(priceList, ip, 30);
        service.createPrice(priceList, kl, 30);
        service.createPrice(priceList, clip, 100);

        Order twoClipCards = service.createOrder(null, LocalDate.parse("2018-04-04"));
        service.createOrderLine(twoClipCards, clip, 2, priceList);
        service.createPayment(twoClipCards, 200, PaymentMethod.MOBILEPAY, LocalDate.parse("2018-04-04"));

        Order fourIPA = service.createOrder(null, LocalDate.parse("2018-04-05"));
        service.createOrderLine(fourIPA, ip, 4, priceList);
        service.createPayment(fourIPA, 2, PaymentMethod.KLIP, LocalDate.parse("2018-04-05"));
        service.createPayment(fourIPA, 60, PaymentMethod.KONTANT, LocalDate.parse("2018-04-05"));

        Order oneClipCard = service.createOrder(null, LocalDate.parse("2018-01-05"));
        service.createOrderLine(oneClipCard, clip, 1, priceList);
        service.createPayment(oneClipCard, 100, PaymentMethod.KORT, LocalDate.parse("2018-01-05"));

        Assert.assertEquals(6,
                service.remainingClipsInPeriod(LocalDate.parse("2018-04-01"), LocalDate.parse("2018-04-05")));

    }

    @Test
    public void testRemainingClipsInPeriodTC2() {
        Assert.assertEquals(8,
                service.remainingClipsInPeriod(LocalDate.parse("2018-04-04"), LocalDate.parse("2018-04-04")));
    }

    @Test
    public void testRemainingClipsInPeriodTC3() {
        Assert.assertEquals(10,
                service.remainingClipsInPeriod(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-04-05")));
    }

}
