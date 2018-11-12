package example.service.data;

import java.io.Serializable;
import lombok.Data;

/**
 * Used as the schema for loading example billing plans
 * from a CSV file on application start up.
 */
@Data
class SerializedBillingPlan implements Serializable {
    private String currency;
    private String basicPrice;
    private String standardPrice;
    private String premiumPrice;
    private String countries;
}
