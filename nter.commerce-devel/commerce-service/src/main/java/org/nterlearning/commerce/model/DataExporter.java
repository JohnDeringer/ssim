package org.nterlearning.commerce.model;

import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Deringer
 * Date: 8/18/11
 * Time: 12:28 PM
 */
public class DataExporter {

    public String toCommaDelimited(List<PaymentTransaction> transactions) {

        StringBuilder sb = new StringBuilder();
        List<String> columns = getColumns();

        // Headers
        int numberOfColumns = columns.size();
        int count = 0;
        for (String column : getColumns()) {
            count++;
            sb.append(column);
            if (count == numberOfColumns) {
                sb.append('\n');
            } else {
                sb.append(',');
            }
        }

        // Data
        count = 0;
        for (PaymentTransaction transaction : transactions) {
            count++;
            sb.append(transaction.getTransactionId());
            sb.append(',');
            sb.append(transaction.getPaymentProcessor());
            sb.append(',');
            sb.append(sanitizeForCsv(transaction.getItemName()));
            sb.append(',');
            sb.append(transaction.getItemNumber());
            sb.append(',');
            sb.append(transaction.getQuantity());
            sb.append(',');
            sb.append(sanitizeForCsv(transaction.getPaymentDate().toString()));
            sb.append(',');
            sb.append(sanitizeForCsv(transaction.getPaymentStatus().value()));
            sb.append(',');
            sb.append(sanitizeForCsv(transaction.getPaymentType().value()));
            sb.append(',');
            sb.append(sanitizeForCsv(transaction.getTransactionType().value()));
            sb.append(',');
            sb.append(transaction.getCurrencyType());
            sb.append(',');
            sb.append(transaction.getPaymentGross());
            sb.append(',');
            sb.append(transaction.getPaymentFee());
            sb.append(',');
            sb.append(transaction.getAdminFee());
            sb.append(',');
            sb.append(transaction.getReferrerFee());
            sb.append(',');
            sb.append(sanitizeForCsv(transaction.getPayerEmail()));
            sb.append(',');
            sb.append(sanitizeForCsv(transaction.getStudentId()));
            sb.append(',');
            sb.append(sanitizeForCsv(transaction.getPayerId()));
            sb.append(',');
            sb.append(transaction.getSysDate());
            sb.append(',');
            sb.append(sanitizeForCsv(transaction.getNterId()));
            sb.append(',');
            sb.append(sanitizeForCsv(transaction.getCourseProviderId()));

            sb.append('\n');

        }
        return sb.toString();
    }

    private List<String> getColumns() {
        List<String> columns = new ArrayList<String>();
        columns.add("\"Transaction Id\"");
        columns.add("\"Payment Processor\"");
        columns.add("\"Item Name\"");
        columns.add("\"Item Number\"");
        columns.add("\"Quantity\"");
        columns.add("\"Payment Date\"");
        columns.add("\"Payment Status\"");
        columns.add("\"Payment Type\"");
        columns.add("\"Transaction Type\"");
        columns.add("\"Currency Type\"");
        columns.add("\"Payment Gross\"");
        columns.add("\"Payment Fee\"");
        columns.add("\"Admin Fee\"");
        columns.add("\"Referrer Fee\"");
        columns.add("\"Payer Email\"");
        columns.add("\"Student Id\"");
        columns.add("\"Payer Id\"");
        columns.add("\"System Date\"");
        columns.add("\"NTER Id\"");
        columns.add("\"Course Provider Id\"");

        return columns;
    }

    private String sanitizeForCsv(String cellData) {
        if (cellData == null) {
            return "";
        }
        StringBuilder resultBuilder = new StringBuilder(cellData);

        // Look for doublequotes, escape as necessary.
        int lastIndex = 0;
        while (resultBuilder.indexOf("\"", lastIndex) >= 0) {
            int quoteIndex = resultBuilder.indexOf("\"", lastIndex);
            resultBuilder.replace(quoteIndex, quoteIndex + 1, "\"\"");
            lastIndex = quoteIndex + 2;
        }

        char firstChar = cellData.charAt(0);
        char lastChar = cellData.charAt(cellData.length() - 1);

        if (cellData.contains(",") || // Check for commas
            cellData.contains("\n") ||  // Check for line breaks
            Character.isWhitespace(firstChar) || // Check for leading whitespace.
            Character.isWhitespace(lastChar)) { // Check for trailing whitespace
            resultBuilder.insert(0, "\"").append("\""); // Wrap in doublequotes.
        }
        return resultBuilder.toString();
    }

}
