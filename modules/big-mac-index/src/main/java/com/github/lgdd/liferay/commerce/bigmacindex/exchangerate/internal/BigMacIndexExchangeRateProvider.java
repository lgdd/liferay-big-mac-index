package com.github.lgdd.liferay.commerce.bigmacindex.exchangerate.internal;

import static com.github.lgdd.liferay.commerce.bigmacindex.exchangerate.api.BigMacIndexConstants.EXCHANGE_RATE_PROVIDER_KEY;
import static com.github.lgdd.liferay.commerce.bigmacindex.exchangerate.api.BigMacIndexConstants.RAW_INDEX_URL;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.util.ExchangeRateProvider;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Map;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    immediate = true,
    property = "commerce.exchange.provider.key=" + EXCHANGE_RATE_PROVIDER_KEY,
    service = ExchangeRateProvider.class
)
public class BigMacIndexExchangeRateProvider
    implements ExchangeRateProvider {

  @Override
  public BigDecimal getExchangeRate(CommerceCurrency primaryCommerceCurrency,
      CommerceCurrency secondaryCommerceCurrency)
      throws Exception {

    final String csv = _http.URLtoString(new URL(RAW_INDEX_URL));
    final Map<String, BigDecimal> rawIndexMap = _exchangeRateParser.toMap(csv);

    final String primaryCurrencyCode =
        StringUtil.toUpperCase(primaryCommerceCurrency.getCode());

    final String secondaryCurrencyCode =
        StringUtil.toUpperCase(secondaryCommerceCurrency.getCode());

    final BigDecimal rateToPrimary = rawIndexMap.get(primaryCurrencyCode);
    final BigDecimal rateToSecondary = rawIndexMap.get(secondaryCurrencyCode);

    return rateToSecondary.divide(rateToPrimary, 4, RoundingMode.HALF_EVEN);
  }

  @Reference
  protected BigMacIndexExchangeRateParser _exchangeRateParser;

  @Reference
  protected Http _http;

}
