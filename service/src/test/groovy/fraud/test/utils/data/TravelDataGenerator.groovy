package fraud.test.utils.data

import common.testutils.gen.AbstractGenerator
import fraud.api.v1.check.Transaction
import fraud.api.v1.check.payment.Amount
import fraud.api.v1.check.payment.Payment
import fraud.api.v1.check.person.Account
import fraud.api.v1.check.person.Person
import fraud.api.v1.check.person.Telephone
import fraud.api.v1.check.person.Traveler
import fraud.api.v1.check.product.Product
import fraud.api.v1.check.product.travel.TravelProduct

import static common.testutils.gen.DateGenerator.generateDateInFuture
import static common.testutils.gen.FinanceGenerator.generateCurrencyCode
import static common.testutils.gen.NumbersGenerator.*
import static common.testutils.gen.PersonGenerator.*

/**
 * Generates Fraud Specific data
 */
abstract class TravelDataGenerator extends AbstractGenerator {

    static Transaction generateTransaction() {
        Transaction transaction = new Transaction();
        transaction.setAccount(generateAccount());
        transaction.setMachineGUID(generateGUID());

        int productsCount = generateInt(1, 4);
        List<Product> products = new ArrayList<>(productsCount);
        for (int i = 0; i < productsCount; i++) {
            products.add(generateTravelProduct(
                    (TravelProduct.Type) TravelProduct.Type.values()[RAND.nextInt(4)],
                    generateInt(100, 10000)));
        }
        transaction.setProducts(products);

        int paymentsCount = generateInt(1, 2);
        List<Payment> payments = new ArrayList<>(paymentsCount);
        for (int i = 0; i < paymentsCount; i++) {
            payments.add(generatePayment(transaction.getAccount()));
        }
        transaction.setPayments(payments);

        return transaction;
    }

    static Payment generatePayment(Person owner) {
        Payment payment = new Payment();
        payment.setAmount(generateAmount(generateInt(300, 20000)));
        payment.setFormOfPayment("CreditCard");
        payment.setPayer(owner);
        return payment;
    }

    static Product generateProduct(final double amount) {
        return fillProductData(new Product(), amount);
    }

    static TravelProduct generateTravelProduct(final TravelProduct.Type type, final double amount) {
        TravelProduct product = fillProductData(new TravelProduct(), amount);
        product.setDateStart(generateDateInFuture(5, 10));
        product.setDateEnd(generateDateInFuture(10, 15));
        product.setType(type);
        return product;
    }

    private static <T extends Product> T fillProductData(final T product, final double amount) {
        product.setPrice(generateAmount(amount));
        return product;
    }

    static Amount generateAmount(final double amount) {
        return fillAmountData(new Amount(), amount);
    }

    private static <T extends Amount> T fillAmountData(final T amount, final double value) {
        amount.setCurrency(generateCurrencyCode());
        amount.setValue(new BigDecimal(value));
        amount.setUsdValue(new BigDecimal(value));
        return amount;
    }

    static Person generatePerson() {
        return fillPersonData(new Person());
    }

    static Traveler generateTraveler() {
        Traveler traveler = new Traveler();
        fillPersonData(traveler);
        traveler.setIsPrimary(RAND.nextBoolean());
        return traveler;
    }

    static Account generateAccount() {
        Account account = new Account();
        fillPersonData(account);
        account.setLogin(generateEmail(account.getFirstName()));
        account.setPassword(generatePasswordData());
        return account;
    }

    private static <T extends Person> T fillPersonData(final T person) {
        person.setAge(generateAge());
        person.setGender(generateGender().charAt(0));
        person.setFirstName(generateFirstName(person.getGender() == 'M'));
        person.setLastName(generateLastName());
        person.setEmail(generateEmail(person.getFirstName()));
        person.setBirthDate(generateBirthDate(person.getAge()));
        person.setMiddleName(generateFirstName(person.getGender() == 'M'));

        final int phonesCount = RAND.nextInt(4);
        if (phonesCount > 0) {
            List<Telephone> telephones = new ArrayList<Telephone>(phonesCount);
            for (int i = 0; i < phonesCount; i++) {
                Telephone telephone = new Telephone();
                telephone.setAreaCode(generateNumber(3));
                telephone.setCountryAccessCode(generateNumber(2));
                telephone.setPhoneNumber(generateNumber(5, false));
                telephone.setType((Telephone.Type) Telephone.Type.values()[RAND.nextInt(3)]);
                telephones.add(telephone);
            }
            person.setTelephones(telephones);
        }
        return person;
    }
}
