package com.example.Checkstyle.Switch.Analyzer;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import org.junit.jupiter.api.Test;

public class SwitchDuplicateLogicCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/example/Checkstyle/Switch/Analyzer/checkstyle";
    }

    @Test
    void testDuplicateSwitchBranches() throws Exception {
        String[] expected = {
            "5:9: Duplicate logic block detected across switch branches (filtered for false positives)."
        };

        verify(
            createModuleConfig(SwitchDuplicateLogicCheck.class),
            getPath("InputDuplicateSwitchBranchCheck.java"),
            expected
        );
    }
}