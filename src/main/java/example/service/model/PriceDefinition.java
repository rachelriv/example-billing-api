package example.service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;
import lombok.NonNull;
import org.joda.time.Interval;

@Data
@ApiModel
@Entity
public class PriceDefinition {

    @Id
    @GeneratedValue
    @ApiModelProperty(hidden = true)
    @Column(name = "price_definition_id")
    private Long priceDefinitionId;

    @ManyToOne
    @JoinColumn(name="billing_plan_id", nullable = false)
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private BillingPlan billingPlan;

    @NonNull
    @ApiModelProperty(required = true)
    @Enumerated(EnumType.STRING)
    private PriceType priceType;

    @NonNull
    @ApiModelProperty(required = true, notes = "The ISO-4217 currency code.", example = "USD")
    private String currencyCode;

    @NonNull
    @ApiModelProperty(required = true, example = "9.99")
    private String currencyValue;

    @NonNull
    @Temporal(TemporalType.DATE)
    @Column(name = "activation_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
    @ApiModelProperty(required = true, example = "2018-1-01@11:10:09.876-0700")
    private Date activationDate;

    @NonNull
    @Temporal(TemporalType.DATE)
    @Column(name = "expiration_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
    @ApiModelProperty(required = true, example = "2019-1-01@11:10:09.876-0700")
    private Date expirationDate;

    public boolean isOverlapping(final PriceDefinition other) {
        if (this.priceType.equals(PriceType.TRIAL) || other.priceType.equals(PriceType.TRIAL)) {
            return false;
        }
        Interval thisActivePeriod = new Interval(activationDate.getTime(), expirationDate.getTime());
        Interval otherActivePeriod = new Interval( this.getActivationDate().getTime(), other.getExpirationDate().getTime());
        return thisActivePeriod.overlaps(otherActivePeriod);
    }
}
