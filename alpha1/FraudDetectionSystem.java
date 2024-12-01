package alpha1;

public class FraudDetectionSystem {

    // Transaction class (inner class)
    static class Transaction {
        double amount;
        String location;
        String merchant;
        String customerUsualLocation;
        boolean isFraudulent;

        public Transaction(double amount, String location, String merchant, String customerUsualLocation) {
            this.amount = amount;
            this.location = location;
            this.merchant = merchant;
            this.customerUsualLocation = customerUsualLocation;
            this.isFraudulent = false;
        }
    }

    // Abstract FraudChecker class (inner class)
    static abstract class FraudChecker {
        protected FraudChecker nextChecker;

        public FraudChecker setNext(FraudChecker nextChecker) {
            this.nextChecker = nextChecker;
            return this.nextChecker;
        }

        public abstract void check(Transaction transaction);
    }

    // Concrete Fraud Checkers (inner classes)
    static class BasicCheck extends FraudChecker {
        @Override
        public void check(Transaction transaction) {
            if (transaction.amount > 10000) {
                System.out.println("Basic Check: Transaction flagged for large amount.");
                transaction.isFraudulent = true;
            } else if (nextChecker != null) {
                nextChecker.check(transaction);
            }
        }
    }

    static class GeographicalAnomalyCheck extends FraudChecker {
        @Override
        public void check(Transaction transaction) {
            if (!transaction.location.equals(transaction.customerUsualLocation)) {
                System.out.println("Geographical Anomaly Check: Location flagged.");
                transaction.isFraudulent = true;
            } else if (nextChecker != null) {
                nextChecker.check(transaction);
            }
        }
    }

    static class BlacklistedMerchantCheck extends FraudChecker {
        @Override
        public void check(Transaction transaction) {
            String[] blacklistedMerchants = { "fraud_merchant_1", "fraud_merchant_2" };
            for (String merchant : blacklistedMerchants) {
                if (transaction.merchant.equals(merchant)) {
                    System.out.println("Blacklisted Merchant Check: Merchant flagged.");
                    transaction.isFraudulent = true;
                    return;
                }
            }
            if (nextChecker != null) {
                nextChecker.check(transaction);
            }
        }
    }

    static class HighRiskTransactionCheck extends FraudChecker {
        @Override
        public void check(Transaction transaction) {
            if (transaction.amount > 5000 && !transaction.location.equals(transaction.customerUsualLocation)) {
                System.out.println("High-Risk Transaction Check: Transaction flagged.");
                transaction.isFraudulent = true;
            } else if (nextChecker != null) {
                nextChecker.check(transaction);
            }
        }
    }

    // Main method
    public static void main(String[] args) {
        // Sample customer profile and transaction
        Transaction transaction = new Transaction(
                12000,                // Amount
                "City_B",             // Location
                "fraud_merchant_1",   // Merchant
                "City_A"              // Customer's usual location
        );

        // Set up the chain of responsibility
        FraudChecker chain = new BasicCheck();
        chain.setNext(new GeographicalAnomalyCheck())
                .setNext(new BlacklistedMerchantCheck())
                .setNext(new HighRiskTransactionCheck());

        // Process the transaction
        chain.check(transaction);

        // Output the result
        if (transaction.isFraudulent) {
            System.out.println("Transaction is flagged as fraudulent.");
        } else {
            System.out.println("Transaction is approved.");
        }
    }
}
