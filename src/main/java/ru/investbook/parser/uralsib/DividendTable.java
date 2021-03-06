/*
 * InvestBook
 * Copyright (C) 2020  Vitalii Ananev <an-vitek@ya.ru>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.investbook.parser.uralsib;

import lombok.extern.slf4j.Slf4j;
import ru.investbook.parser.ReportTable;
import ru.investbook.parser.SecurityTransaction;
import ru.investbook.parser.table.Table;
import ru.investbook.parser.table.TableRow;
import ru.investbook.pojo.CashFlowType;
import ru.investbook.pojo.Security;
import ru.investbook.pojo.SecurityEventCashFlow;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Double.parseDouble;
import static java.util.Collections.emptyList;
import static ru.investbook.parser.uralsib.PaymentsTable.PaymentsTableHeader.*;

@Slf4j
public class DividendTable extends PaymentsTable {

    private static final String DIVIDEND_ACTION = "Доход по финансовым инструментам";
    private final Pattern taxInformationPattern = Pattern.compile("налог в размере ([0-9\\.]+) удержан");
    private static final BigDecimal minValue = BigDecimal.valueOf(0.01);

    public DividendTable(UralsibBrokerReport report,
                         PortfolioSecuritiesTable securitiesTable,
                         ReportTable<SecurityTransaction> securityTransactionTable) {
        super(report, securitiesTable, securityTransactionTable);
    }

    @Override
    protected Collection<SecurityEventCashFlow> getRow(Table table, TableRow row) {
        String action = table.getStringCellValue(row, OPERATION);
        action = String.valueOf(action).toLowerCase().trim();
        String description = table.getStringCellValue(row, DESCRIPTION);
        description = String.valueOf(description).toLowerCase();
        if (!action.equalsIgnoreCase(DIVIDEND_ACTION) || !description.contains("дивиденд")) {
            return emptyList();
        }

        Security security = getSecurity(table, row, CashFlowType.DIVIDEND);
        if (security == null) return emptyList();
        Instant timestamp = getReport().convertToInstant(table.getStringCellValue(row, DATE));

        SecurityEventCashFlow.SecurityEventCashFlowBuilder builder = SecurityEventCashFlow.builder()
                .isin(security.getIsin())
                .portfolio(getReport().getPortfolio())
                .count(getSecurityCount(security, timestamp))
                .eventType(CashFlowType.DIVIDEND)
                .timestamp(timestamp)
                .value(table.getCurrencyCellValue(row, VALUE))
                .currency(UralsibBrokerReport.convertToCurrency(table.getStringCellValue(row, CURRENCY)));

        Collection<SecurityEventCashFlow> data = new ArrayList<>();
        data.add(builder.build());

        BigDecimal tax = getTax(table, row).negate();
        if (tax.abs().compareTo(minValue) >= 0) {
            data.add(builder
                    .eventType(CashFlowType.TAX)
                    .value(tax)
                    .build());
        }
        return data;
    }

    private BigDecimal getTax(Table table, TableRow row) {
        String description = table.getStringCellValue(row, DESCRIPTION);
        Matcher matcher = taxInformationPattern.matcher(description.toLowerCase());
        if (matcher.find()) {
            try {
                return BigDecimal.valueOf(parseDouble(matcher.group(1)));
            } catch (Exception e) {
                log.info("Не смогу выделить сумму налога из описания: {}", description);
            }
        }
        return BigDecimal.ZERO;
    }
}
