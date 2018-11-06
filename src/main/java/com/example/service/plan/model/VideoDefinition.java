package com.example.service.plan.model;

import io.swagger.annotations.ApiModel;
import java.util.Comparator;

/**
 * Created by rrivera on 11/5/18.
 */
@ApiModel
public enum VideoDefinition {
    SD,
    HD,
    UHD;

    public static Comparator<VideoDefinition> videoDefinitionComparator = new Comparator<VideoDefinition>() {
        public int compare(VideoDefinition d1, VideoDefinition d2) {
            return d1.ordinal() - d2.ordinal();
        }
    };

}
