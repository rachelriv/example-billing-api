package example.service.model;

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
@ApiModel
@Table(
        indexes = {@Index(columnList = "serviceOffering,countryCode", unique = true)}
)
public class BillingPlan {

    @Id
    @GeneratedValue
    @Column(name = "billing_plan_id")
    @ApiModelProperty(readOnly = true)
    private Integer id;

    @NonNull
    @Enumerated(EnumType.STRING)
    private ServiceOffering serviceOffering;

    @NonNull
    @ApiModelProperty(notes = "two-character IS0-3166-1 country codes", example = "US", required = true)
    @Enumerated(EnumType.STRING)
    private CountryCode countryCode;

    @NonNull
    @OneToMany(mappedBy = "billingPlan")
    @ApiModelProperty(notes = "price definitions", required = true)
    @Where(clause = "activation_date < NOW() AND NOW() < expiration_date")
    private List<PriceDefinition> priceDefinitions;

}
