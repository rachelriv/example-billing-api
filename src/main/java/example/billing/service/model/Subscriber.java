package example.billing.service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Data;
import lombok.NonNull;

@Data
@Entity
@ApiModel
public class Subscriber {
    @Id
    @GeneratedValue
    @ApiModelProperty(readOnly = true)
    @Column(name = "subscriber_id")
    private Integer id;

    @ApiModelProperty(example = "John")
    private String firstName;

    @ApiModelProperty(example = "Smith")
    private String lastName;

    @NonNull
    @ApiModelProperty(example = "johnsmith@email.com", required = true)
    private String email;

    @NonNull
    @ApiModelProperty(notes = "two-character IS0-3166-1 country codes", example = "US", required = true)
    @Enumerated(EnumType.STRING)
    private CountryCode countryCode;

    @OneToOne
    @JoinColumn(name = "subscription_id")
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private Subscription subscription;

}
