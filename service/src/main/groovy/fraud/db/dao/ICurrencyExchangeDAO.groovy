package fraud.db.dao

import java.text.ParseException

/**
 * Data access to the currency exchange rates
 */
interface ICurrencyExchangeDAO {

    void performScheduledCurrencyUpdate() throws IOException, ParseException;

    Double usdValue(final String currency, final Double amount)
}
