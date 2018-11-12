package example.service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@ApiModel
public class Subscription {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "subscription_id", columnDefinition = "VARCHAR(255)")
    private UUID id;

    @NonNull
    @OneToOne
    @JoinColumn(name = "subscriber_id", nullable = false)
    @ApiModelProperty(readOnly = true)
    private Subscriber subscriber;

    @NonNull
    @ManyToOne
    @JoinColumn(name="billing_plan_id", nullable = false)
    @ApiModelProperty(readOnly = true)
    private BillingPlan billingPlan;

    @NonNull
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
    @ApiModelProperty(required = true, example = "2019-1-01@11:10:09.876-0700")
    private Date startDate;

    // canceled_at
    // billing_cycle_anchor
    // charge mechanism?
}
