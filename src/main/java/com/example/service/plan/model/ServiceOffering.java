package com.example.service.plan.model;

import com.google.common.collect.ImmutableSet;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Arrays;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Transient;

@Data
@Entity
@ApiModel
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"concurrent_stream_count", "highest_supported_video_definition"})
})
public class ServiceOffering {

    @Transient
    public static final Set<Integer> ALLOWABLE_CONCURRENT_STREAM_COUNTS =
            new ImmutableSet.Builder<Integer>()
                    .addAll(Arrays.asList(1, 2, 4))
                    .build();
    @Id
    @GeneratedValue
    @ApiModelProperty(hidden = true)
    private Long id;

    @ApiModelProperty(required = true)
    @Column(name = "concurrent_stream_count")
    @NonNull
    private Integer concurrentStreamCount;

    @ApiModelProperty(required = true)
    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "highest_supported_video_definition")
    private VideoDefinition highestSupportedVideoDefinition;

}
