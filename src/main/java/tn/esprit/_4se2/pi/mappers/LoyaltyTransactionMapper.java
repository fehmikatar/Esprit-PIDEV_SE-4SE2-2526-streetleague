package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.Loyalty.LoyaltyTransactionResponse;
import tn.esprit._4se2.pi.entities.LoyaltyTransaction;

@Component
public class LoyaltyTransactionMapper {

    public LoyaltyTransactionResponse toResponse(LoyaltyTransaction transaction) {
        return LoyaltyTransactionResponse.builder()
                .id(transaction.getId())
                .clientId(transaction.getClient().getId())
                .points(transaction.getPoints())
                .reason(transaction.getReason())
                .relatedEntityType(transaction.getRelatedEntityType())
                .relatedEntityId(transaction.getRelatedEntityId())
                .transactionDate(transaction.getTransactionDate())
                .build();
    }
}