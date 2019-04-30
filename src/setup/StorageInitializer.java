package setup;

import java.time.LocalDate;
import java.time.LocalDateTime;

import application.model.Beer;
import application.model.BeerKeg;
import application.model.BeerTap;
import application.model.Customer;
import application.model.Gift;
import application.model.Order;
import application.model.PaymentMethod;
import application.model.PriceList;
import application.model.Product;
import application.model.WareHouse;
import application.model.WareHouseCapacity;
import application.service.Service;
import storage.DiskStorage;

public class StorageInitializer {

    public static void main(String[] args) {
        DiskStorage.clearStorage();
        initStorage();
    }

    /**
     * Initializes the storage with some objects.
     */
    private static void initStorage() {

        Service service = new Service();
        service.setStorage(DiskStorage.getInstance());
        Beer ip = service.createBeer("Indian Pale Ale", "Fadøl");
        Beer kl = service.createBeer("Klosterbryg", "Fadøl");
        Beer på = service.createBeer("Påskebryg", "Fadøl");
        Beer fj = service.createBeer("Fregatten Jylland", "Fadøl");
        Beer pi = service.createBeer("Pilsner", "Fadøl");
        Beer im = service.createBeer("Imperial Stout", "Fadøl");
        Product clip = service.createProduct("4 klip Klippekort", "Klippekort");
        Product chips = service.createProduct("Chips", "Fadøl");
        Product peanuts = service.createProduct("Peanuts", "Fadøl");
        Product kl2 = service.createProduct("KlosterBryg", "Flaske");
        Product ip2 = service.createProduct("Indian Pale Ale", "Flaske");
        Product bl = service.createProduct("Blondie", "Flaske");
        Product fj2 = service.createProduct("Fregatten Jylland", "Flaske");
        BeerKeg pikeg = service.createBeerKeg("Pilsner", 500, 25);
        BeerKeg ipkeg = service.createBeerKeg("India Pale Ale", 500, 20);
        Product stor = service.createCarbonicDioxide("Stor Patron", 250, 10);
        Product mellem = service.createCarbonicDioxide("Mellem Patron", 150, 6);
        Product lille = service.createCarbonicDioxide("Lille Patron", 100, 4);
        BeerTap b1 = service.createBeerTap("Lille Per", 1);
        service.createBeerTap("Tvillingerne", 2);
        BeerTap b3 = service.createBeerTap("Cerberus", 3);
        Customer cust = service.createCustomer("Simon Simonsen", 13131313);
        service.createCustomer("Peter", 88888888);
        service.createGift("Trækasse", 4, 0);
        Gift gift66 = service.createGift("Trækasse", 6, 6);
        PriceList priceList = service.createPriceList("Fredag");
        service.createPrice(priceList, ip, 30);
        service.createPrice(priceList, kl, 30);
        service.createPrice(priceList, på, 30);
        service.createPrice(priceList, fj, 30);
        service.createPrice(priceList, pi, 30);
        service.createPrice(priceList, im, 30);
        service.createPrice(priceList, stor, 400);
        service.createPrice(priceList, mellem, 350);
        service.createPrice(priceList, lille, 200);
        service.createPrice(priceList, pikeg, 300);
        service.createPrice(priceList, ipkeg, 350);
        service.createPrice(priceList, gift66, 290);
        service.createPrice(priceList, clip, 100);
        service.createPrice(priceList, peanuts, 10);
        service.createPrice(priceList, chips, 10);
        service.createPrice(priceList, kl2, 50);
        service.createPrice(priceList, ip2, 50);
        service.createPrice(priceList, fj2, 50);
        PriceList storePrices = service.createPriceList("Butikken");
        service.createPrice(storePrices, kl2, 36);
        service.createPrice(storePrices, ip2, 36);
        service.createPrice(storePrices, fj2, 36);
        service.createPrice(storePrices, bl, 36);
        service.createPrice(storePrices, stor, 400);
        service.createPrice(storePrices, mellem, 350);
        service.createPrice(storePrices, lille, 200);
        service.createPrice(storePrices, pikeg, 300);
        service.createPrice(storePrices, ipkeg, 350);
        service.createPrice(storePrices, pikeg, 300);
        service.createPrice(storePrices, ipkeg, 350);
        service.createPrice(storePrices, gift66, 290);

        service.reserveGuidedTour(LocalDateTime.of(2018, 04, 16, 12, 00), 70, 100, cust);
        service.reserveBeerTap(b1, LocalDate.parse("2018-05-12"), LocalDate.parse("2018-05-18"), cust, 250);
        service.reserveBeerTap(b3, LocalDate.parse("2018-05-01"), LocalDate.parse("2018-05-05"), cust, 600);

        Order twoClipCards = service.createOrder(null, LocalDate.parse("2018-04-04"));
        service.createOrderLine(twoClipCards, clip, 2, priceList);
        service.createPayment(twoClipCards, 200, PaymentMethod.MOBILEPAY, LocalDate.parse("2018-04-04"));

        Order oneClipCard = service.createOrder(cust, LocalDate.parse("2018-01-05"));
        service.createOrderLine(oneClipCard, clip, 1, priceList);
        service.createPayment(oneClipCard, 100, PaymentMethod.KORT, LocalDate.parse("2018-01-05"));

        Order fourIPA = service.createOrder(null, LocalDate.parse("2018-04-05"));
        service.createOrderLine(fourIPA, ip, 4, priceList);
        service.createPayment(fourIPA, 2, PaymentMethod.KLIP, LocalDate.parse("2018-04-05"));
        service.createPayment(fourIPA, 60, PaymentMethod.KONTANT, LocalDate.parse("2018-04-05"));

        Order giftBoxSixSix = service.createOrder(cust, LocalDate.parse("2018-03-01"));
        service.createOrderLine(giftBoxSixSix, gift66, 1, priceList);

        WareHouse wareHouse = service.getWareHouse();
        for (WareHouseCapacity product : wareHouse.getWareHouseCapacity()) {
            product.addToStock(50);
        }

        System.out.println("Storage created with objects.");

        Service.saveStorage();
    }

}
