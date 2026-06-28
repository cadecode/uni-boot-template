package com.github.cadecode.xboot.admin.pipeline;

import com.github.cadecode.xboot.common.enums.ExtensionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PipelineTestType implements ExtensionType {
    BIZ1("BIZ1"),
    BIZ2("BIZ2"),
    BIZ3("BIZ3"),
    BIZ4("BIZ4"),
    BIZ5("BIZ5"),
    UNKNOWN("UNKNOWN"),
    ;
    private final String type;
}
