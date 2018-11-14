package example.billing.service.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.annotations.Where;

@Data
@Entity
@ApiModel(description = "Billing plans define the currency, price(s), and billing interval of a service offering for a country. " +
        "For example, you might have a $7.99/month plan for the \"Basic\" service offering in the United States. " +
        "At this time, only one plan can be defined per country/offering combination.")
@Table(
        indexes = {@Index(columnList = "serviceOffering,countryCode", unique = true)}
)
public class BillingPlan {

    @Id
    @GeneratedValue
    @Column(name = "billing_plan_id")
    @ApiModelProperty(readOnly = true, notes = "Monotonically increasing ID that is automatically generated.", example = "1")
    private Integer id;

    @NonNull
    @Enumerated(EnumType.STRING)
    @ApiModelProperty(required = true)
    private ServiceOffering serviceOffering;

    @NonNull
    @ApiModelProperty(required = true, notes = "Uppercase two-character [IS0 3166-1](https://www.iso.org/obp/ui/#search/code/) country code.", example = "US")
    @Enumerated(EnumType.STRING)
    private CountryCode countryCode;

    @NonNull
    @OneToMany(mappedBy = "billingPlan")
    @ApiModelProperty(required = true, notes = "Price definitions currently active for this billing plan.")
    @Where(clause = "activation_date < NOW() AND NOW() < expiration_date")
    private List<PriceDefinition> priceDefinitions;

    @Enumerated(EnumType.STRING)
    @ApiModelProperty(readOnly = true, notes = "Monthly billing intervals are the only supported interval at this time.")
    private final Interval interval = Interval.MONTH;

}
