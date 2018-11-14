package example.billing.service.model;

import io.swagger.annotations.ApiModel;

@ApiModel
public enum ServiceOffering {

    BASIC(1),
    STANDARD(2),
    PREMIUM(4);

    private int concurrentStreamCount;

    ServiceOffering(int concurrentStreamCount) {
        this.concurrentStreamCount = concurrentStreamCount;
    }

}
